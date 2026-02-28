package mconi.common.sim.subsystem

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
        val maxTransfer = config.gasTransferKgPerStep().coerceAtLeast(0.0)
        if (maxTransfer <= 0.0) {
            return
        }

        val deltas: MutableMap<BlockPos, Double> = HashMap()

        for (pos in positions) {
            val state = level.getBlockState(pos)
            val gasSpec = OniMatterAccess.gasSpec(state) ?: continue
            val entity = OniMatterAccess.matterEntity(level, pos) ?: continue
            val mass = entity.massKg()
            if (mass <= 0.0) {
                continue
            }

            for (neighbor in neighborsOf(pos)) {
                if (pos.x > neighbor.x ||
                    (pos.x == neighbor.x && pos.y > neighbor.y) ||
                    (pos.x == neighbor.x && pos.y == neighbor.y && pos.z > neighbor.z)
                ) {
                    continue
                }
                val neighborState = level.getBlockState(neighbor)
                val neighborSpec = OniMatterAccess.gasSpec(neighborState)
                if (neighborSpec != gasSpec) {
                    continue
                }
                val neighborMass = OniMatterAccess.matterEntity(level, neighbor)?.massKg() ?: 0.0
                val diff = mass - neighborMass
                if (kotlin.math.abs(diff) < 0.0001) {
                    continue
                }
                val transfer = (diff * 0.1).coerceIn(-maxTransfer, maxTransfer)
                if (transfer == 0.0) {
                    continue
                }
                deltas[pos] = (deltas[pos] ?: 0.0) - transfer
                deltas[neighbor] = (deltas[neighbor] ?: 0.0) + transfer
            }
        }

        for ((pos, delta) in deltas) {
            if (delta == 0.0) {
                continue
            }
            val state = level.getBlockState(pos)
            val gasSpec = OniMatterAccess.gasSpec(state)
            if (gasSpec == null) {
                continue
            }
            val entity = OniMatterAccess.matterEntity(level, pos) ?: continue
            val next = (entity.massKg() + delta).coerceAtLeast(0.0)
            if (next <= 0.0) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2)
                continue
            }
            entity.setMassKg(next)
        }
    }

    private fun neighborsOf(coordinate: BlockPos): List<BlockPos> {
        return listOf(
            BlockPos(coordinate.x + 1, coordinate.y, coordinate.z),
            BlockPos(coordinate.x - 1, coordinate.y, coordinate.z),
            BlockPos(coordinate.x, coordinate.y + 1, coordinate.z),
            BlockPos(coordinate.x, coordinate.y - 1, coordinate.z),
            BlockPos(coordinate.x, coordinate.y, coordinate.z + 1),
            BlockPos(coordinate.x, coordinate.y, coordinate.z - 1),
        )
    }

    companion object
}
