package mconi.spigot.world;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class OniChunkGenerator extends ChunkGenerator {
    private static final int SURFACE_Y = 80;
    private static final int LAVA_BAND_HEIGHT = 24;
    private static final int SPACE_BAND_HEIGHT = 32;

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        ChunkData data = createChunkData(world);
        int minY = world.getMinHeight();
        int maxY = world.getMaxHeight() - 1;
        int surfaceY = Math.min(SURFACE_Y, maxY - 1);
        int lavaTop = minY + LAVA_BAND_HEIGHT - 1;
        int spaceStart = maxY - SPACE_BAND_HEIGHT + 1;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = minY; y <= maxY; y++) {
                    if (y == minY) {
                        data.setBlock(x, y, z, Material.BEDROCK);
                    } else if (y <= lavaTop) {
                        data.setBlock(x, y, z, Material.LAVA);
                    } else if (y <= surfaceY) {
                        data.setBlock(x, y, z, Material.STONE);
                    } else if (y >= spaceStart) {
                        data.setBlock(x, y, z, Material.AIR);
                    } else {
                        data.setBlock(x, y, z, Material.AIR);
                    }
                }
            }
        }

        if (chunkX == 0 && chunkZ == 0) {
            int baseY = surfaceY - 4;
            for (int x = 4; x < 8; x++) {
                for (int z = 4; z < 8; z++) {
                    data.setBlock(x, baseY, z, Material.WATER);
                }
            }
            data.setBlock(10, baseY + 1, 10, Material.MOSS_BLOCK);
            data.setBlock(11, baseY + 1, 10, Material.MOSS_BLOCK);
            data.setBlock(12, baseY + 1, 10, Material.IRON_ORE);
            data.setBlock(13, baseY - 6, 13, Material.MAGMA_BLOCK);
        }

        return data;
    }
}
