package conservecraft.common.sim.subsystem

import conservecraft.common.block.OniBlockFactory
import conservecraft.common.block.OniBlockLookup
import conservecraft.common.element.OniElements
import conservecraft.common.sim.conduit.OniConduitAccess
import conservecraft.common.sim.conduit.OniConduitFlow
import conservecraft.common.sim.conduit.OniConduitNetworkBuilder
import conservecraft.common.world.OniMatterAccess
import conservecraft.common.world.OniWorldScan
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks

class VentilationSystem : OniSystem {
    override fun id(): String = "ventilation"

    override fun run(context: SystemContext) {
        val level = context.level()
        val config = context.config()
        val radius = config.worldSampleRadiusBlocks()
        val cellSize = config.cellSize()
        val positions = OniWorldScan.positionsAroundPlayers(level, radius, cellSize)

        handlePumps(context, positions)

        val builder = OniConduitNetworkBuilder(level, OniConduitAccess::isGasConduit)
        val networks = builder.build(radius, cellSize)
        for (network in networks) {
            val contentsByPos: MutableMap<BlockPos, OniConduitFlow.Contents> = HashMap()
            for (pos in network) {
                val entity = OniConduitAccess.conduitEntity(level, pos) ?: continue
                contentsByPos[pos] = OniConduitFlow.Contents(
                    elementId = OniConduitAccess.resolveElementIdForGas(entity.elementId()),
                    mass = entity.mass(),
                    temperatureK = entity.temperatureK()
                )
            }
            val result = OniConduitFlow.step(
                network,
                contentsByPos,
                OniConduitFlow.FlowConfig(MAX_GAS_MASS, MOVE_PER_STEP)
            )
            for (pos in network) {
                val entity = OniConduitAccess.conduitEntity(level, pos) ?: continue
                val contents = result.contentsByPos[pos] ?: continue
                entity.setElementId(contents.elementId)
                entity.setMass(contents.mass)
                entity.setContentsTemperatureK(contents.temperatureK)
            }
        }

        handleVents(context, positions)
    }

    companion object {
        private const val MAX_GAS_MASS = 1.0
        private const val MOVE_PER_STEP = 0.25
        private const val PUMP_RATE = 0.25
        private const val VENT_RATE = 0.25
    }

    private fun handlePumps(context: SystemContext, positions: List<BlockPos>) {
        val level = context.level()
        val power = context.runtime().powerState()
        for (pos in positions) {
            val state = level.getBlockState(pos)
            if (!OniConduitAccess.isGasPump(state)) {
                continue
            }
            if (!power.isConsumerPowered(pos)) {
                continue
            }
            val intakePos = pos.above()
            val intakeState = level.getBlockState(intakePos)
            val gasSpec = OniMatterAccess.gasSpec(intakeState) ?: continue
            val worldEntity = OniMatterAccess.matterEntity(level, intakePos) ?: continue
            val conduitPos = findAdjacentConduit(level, pos, OniConduitAccess::isGasConduit) ?: continue
            val conduitEntity = OniConduitAccess.conduitEntity(level, conduitPos) ?: continue
            val conduitElement = OniConduitAccess.resolveElementIdForGas(conduitEntity.elementId())
            if (conduitElement != null && conduitElement != gasSpec.id) {
                continue
            }
            val available = worldEntity.mass()
            if (available <= 0.0) {
                continue
            }
            val capacity = MAX_GAS_MASS - conduitEntity.mass()
            if (capacity <= 0.0) {
                continue
            }
            val transfer = minOf(PUMP_RATE, available, capacity)
            if (transfer <= 0.0) {
                continue
            }
            val newConduitMass = conduitEntity.mass() + transfer
            val newTemp = mixTemperature(
                conduitEntity.temperatureK(),
                conduitEntity.mass(),
                worldEntity.temperatureK(),
                transfer
            )
            conduitEntity.setElementId(gasSpec.id)
            conduitEntity.setMass(newConduitMass)
            conduitEntity.setContentsTemperatureK(newTemp)

            val remaining = available - transfer
            if (remaining <= 0.0) {
                level.setBlock(intakePos, Blocks.AIR.defaultBlockState(), 2)
            } else {
                worldEntity.setMass(remaining)
            }
        }
    }

    private fun handleVents(context: SystemContext, positions: List<BlockPos>) {
        val level = context.level()
        for (pos in positions) {
            val state = level.getBlockState(pos)
            if (!OniConduitAccess.isGasVent(state)) {
                continue
            }
            val conduitPos = findAdjacentConduit(level, pos, OniConduitAccess::isGasConduit) ?: continue
            val conduitEntity = OniConduitAccess.conduitEntity(level, conduitPos) ?: continue
            val elementId = OniConduitAccess.resolveElementIdForGas(conduitEntity.elementId()) ?: continue
            val available = conduitEntity.mass()
            if (available <= 0.0) {
                continue
            }
            val outputPos = pos.above()
            val outputState = level.getBlockState(outputPos)
            val outputGas = OniMatterAccess.gasSpec(outputState)
            if (!outputState.isAir && outputGas?.id != elementId) {
                continue
            }
            if (outputState.isAir) {
                val target = blockStateForGas(elementId)
                level.setBlock(outputPos, target, 2)
            }
            val targetEntity = OniMatterAccess.matterEntity(level, outputPos) ?: continue
            val transfer = minOf(VENT_RATE, available)
            val newTemp = mixTemperature(
                targetEntity.temperatureK(),
                targetEntity.mass(),
                conduitEntity.temperatureK(),
                transfer
            )
            val total = targetEntity.mass() + transfer
            targetEntity.ensureContents(elementId, total, newTemp)

            val remaining = available - transfer
            conduitEntity.setMass(remaining)
            if (remaining <= 0.0) {
                conduitEntity.setElementId(null)
            }
        }
    }

    private fun findAdjacentConduit(
        level: net.minecraft.server.level.ServerLevel,
        pos: BlockPos,
        isConduit: (net.minecraft.world.level.block.state.BlockState) -> Boolean
    ): BlockPos? {
        val neighbors = listOf(
            pos.north(),
            pos.south(),
            pos.west(),
            pos.east(),
            pos.above(),
            pos.below()
        )
        for (neighbor in neighbors) {
            if (isConduit(level.getBlockState(neighbor))) {
                return neighbor
            }
        }
        return null
    }

    private fun blockStateForGas(elementId: String): net.minecraft.world.level.block.state.BlockState {
        val spec = OniElements.parseGas(elementId) ?: return Blocks.AIR.defaultBlockState()
        return when (spec.id) {
            OniElements.GAS_OXYGEN.id -> OniBlockLookup.state(OniBlockFactory.OXYGEN_GAS)
            OniElements.GAS_CARBON_DIOXIDE.id -> OniBlockLookup.state(OniBlockFactory.CARBON_DIOXIDE_GAS)
            OniElements.GAS_HYDROGEN.id -> OniBlockLookup.state(OniBlockFactory.HYDROGEN_GAS)
            OniElements.GAS_METHANE.id -> OniBlockLookup.state(OniBlockFactory.METHANE_GAS)
            OniElements.GAS_STEAM.id -> OniBlockLookup.state(OniBlockFactory.STEAM_GAS)
            OniElements.GAS_CHLORINE.id -> OniBlockLookup.state(OniBlockFactory.CHLORINE_GAS)
            else -> Blocks.AIR.defaultBlockState()
        }
    }

    private fun mixTemperature(
        baseTemp: Double,
        baseMass: Double,
        incomingTemp: Double,
        incomingMass: Double
    ): Double {
        if (incomingMass <= 0.0) {
            return baseTemp
        }
        val total = baseMass + incomingMass
        if (total <= 0.0) {
            return baseTemp
        }
        return (baseTemp * baseMass + incomingTemp * incomingMass) / total
    }
}
