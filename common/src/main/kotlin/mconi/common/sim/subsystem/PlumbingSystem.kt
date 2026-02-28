package mconi.common.sim.subsystem

import mconi.common.sim.conduit.OniConduitAccess
import mconi.common.sim.conduit.OniConduitFlow
import mconi.common.sim.conduit.OniConduitNetworkBuilder

class PlumbingSystem : OniSystem {
    override fun id(): String = "plumbing"

    override fun run(context: SystemContext) {
        val level = context.level()
        val config = context.config()
        val builder = OniConduitNetworkBuilder(level, OniConduitAccess::isLiquidConduit)
        val networks = builder.build(config.worldSampleRadiusBlocks(), config.cellSize())
        for (network in networks) {
            val contentsByPos: MutableMap<net.minecraft.core.BlockPos, OniConduitFlow.Contents> = HashMap()
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
                entity.setTemperatureK(contents.temperatureK)
            }
        }
    }

    companion object {
        private const val MAX_LIQUID_MASS = 1000.0
        private const val MOVE_PER_STEP = 200.0
    }
}
