package mconi.common.sim.power

import mconi.common.sim.OniSystemConfig
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import java.util.ArrayDeque
import kotlin.math.abs

class OniPowerNetworkBuilder(
    private val world: PowerWorldView,
    private val config: OniSystemConfig,
    private val catalog: PowerCatalog,
    private val existingBatteryEnergy: Map<Long, Double>,
) {
    data class BuildResult(
        val networks: List<OniPowerNetwork>,
        val consumerPoweredByPos: Set<Long>,
        val batteryEnergyByPos: Map<Long, Double>,
        val totalGenerationW: Double,
        val totalDemandW: Double,
        val totalStoredEnergyJ: Double,
        val tripped: Boolean,
    )

    fun build(): BuildResult {
        val wirePositions = HashSet<Long>()
        val generatorPositions = HashMap<Long, Double>()
        val consumerPositions = HashMap<Long, Double>()
        val batteryPositions = HashMap<Long, Double>()
        val transformerPositions = HashMap<Long, Double>()

        val visited = HashSet<Long>()
        val radius = config.worldSampleRadiusBlocks()
        val minY = world.minY
        val maxY = world.maxY
        val cursor = BlockPos.MutableBlockPos()

        // Sample a cube around each player and collect candidate power blocks.
        for (playerPos in world.players()) {
            val center = playerPos
            var x = center.x - radius
            while (x <= center.x + radius) {
                var y = center.y - radius
                while (y <= center.y + radius) {
                    if (y < minY || y > maxY) {
                        y++
                        continue
                    }
                    var z = center.z - radius
                    while (z <= center.z + radius) {
                        val key = BlockPos.asLong(x, y, z)
                        if (!visited.add(key)) {
                            z++
                            continue
                        }
                        cursor.set(x, y, z)
                        val state = world.getBlockState(cursor)
                        val wireCapacity = catalog.wireCapacity(state)
                        if (wireCapacity != null) {
                            wirePositions.add(key)
                            z++
                            continue
                        }
                        val gen = catalog.generatorOutput(state)
                        if (gen != null) {
                            generatorPositions[key] = gen
                            z++
                            continue
                        }
                        val demand = catalog.consumerDemand(state)
                        if (demand != null) {
                            consumerPositions[key] = demand
                            z++
                            continue
                        }
                        val batteryCap = catalog.batteryCapacity(state)
                        if (batteryCap != null) {
                            batteryPositions[key] = batteryCap
                            z++
                            continue
                        }
                        val throughput = catalog.transformerThroughput(state)
                        if (throughput != null) {
                            transformerPositions[key] = throughput
                            z++
                            continue
                        }
                        z++
                    }
                    y++
                }
                x++
            }
        }

        // Build wire connected components (circuits) using 6-direction adjacency.
        val posToCircuit = HashMap<Long, Int>()
        val circuits = ArrayList<CircuitBuilder>()
        val queue = ArrayDeque<Long>()

        for (pos in wirePositions) {
            if (posToCircuit.containsKey(pos)) {
                continue
            }
            val circuitId = circuits.size
            val circuit = CircuitBuilder(circuitId)
            circuits.add(circuit)
            posToCircuit[pos] = circuitId
            queue.add(pos)
            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                circuit.wirePositions.add(current)
                val currentPos = BlockPos.of(current)
                val state = world.getBlockState(currentPos)
                val cap = catalog.wireCapacity(state)
                if (cap != null) {
                    circuit.wireCapacityW = minOf(circuit.wireCapacityW, cap)
                }
                for (dir in Direction.values()) {
                    val nx = currentPos.x + dir.stepX
                    val ny = currentPos.y + dir.stepY
                    val nz = currentPos.z + dir.stepZ
                    val neighborKey = BlockPos.asLong(nx, ny, nz)
                    if (!wirePositions.contains(neighborKey)) {
                        continue
                    }
                    if (posToCircuit.putIfAbsent(neighborKey, circuitId) == null) {
                        queue.add(neighborKey)
                    }
                }
            }
            if (!circuit.wireCapacityW.isFinite()) {
                circuit.wireCapacityW = 0.0
            }
        }

        // Create transformer links between the two strongest adjacent circuits.
        val transformerLinks = ArrayList<TransformerEdge>()

        for ((pos, throughput) in transformerPositions) {
            val adjacentCircuits = adjacentCircuitIds(pos, posToCircuit)
            if (adjacentCircuits.size < 2) {
                continue
            }
            val sorted = adjacentCircuits
                .map { id -> id to circuits[id].wireCapacityW }
                .sortedByDescending { it.second }
            val a = sorted[0].first
            val b = sorted[1].first
            transformerLinks.add(TransformerEdge(BlockPos.of(pos), a, b, throughput))
        }

        for ((pos, outputW) in generatorPositions) {
            val circuitId = chooseCircuit(adjacentCircuitIds(pos, posToCircuit), circuits) ?: continue
            circuits[circuitId].generationW += outputW
            circuits[circuitId].generatorPositions.add(pos)
        }

        for ((pos, demandW) in consumerPositions) {
            val circuitId = chooseCircuit(adjacentCircuitIds(pos, posToCircuit), circuits) ?: continue
            circuits[circuitId].demandW += demandW
            circuits[circuitId].consumerPositions.add(pos)
            circuits[circuitId].consumerDemandByPos[pos] = demandW
        }

        for ((pos, capacityJ) in batteryPositions) {
            val circuitId = chooseCircuit(adjacentCircuitIds(pos, posToCircuit), circuits) ?: continue
            val energy = existingBatteryEnergy[pos] ?: 0.0
            circuits[circuitId].batteryPositions.add(pos)
            circuits[circuitId].batteryCapacityByPos[pos] = capacityJ
            circuits[circuitId].batteryEnergyByPos[pos] = energy.coerceIn(0.0, capacityJ)
        }

        val tickSeconds = config.tickInterval().toDouble()
        // Net power per circuit before transformer exchange.
        val netW = DoubleArray(circuits.size)
        for (circuit in circuits) {
            netW[circuit.id] = circuit.generationW - circuit.demandW
        }

        // Move energy across transformer links respecting throughput.
        val appliedTransfers = ArrayList<OniPowerNetwork.TransformerLink>()
        for (edge in transformerLinks) {
            val a = edge.circuitA
            val b = edge.circuitB
            val cap = edge.capacityW
            val transfer: Double = when {
                netW[a] > 0 && netW[b] < 0 -> minOf(netW[a], -netW[b], cap)
                netW[b] > 0 && netW[a] < 0 -> -minOf(netW[b], -netW[a], cap)
                else -> 0.0
            }
            if (abs(transfer) > 1e-9) {
                netW[a] -= transfer
                netW[b] += transfer
            }
            appliedTransfers.add(
                OniPowerNetwork.TransformerLink(
                    pos = edge.pos,
                    circuitA = a,
                    circuitB = b,
                    capacityW = cap,
                    transferW = transfer
                )
            )
        }

        // Update battery energy and mark powered consumers.
        val nextBatteryEnergy = HashMap<Long, Double>()
        val poweredConsumers = HashSet<Long>()
        var totalGenerationW = 0.0
        var totalDemandW = 0.0
        var totalStoredEnergyJ = 0.0
        var tripped = false

        val builtCircuits = ArrayList<OniPowerNetwork.Circuit>(circuits.size)
        for (circuit in circuits) {
            val currentEnergy = circuit.batteryEnergyByPos.values.sum()
            val capacity = circuit.batteryCapacityByPos.values.sum()
            val net = netW[circuit.id]
            val energyDelta = net * tickSeconds
            val nextEnergy = (currentEnergy + energyDelta).coerceIn(0.0, capacity)

            distributeBatteryEnergy(circuit, nextEnergy, nextBatteryEnergy)

            val overload = circuit.demandW > circuit.wireCapacityW && circuit.wireCapacityW > 0.0
            val deficitJ = (-net).coerceAtLeast(0.0) * tickSeconds
            val deficit = net < 0 && deficitJ > currentEnergy + 1e-9
            val powered = !overload && !deficit
            if (!powered) {
                tripped = true
            }
            if (powered) {
                poweredConsumers.addAll(circuit.consumerPositions)
            }

            totalGenerationW += circuit.generationW
            totalDemandW += circuit.demandW
            totalStoredEnergyJ += nextEnergy

            builtCircuits.add(
                OniPowerNetwork.Circuit(
                    id = circuit.id,
                    wireCapacityW = circuit.wireCapacityW,
                    generationW = circuit.generationW,
                    demandW = circuit.demandW,
                    batteryCapacityJ = capacity,
                    batteryEnergyJ = nextEnergy,
                    powered = powered,
                    overloaded = overload,
                )
            )
        }

        val networks = splitNetworks(builtCircuits, appliedTransfers)
        return BuildResult(
            networks = networks,
            consumerPoweredByPos = poweredConsumers,
            batteryEnergyByPos = nextBatteryEnergy,
            totalGenerationW = totalGenerationW,
            totalDemandW = totalDemandW,
            totalStoredEnergyJ = totalStoredEnergyJ,
            tripped = tripped,
        )
    }

    private fun adjacentCircuitIds(pos: Long, posToCircuit: Map<Long, Int>): Set<Int> {
        val result = LinkedHashSet<Int>()
        val base = BlockPos.of(pos)
        for (dir in Direction.values()) {
            val nx = base.x + dir.stepX
            val ny = base.y + dir.stepY
            val nz = base.z + dir.stepZ
            val key = BlockPos.asLong(nx, ny, nz)
            val id = posToCircuit[key] ?: continue
            result.add(id)
        }
        return result
    }

    private fun chooseCircuit(adjacent: Set<Int>, circuits: List<CircuitBuilder>): Int? {
        if (adjacent.isEmpty()) {
            return null
        }
        if (adjacent.size == 1) {
            return adjacent.first()
        }
        var bestId = -1
        var bestCapacity = -1.0
        for (id in adjacent) {
            val cap = circuits[id].wireCapacityW
            if (cap > bestCapacity) {
                bestCapacity = cap
                bestId = id
            }
        }
        return if (bestId >= 0) bestId else adjacent.first()
    }

    private fun distributeBatteryEnergy(
        circuit: CircuitBuilder,
        totalEnergy: Double,
        target: MutableMap<Long, Double>
    ) {
        val totalCapacity = circuit.batteryCapacityByPos.values.sum()
        if (totalCapacity <= 0.0) {
            return
        }
        for ((pos, capacity) in circuit.batteryCapacityByPos) {
            val share = (capacity / totalCapacity).coerceIn(0.0, 1.0)
            val energy = (totalEnergy * share).coerceIn(0.0, capacity)
            target[pos] = energy
        }
    }

    private data class TransformerEdge(
        val pos: BlockPos,
        val circuitA: Int,
        val circuitB: Int,
        val capacityW: Double,
    )

    private fun splitNetworks(
        circuits: List<OniPowerNetwork.Circuit>,
        links: List<OniPowerNetwork.TransformerLink>
    ): List<OniPowerNetwork> {
        if (circuits.isEmpty()) {
            return emptyList()
        }
        val adjacency: MutableMap<Int, MutableSet<Int>> = HashMap()
        for (circuit in circuits) {
            adjacency[circuit.id] = HashSet()
        }
        for (link in links) {
            adjacency[link.circuitA]?.add(link.circuitB)
            adjacency[link.circuitB]?.add(link.circuitA)
        }
        val visited = HashSet<Int>()
        val networks = ArrayList<OniPowerNetwork>()
        val queue = ArrayDeque<Int>()

        for (circuit in circuits) {
            if (!visited.add(circuit.id)) {
                continue
            }
            val circuitIds = ArrayList<Int>()
            queue.add(circuit.id)
            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                circuitIds.add(current)
                val neighbors = adjacency[current] ?: emptySet()
                for (neighbor in neighbors) {
                    if (visited.add(neighbor)) {
                        queue.add(neighbor)
                    }
                }
            }

            val networkCircuits = circuits.filter { circuitIds.contains(it.id) }
            val networkLinks = links.filter { circuitIds.contains(it.circuitA) && circuitIds.contains(it.circuitB) }
            val consumerPowered = emptySet<Long>()
            val batteryEnergy = emptyMap<Long, Double>()
            var generationW = 0.0
            var demandW = 0.0
            var storedEnergyJ = 0.0
            var tripped = false
            for (entry in networkCircuits) {
                generationW += entry.generationW
                demandW += entry.demandW
                storedEnergyJ += entry.batteryEnergyJ
                tripped = tripped || !entry.powered
            }
            networks.add(
                OniPowerNetwork(
                    circuits = networkCircuits,
                    transformerLinks = networkLinks,
                    consumerPoweredByPos = consumerPowered,
                    batteryEnergyByPos = batteryEnergy,
                    totalGenerationW = generationW,
                    totalDemandW = demandW,
                    totalStoredEnergyJ = storedEnergyJ,
                    tripped = tripped,
                )
            )
        }
        return networks
    }

    private class CircuitBuilder(val id: Int) {
        val wirePositions: MutableSet<Long> = HashSet()
        var wireCapacityW: Double = Double.POSITIVE_INFINITY
        var generationW: Double = 0.0
        var demandW: Double = 0.0
        val generatorPositions: MutableSet<Long> = HashSet()
        val consumerPositions: MutableSet<Long> = HashSet()
        val consumerDemandByPos: MutableMap<Long, Double> = HashMap()
        val batteryPositions: MutableSet<Long> = HashSet()
        val batteryCapacityByPos: MutableMap<Long, Double> = HashMap()
        val batteryEnergyByPos: MutableMap<Long, Double> = HashMap()
    }
}
