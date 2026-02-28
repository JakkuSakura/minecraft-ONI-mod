package mconi.common.world

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import java.util.LinkedHashSet

object OniWorldScan {
    fun positionsAroundPlayers(level: ServerLevel, radiusBlocks: Int, cellSize: Int): List<BlockPos> {
        val positions: LinkedHashSet<BlockPos> = LinkedHashSet()
        val minY = level.minY
        val maxY = level.maxY - 1
        for (player in level.players()) {
            val center = player.blockPosition()
            var x = center.x - radiusBlocks
            while (x <= center.x + radiusBlocks) {
                var y = center.y - radiusBlocks
                while (y <= center.y + radiusBlocks) {
                    if (y in minY..maxY) {
                        var z = center.z - radiusBlocks
                        while (z <= center.z + radiusBlocks) {
                            positions.add(BlockPos(x, y, z))
                            z += cellSize
                        }
                    }
                    y += cellSize
                }
                x += cellSize
            }
        }
        return positions.toList()
    }
}
