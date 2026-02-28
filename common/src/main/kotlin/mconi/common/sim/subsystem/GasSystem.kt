package mconi.common.sim.subsystem

import mconi.common.block.OniBlockFactory
import mconi.common.block.OniBlockLookup
import mconi.common.element.OniElements
import mconi.common.world.OniMatterAccess
import mconi.common.world.OniWorldScan
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks

class GasSystem : OniSystem {
    override fun id(): String = "gas"

    override fun run(context: SystemContext) {
        val level = context.level()
        val config = context.config()
        val radius = config.worldSampleRadiusBlocks()
        val cellSize = config.cellSize()
        val positions = OniWorldScan.positionsAroundPlayers(level, radius, cellSize)
        val maxTransfer = config.gasTransferPerStep().coerceAtLeast(0.0)
        if (maxTransfer <= 0.0) {
            return
        }

        val cells: MutableMap<BlockPos, FluidFlowKernel.CellState> = HashMap()
        for (pos in positions) {
            val state = level.getBlockState(pos)
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
            cells[pos] = FluidFlowKernel.CellState(
                phase = FluidFlowKernel.Phase.SOLID,
                elementId = null,
                mass = 0.0,
                temperatureK = 293.15
            )
        }

        val updated = FluidFlowKernel.applyGasFlow(
            cells,
            FluidFlowKernel.FlowConfig(
                maxTransferKgPerStep = maxTransfer,
                referenceMassKg = GAS_REFERENCE_MASS_KG,
                downwardBias = 0.0
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
                FluidFlowKernel.Phase.GAS -> {
                    val gasSpec = OniElements.parseGas(cell.elementId ?: "") ?: continue
                    val target = blockStateForGas(gasSpec)
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

    private fun blockStateForGas(spec: OniElements.GasSpec): net.minecraft.world.level.block.state.BlockState {
        return when (spec.id) {
            OniElements.GAS_OXYGEN.id -> OniBlockLookup.state(OniBlockFactory.OXYGEN_GAS)
            OniElements.GAS_CARBON_DIOXIDE.id -> OniBlockLookup.state(OniBlockFactory.CARBON_DIOXIDE_GAS)
            OniElements.GAS_HYDROGEN.id -> OniBlockLookup.state(OniBlockFactory.HYDROGEN_GAS)
            else -> Blocks.AIR.defaultBlockState()
        }
    }

    companion object {
        private const val GAS_REFERENCE_MASS_KG = 100.0
    }
}
