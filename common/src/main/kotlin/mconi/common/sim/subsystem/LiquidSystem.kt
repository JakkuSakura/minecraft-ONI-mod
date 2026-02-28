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

        val deltas: MutableMap<BlockPos, Double> = HashMap()
        val incomingLiquidByPos: MutableMap<BlockPos, String> = HashMap()
        val liquidIdByPos: MutableMap<BlockPos, String> = HashMap()
        val massByPos: MutableMap<BlockPos, Double> = HashMap()

        for (pos in positions) {
            val state = level.getBlockState(pos)
            val liquidId = OniMatterAccess.liquidId(state) ?: continue
            val entity = OniMatterAccess.matterEntity(level, pos) ?: continue
            val mass = entity.mass()
            if (mass <= 0.0) {
                continue
            }
            liquidIdByPos[pos] = liquidId
            massByPos[pos] = mass
        }

        for (pos in positions) {
            val liquidId = liquidIdByPos[pos] ?: continue
            val mass = massByPos[pos] ?: 0.0
            if (mass <= 0.0) {
                continue
            }

            val below = BlockPos(pos.x, pos.y - 1, pos.z)
            val belowState = level.getBlockState(below)
            val belowLiquid = OniMatterAccess.liquidId(belowState)
            if (belowLiquid == null || belowLiquid == liquidId) {
                val belowMass = massByPos[below] ?: OniMatterAccess.matterEntity(level, below)?.mass() ?: 0.0
                val capacity = MAX_LIQUID_MASS_PER_CELL - belowMass
                if (capacity > 0.0) {
                    val transfer = minOf(maxTransfer, mass, capacity)
                    if (transfer > 0.0) {
                        deltas[pos] = (deltas[pos] ?: 0.0) - transfer
                        deltas[below] = (deltas[below] ?: 0.0) + transfer
                        if (belowLiquid == null) {
                            incomingLiquidByPos[below] = liquidId
                        }
                        continue
                    }
                }
            }

            for (neighbor in lateralNeighbors(pos)) {
                val neighborState = level.getBlockState(neighbor)
                val neighborLiquid = OniMatterAccess.liquidId(neighborState)
                if (neighborLiquid != null && neighborLiquid != liquidId) {
                    continue
                }
                val neighborMass = massByPos[neighbor] ?: OniMatterAccess.matterEntity(level, neighbor)?.mass() ?: 0.0
                val diff = mass - neighborMass
                if (diff <= 1.0) {
                    continue
                }
                val transfer = minOf(maxTransfer, diff * 0.25, MAX_LIQUID_MASS_PER_CELL - neighborMass)
                if (transfer <= 0.0) {
                    continue
                }
                deltas[pos] = (deltas[pos] ?: 0.0) - transfer
                deltas[neighbor] = (deltas[neighbor] ?: 0.0) + transfer
                if (neighborLiquid == null) {
                    incomingLiquidByPos[neighbor] = liquidId
                }
            }
        }

        for ((pos, delta) in deltas) {
            if (delta == 0.0) {
                continue
            }
            val state = level.getBlockState(pos)
            val liquidId = OniMatterAccess.liquidId(state)
            if (liquidId == null && delta > 0.0) {
                val targetId = incomingLiquidByPos[pos] ?: continue
                val target = blockStateForLiquid(targetId)
                level.setBlock(pos, target, 2)
            }
            val entity = OniMatterAccess.matterEntity(level, pos) ?: continue
            val next = (entity.mass() + delta).coerceAtLeast(0.0)
            if (next <= 0.0) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2)
                continue
            }
            entity.setMass(next)
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

    private fun lateralNeighbors(coordinate: BlockPos): List<BlockPos> {
        return listOf(
            BlockPos(coordinate.x + 1, coordinate.y, coordinate.z),
            BlockPos(coordinate.x - 1, coordinate.y, coordinate.z),
            BlockPos(coordinate.x, coordinate.y, coordinate.z + 1),
            BlockPos(coordinate.x, coordinate.y, coordinate.z - 1),
        )
    }

    companion object {
        private const val MAX_LIQUID_MASS_PER_CELL = 4000.0
    }
}
