package conservecraft.spigot.world

import org.bukkit.Material
import org.bukkit.World
import org.bukkit.generator.ChunkGenerator
import java.util.Random

class OniChunkGenerator : ChunkGenerator() {
    override fun generateChunkData(world: World, random: Random, chunkX: Int, chunkZ: Int, biome: BiomeGrid): ChunkData {
        val data = createChunkData(world)
        val minY = world.minHeight
        val maxY = world.maxHeight - 1
        val surfaceY = minOf(SURFACE_Y, maxY - 1)
        val lavaTop = minY + LAVA_BAND_HEIGHT - 1
        val spaceStart = maxY - SPACE_BAND_HEIGHT + 1

        for (x in 0 until 16) {
            for (z in 0 until 16) {
                for (y in minY..maxY) {
                    when {
                        y == minY -> data.setBlock(x, y, z, Material.BEDROCK)
                        y <= lavaTop -> data.setBlock(x, y, z, Material.LAVA)
                        y <= surfaceY -> data.setBlock(x, y, z, Material.STONE)
                        y >= spaceStart -> data.setBlock(x, y, z, Material.AIR)
                        else -> data.setBlock(x, y, z, Material.AIR)
                    }
                }
            }
        }

        if (chunkX == 0 && chunkZ == 0) {
            val baseY = surfaceY - 4
            for (x in 4 until 8) {
                for (z in 4 until 8) {
                    data.setBlock(x, baseY, z, Material.WATER)
                }
            }
            data.setBlock(10, baseY + 1, 10, Material.MOSS_BLOCK)
            data.setBlock(11, baseY + 1, 10, Material.MOSS_BLOCK)
            data.setBlock(12, baseY + 1, 10, Material.IRON_ORE)
            data.setBlock(13, baseY - 6, 13, Material.MAGMA_BLOCK)
        }

        return data
    }

    companion object {
        private const val SURFACE_Y = 80
        private const val LAVA_BAND_HEIGHT = 24
        private const val SPACE_BAND_HEIGHT = 32
    }
}
