package mconi.common.sim.subsystem

import mconi.common.element.OniElements
import mconi.common.sim.model.OccupancyState
import mconi.common.world.OniChunkDataAccess
import net.minecraft.core.BlockPos

class LiquidSubsystem : SimulationSubsystem {
    override fun id(): String = "liquid"

    override fun run(context: SimulationContext) {
        val level = context.level()
        val maxTransfer = context.config().liquidTransferKgPerStep().coerceAtLeast(0.0)
        val voidDrain = context.config().voidLiquidDrainFraction().coerceIn(0.0, 1.0)

        val entries = OniChunkDataAccess.blockEntries(level)
        val massByPos: MutableMap<BlockPos, Double> = HashMap(entries.size)
        val speciesByPos: MutableMap<BlockPos, String> = HashMap(entries.size)
        val occupancyByPos: MutableMap<BlockPos, OccupancyState> = HashMap(entries.size)
        val pendingSpeciesByPos: MutableMap<BlockPos, String?> = HashMap(entries.size)

        for (entry in entries) {
            val coordinate = entry.pos
            val cell = entry.data
            val occupancy = cell.occupancyState()
            occupancyByPos[coordinate] = occupancy
            if (occupancy == OccupancyState.SOLID) {
                continue
            }

            var species = cell.liquidId()
            var mass = cell.liquidMassKg()
            if (species == OniElements.LIQUID_NONE || mass <= 0.0) {
                species = OniElements.LIQUID_NONE
                mass = 0.0
            }

            if (occupancy == OccupancyState.VOID && species != OniElements.LIQUID_NONE) {
                mass = mass * (1.0 - voidDrain)
            }

            if (mass <= 0.0) {
                species = OniElements.LIQUID_NONE
                mass = 0.0
            }

            massByPos[coordinate] = mass
            speciesByPos[coordinate] = species
            pendingSpeciesByPos[coordinate] = if (species == OniElements.LIQUID_NONE) null else species
        }

        val deltas: MutableMap<BlockPos, MutableMap<String, Double>> = HashMap(entries.size)
        for (entry in entries) {
            val coordinate = entry.pos
            val occupancy = occupancyByPos[coordinate] ?: continue
            if (occupancy == OccupancyState.SOLID) {
                continue
            }
            val species = speciesByPos[coordinate] ?: OniElements.LIQUID_NONE
            if (species == OniElements.LIQUID_NONE) {
                continue
            }
            val mass = massByPos[coordinate] ?: 0.0
            if (mass <= 0.0) {
                continue
            }

            val below = BlockPos(coordinate.x, coordinate.y - 1, coordinate.z)
            val belowOccupancy = occupancyByPos[below]
            if (belowOccupancy != null && belowOccupancy != OccupancyState.SOLID) {
                val belowSpecies = speciesByPos[below] ?: OniElements.LIQUID_NONE
                if (belowSpecies == OniElements.LIQUID_NONE || belowSpecies == species) {
                    val belowMass = massByPos[below] ?: 0.0
                    val capacity = MAX_LIQUID_MASS_PER_CELL_KG - belowMass
                    if (capacity > 0.0) {
                        val transfer = minOf(maxTransfer, mass, capacity)
                        if (transfer > 0.0 && canReceive(pendingSpeciesByPos, below, belowSpecies, species)) {
                            addDelta(deltas, coordinate, species, -transfer)
                            addDelta(deltas, below, species, transfer)
                            continue
                        }
                    }
                }
            }

            for (neighbor in lateralNeighbors(coordinate)) {
                val otherOccupancy = occupancyByPos[neighbor] ?: continue
                if (otherOccupancy == OccupancyState.SOLID) {
                    continue
                }
                val otherSpecies = speciesByPos[neighbor] ?: OniElements.LIQUID_NONE
                if (otherSpecies != OniElements.LIQUID_NONE && otherSpecies != species) {
                    continue
                }
                val otherMass = massByPos[neighbor] ?: 0.0
                val diff = mass - otherMass
                if (diff <= 1.0) {
                    continue
                }
                val transfer = minOf(maxTransfer, diff * 0.25, MAX_LIQUID_MASS_PER_CELL_KG - otherMass)
                if (transfer <= 0.0) {
                    continue
                }
                if (!canReceive(pendingSpeciesByPos, neighbor, otherSpecies, species)) {
                    continue
                }
                addDelta(deltas, coordinate, species, -transfer)
                addDelta(deltas, neighbor, species, transfer)
            }
        }

        for (entry in entries) {
            val coordinate = entry.pos
            val cell = entry.data
            val baseSpecies = speciesByPos[coordinate] ?: OniElements.LIQUID_NONE
            val baseMass = massByPos[coordinate] ?: 0.0
            val deltaMap = deltas[coordinate]
            if (deltaMap == null || deltaMap.isEmpty()) {
                if (baseSpecies == OniElements.LIQUID_NONE) {
                    cell.setLiquidState(OniElements.LIQUID_NONE, 0.0)
                } else {
                    cell.setLiquidState(baseSpecies, baseMass)
                }
                continue
            }

            var appliedSpecies = baseSpecies
            var nextMass = baseMass
            for ((species, delta) in deltaMap) {
                if (delta == 0.0) {
                    continue
                }
                if (appliedSpecies == OniElements.LIQUID_NONE && delta > 0.0) {
                    appliedSpecies = species
                }
                if (species != appliedSpecies) {
                    continue
                }
                nextMass += delta
            }

            if (nextMass <= 0.0) {
                cell.setLiquidState(OniElements.LIQUID_NONE, 0.0)
            } else {
                cell.setLiquidState(appliedSpecies, nextMass)
            }
        }
    }

    companion object {
        private const val MAX_LIQUID_MASS_PER_CELL_KG = 4000.0
    }

    private fun lateralNeighbors(coordinate: BlockPos): List<BlockPos> {
        return listOf(
            BlockPos(coordinate.x + 1, coordinate.y, coordinate.z),
            BlockPos(coordinate.x - 1, coordinate.y, coordinate.z),
            BlockPos(coordinate.x, coordinate.y, coordinate.z + 1),
            BlockPos(coordinate.x, coordinate.y, coordinate.z - 1),
        )
    }

    private fun addDelta(
        deltas: MutableMap<BlockPos, MutableMap<String, Double>>,
        coordinate: BlockPos,
        species: String,
        delta: Double,
    ) {
        val map = deltas.computeIfAbsent(coordinate) { HashMap() }
        map[species] = (map[species] ?: 0.0) + delta
    }

    private fun canReceive(
        pendingSpeciesByPos: MutableMap<BlockPos, String?>,
        coordinate: BlockPos,
        baseSpecies: String,
        incomingSpecies: String,
    ): Boolean {
        if (baseSpecies != OniElements.LIQUID_NONE && baseSpecies != incomingSpecies) {
            return false
        }
        val pending = pendingSpeciesByPos[coordinate]
        if (pending == null) {
            pendingSpeciesByPos[coordinate] = incomingSpecies
            return true
        }
        return pending == incomingSpecies
    }
}
