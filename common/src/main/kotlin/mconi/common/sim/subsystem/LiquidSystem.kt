package mconi.common.sim.subsystem

import mconi.common.element.OniElements
import mconi.common.world.OniMatterAccess
import mconi.common.world.OniWorldScan
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks

class LiquidSystem : OniSystem {
    override fun id(): String = "liquid"

    override fun run(context: SystemContext) {
        val level = context.level()
        val config = context.config()
        val maxTransfer = config.liquidTransferPerStep().coerceAtLeast(0.0)
        if (maxTransfer <= 0.0) {
            return
        }
        val radius = config.worldSampleRadiusBlocks()
        val cellSize = config.cellSize()
        val positions = OniWorldScan.positionsAroundPlayers(level, radius, cellSize)

        val cells: MutableMap<BlockPos, FluidFlowKernel.CellState> = HashMap()
        for (pos in positions) {
            val state = level.getBlockState(pos)
            val liquidId = OniMatterAccess.liquidId(state)
            if (liquidId != null) {
                val entity = OniMatterAccess.matterEntity(level, pos)
                val mass = entity?.mass() ?: 0.0
                val temp = entity?.temperatureK() ?: 293.15
                cells[pos] = FluidFlowKernel.CellState(
                    phase = FluidFlowKernel.Phase.LIQUID,
                    elementId = liquidId,
                    mass = mass,
                    temperatureK = temp
                )
                continue
            }
            val gasSpec = OniMatterAccess.gasSpec(state)
            if (gasSpec != null) {
                val entity = OniMatterAccess.matterEntity(level, pos)
                val mass = entity?.mass() ?: 0.0
                val temp = entity?.temperatureK() ?: 293.15
                cells[pos] = FluidFlowKernel.CellState(
                    phase = FluidFlowKernel.Phase.GAS,
                    elementId = gasSpec.id,
                    mass = mass,
                    temperatureK = temp
                )
                continue
            }
            if (state.isAir) {
                cells[pos] = FluidFlowKernel.CellState(
                    phase = FluidFlowKernel.Phase.EMPTY,
                    elementId = null,
                    mass = 0.0,
                    temperatureK = 293.15
                )
                continue
            }
            cells[pos] = FluidFlowKernel.CellState(
                phase = FluidFlowKernel.Phase.SOLID,
                elementId = null,
                mass = 0.0,
                temperatureK = 293.15
            )
        }

        val updated = FluidFlowKernel.applyLiquidFlow(
            cells,
            FluidFlowKernel.FlowConfig(
                maxTransferKgPerStep = maxTransfer,
                referenceMassKg = LIQUID_REFERENCE_MASS_KG,
                downwardBias = DOWNWARD_BIAS
            )
        )

        for ((pos, cell) in updated) {
            if (cell.phase == FluidFlowKernel.Phase.SOLID) {
                continue
            }
            when (cell.phase) {
                FluidFlowKernel.Phase.EMPTY -> {
                    val state = level.getBlockState(pos)
                    if (!state.isAir) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2)
                    }
                }
                FluidFlowKernel.Phase.LIQUID -> {
                    val liquidId = cell.elementId ?: continue
                    val target = blockStateForLiquid(liquidId)
                    val current = level.getBlockState(pos)
                    if (current.block != target.block) {
                        level.setBlock(pos, target, 2)
                    }
                    val entity = OniMatterAccess.matterEntity(level, pos) ?: continue
                    entity.setMass(cell.mass)
                    entity.setTemperatureK(cell.temperatureK)
                }
                else -> {
                }
            }
        }
    }

    private fun blockStateForLiquid(liquidId: String): net.minecraft.world.level.block.state.BlockState {
        return when (liquidId) {
            OniElements.LIQUID_WATER -> mconi.common.block.OniBlockLookup.state(mconi.common.block.OniBlockFactory.WATER)
            OniElements.LIQUID_POLLUTED_WATER -> mconi.common.block.OniBlockLookup.state(mconi.common.block.OniBlockFactory.POLLUTED_WATER)
            OniElements.LIQUID_CRUDE_OIL -> mconi.common.block.OniBlockLookup.state(mconi.common.block.OniBlockFactory.CRUDE_OIL)
            OniElements.LIQUID_LAVA -> mconi.common.block.OniBlockLookup.state(mconi.common.block.OniBlockFactory.LAVA)
            else -> Blocks.AIR.defaultBlockState()
        }
    }

    companion object {
        private const val LIQUID_REFERENCE_MASS_KG = 1000.0
        private const val DOWNWARD_BIAS = 0.25
    }
}
