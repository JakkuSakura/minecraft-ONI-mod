package mconi.common.sim.conduit

import mconi.common.world.OniWorldScan
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import java.util.ArrayDeque

class OniConduitNetworkBuilder(
    private val level: ServerLevel,
    private val isConduit: (net.minecraft.world.level.block.state.BlockState) -> Boolean
) {
    fun build(radius: Int, cellSize: Int): List<List<BlockPos>> {
        val positions = OniWorldScan.positionsAroundPlayers(level, radius, cellSize)
        val conduits: MutableSet<BlockPos> = HashSet()
        for (pos in positions) {
            if (isConduit(level.getBlockState(pos))) {
                conduits.add(pos)
            }
        }
        val visited: MutableSet<BlockPos> = HashSet()
        val networks: MutableList<List<BlockPos>> = ArrayList()
        for (start in conduits) {
            if (!visited.add(start)) {
                continue
            }
            val network: MutableList<BlockPos> = ArrayList()
            val queue: ArrayDeque<BlockPos> = ArrayDeque()
            queue.add(start)
            while (queue.isNotEmpty()) {
                val pos = queue.removeFirst()
                network.add(pos)
                for (neighbor in neighborsOf(pos)) {
                    if (!conduits.contains(neighbor)) {
                        continue
                    }
                    if (visited.add(neighbor)) {
                        queue.add(neighbor)
                    }
                }
            }
            networks.add(network)
        }
        return networks
    }

    private fun neighborsOf(pos: BlockPos): List<BlockPos> {
        return listOf(
            BlockPos(pos.x + 1, pos.y, pos.z),
            BlockPos(pos.x - 1, pos.y, pos.z),
            BlockPos(pos.x, pos.y + 1, pos.z),
            BlockPos(pos.x, pos.y - 1, pos.z),
            BlockPos(pos.x, pos.y, pos.z + 1),
            BlockPos(pos.x, pos.y, pos.z - 1)
        )
    }
}
