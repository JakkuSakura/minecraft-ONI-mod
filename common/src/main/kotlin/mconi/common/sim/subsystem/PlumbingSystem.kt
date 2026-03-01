package mconi.common.sim.subsystem

import mconi.common.block.OniBlockFactory
import mconi.common.block.OniBlockLookup
import mconi.common.element.OniElements
import mconi.common.sim.conduit.OniConduitAccess
import mconi.common.sim.conduit.OniConduitFlow
import mconi.common.sim.conduit.OniConduitNetworkBuilder
import mconi.common.world.OniMatterAccess
import mconi.common.world.OniWorldScan
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks

class PlumbingSystem : OniSystem {
    override fun id(): String = "plumbing"

    override fun run(context: SystemContext) {
        val level = context.level()
        val config = context.config()
        val radius = config.worldSampleRadiusBlocks()
        val cellSize = config.cellSize()
        val positions = OniWorldScan.positionsAroundPlayers(level, radius, cellSize)

        handlePumps(context, positions)

        val builder = OniConduitNetworkBuilder(level, OniConduitAccess::isLiquidConduit)
        val networks = builder.build(radius, cellSize)
        for (network in networks) {
            val contentsByPos: MutableMap<BlockPos, OniConduitFlow.Contents> = HashMap()
            for (pos in network) {
                val entity = OniConduitAccess.conduitEntity(level, pos) ?: continue
                contentsByPos[pos] = OniConduitFlow.Contents(
                    elementId = OniConduitAccess.resolveElementIdForLiquid(entity.elementId()),
                    mass = entity.mass(),
                    temperatureK = entity.temperatureK()
                )
            }
            val result = OniConduitFlow.step(
                network,
                contentsByPos,
                OniConduitFlow.FlowConfig(MAX_LIQUID_MASS, MOVE_PER_STEP)
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
        private const val MAX_LIQUID_MASS = 1000.0
        private const val MOVE_PER_STEP = 200.0
        private const val PUMP_RATE = 100.0
        private const val VENT_RATE = 100.0
    }

    private fun handlePumps(context: SystemContext, positions: List<BlockPos>) {
        val level = context.level()
        val power = context.runtime().powerState()
        for (pos in positions) {
            val state = level.getBlockState(pos)
            if (!OniConduitAccess.isLiquidPump(state)) {
                continue
            }
            if (!power.isConsumerPowered(pos)) {
                continue
            }
            val intakePos = pos.below()
            val intakeState = level.getBlockState(intakePos)
            val liquidId = OniMatterAccess.liquidId(intakeState) ?: continue
            val worldEntity = OniMatterAccess.matterEntity(level, intakePos) ?: continue
            val conduitPos = findAdjacentConduit(level, pos, OniConduitAccess::isLiquidConduit) ?: continue
            val conduitEntity = OniConduitAccess.conduitEntity(level, conduitPos) ?: continue
            val conduitElement = OniConduitAccess.resolveElementIdForLiquid(conduitEntity.elementId())
            if (conduitElement != null && conduitElement != liquidId) {
                continue
            }
            val available = worldEntity.mass()
            if (available <= 0.0) {
                continue
            }
            val capacity = MAX_LIQUID_MASS - conduitEntity.mass()
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
            conduitEntity.setElementId(liquidId)
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
            if (!OniConduitAccess.isLiquidVent(state)) {
                continue
            }
            val conduitPos = findAdjacentConduit(level, pos, OniConduitAccess::isLiquidConduit) ?: continue
            val conduitEntity = OniConduitAccess.conduitEntity(level, conduitPos) ?: continue
            val liquidId = OniConduitAccess.resolveElementIdForLiquid(conduitEntity.elementId()) ?: continue
            val available = conduitEntity.mass()
            if (available <= 0.0) {
                continue
            }
            val outputPos = pos.below()
            val outputState = level.getBlockState(outputPos)
            val outputLiquid = OniMatterAccess.liquidId(outputState)
            if (!outputState.isAir && outputLiquid != liquidId) {
                continue
            }
            if (outputState.isAir) {
                val target = blockStateForLiquid(liquidId)
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
            targetEntity.ensureContents(liquidId, total, newTemp)

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

    private fun blockStateForLiquid(liquidId: String): net.minecraft.world.level.block.state.BlockState {
        return when (liquidId) {
            OniElements.LIQUID_WATER -> OniBlockLookup.state(OniBlockFactory.WATER)
            OniElements.LIQUID_POLLUTED_WATER -> OniBlockLookup.state(OniBlockFactory.POLLUTED_WATER)
            OniElements.LIQUID_CRUDE_OIL -> OniBlockLookup.state(OniBlockFactory.CRUDE_OIL)
            OniElements.LIQUID_LAVA -> OniBlockLookup.state(OniBlockFactory.LAVA)
            OniElements.LIQUID_SALT_WATER -> OniBlockLookup.state(OniBlockFactory.SALT_WATER)
            OniElements.LIQUID_BRINE -> OniBlockLookup.state(OniBlockFactory.BRINE)
            OniElements.LIQUID_ETHANOL -> OniBlockLookup.state(OniBlockFactory.ETHANOL)
            OniElements.LIQUID_PETROLEUM -> OniBlockLookup.state(OniBlockFactory.PETROLEUM)
            OniElements.LIQUID_MILK -> OniBlockLookup.state(OniBlockFactory.MILK)
            OniElements.LIQUID_NATURAL_RESIN -> OniBlockLookup.state(OniBlockFactory.NATURAL_RESIN)
            OniElements.LIQUID_PHYTO_OIL -> OniBlockLookup.state(OniBlockFactory.PHYTO_OIL)
            OniElements.LIQUID_MOLTEN_GLASS -> OniBlockLookup.state(OniBlockFactory.MOLTEN_GLASS)
            OniElements.LIQUID_SUPER_COOLANT -> OniBlockLookup.state(OniBlockFactory.SUPER_COOLANT)
            OniElements.LIQUID_VISCO_GEL -> OniBlockLookup.state(OniBlockFactory.VISCO_GEL)
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
