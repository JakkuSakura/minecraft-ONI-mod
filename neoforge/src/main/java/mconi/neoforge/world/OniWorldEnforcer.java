/*
 *    This file is part of the minecraft-ONI-mod
 *    licensed under the GNU GPL v3 License.
 *
 *    Copyright (C) 2024  Leander Knüttel
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package mconi.neoforge.world;

import mconi.common.AbstractModInitializer;
import mconi.common.sim.OniServices;
import mconi.common.sim.OniSimulationConfig;
import mconi.common.sim.OniWorldFoundation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Fluids;
import org.apache.logging.log4j.Logger;

public final class OniWorldEnforcer {
    private static final Logger LOGGER = AbstractModInitializer.LOGGER;

    private OniWorldEnforcer() {
    }

    public static void applyWorldBorder(ServerLevel level) {
        if (level.dimension() != Level.OVERWORLD) {
            return;
        }
        OniSimulationConfig config = OniServices.simulationRuntime().config();
        int minX = config.worldMinX();
        int maxX = config.worldMaxX();
        int minZ = config.worldMinZ();
        int maxZ = config.worldMaxZ();
        int sizeX = Math.max(1, maxX - minX + 1);
        int sizeZ = Math.max(1, maxZ - minZ + 1);
        double size = Math.max(sizeX, sizeZ);
        double centerX = minX + sizeX / 2.0;
        double centerZ = minZ + sizeZ / 2.0;

        WorldBorder border = level.getWorldBorder();
        border.setCenter(centerX, centerZ);
        border.setSize(size);
        LOGGER.info("Applied ONI world border center=({},{}) size={}", centerX, centerZ, size);
    }

    public static void enforceChunk(ServerLevel level, LevelChunk chunk) {
        if (level.dimension() != Level.OVERWORLD) {
            return;
        }
        OniSimulationConfig config = OniServices.simulationRuntime().config();
        int minY = level.getMinY();
        int maxY = level.getMaxY() - 1;
        int minX = config.worldMinX();
        int maxX = config.worldMaxX();
        int minZ = config.worldMinZ();
        int maxZ = config.worldMaxZ();
        ChunkPos pos = chunk.getPos();
        int chunkMinX = pos.getMinBlockX();
        int chunkMaxX = pos.getMaxBlockX();
        int chunkMinZ = pos.getMinBlockZ();
        int chunkMaxZ = pos.getMaxBlockZ();

        boolean touchesMinX = chunkMinX <= minX && minX <= chunkMaxX;
        boolean touchesMaxX = chunkMinX <= maxX && maxX <= chunkMaxX;
        boolean touchesMinZ = chunkMinZ <= minZ && minZ <= chunkMaxZ;
        boolean touchesMaxZ = chunkMinZ <= maxZ && maxZ <= chunkMaxZ;

        if (touchesMinX) {
            buildBedrockWall(level, minX, chunkMinZ, chunkMaxZ, minY, maxY);
        }
        if (touchesMaxX) {
            buildBedrockWall(level, maxX, chunkMinZ, chunkMaxZ, minY, maxY);
        }
        if (touchesMinZ) {
            buildBedrockWallZ(level, minZ, chunkMinX, chunkMaxX, minY, maxY);
        }
        if (touchesMaxZ) {
            buildBedrockWallZ(level, maxZ, chunkMinX, chunkMaxX, minY, maxY);
        }

        // Clear top void band and flood bottom lava band inside bounds.
        for (int x = chunkMinX; x <= chunkMaxX; x++) {
            for (int z = chunkMinZ; z <= chunkMaxZ; z++) {
                if (!OniWorldFoundation.isWithinHorizontalBounds(x, z, config)) {
                    continue;
                }
                for (int y = maxY; y >= minY; y--) {
                    if (OniWorldFoundation.isVoidBand(y, maxY, config)) {
                        setIfNot(level, x, y, z, Blocks.AIR);
                        continue;
                    }
                    if (OniWorldFoundation.isLavaBand(y, minY, config)) {
                        setIfReplaceable(level, x, y, z, Blocks.LAVA);
                    }
                }
            }
        }
    }

    private static void buildBedrockWall(ServerLevel level, int x, int minZ, int maxZ, int minY, int maxY) {
        for (int z = minZ; z <= maxZ; z++) {
            for (int y = minY; y <= maxY; y++) {
                level.setBlock(new BlockPos(x, y, z), Blocks.BEDROCK.defaultBlockState(), 3);
            }
        }
    }

    private static void buildBedrockWallZ(ServerLevel level, int z, int minX, int maxX, int minY, int maxY) {
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                level.setBlock(new BlockPos(x, y, z), Blocks.BEDROCK.defaultBlockState(), 3);
            }
        }
    }

    private static void setIfNot(ServerLevel level, int x, int y, int z, net.minecraft.world.level.block.Block block) {
        BlockPos pos = new BlockPos(x, y, z);
        if (level.getBlockState(pos).getBlock() != block) {
            level.setBlock(pos, block.defaultBlockState(), 2);
        }
    }

    private static void setIfReplaceable(ServerLevel level, int x, int y, int z, net.minecraft.world.level.block.Block block) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.getFluidState().is(Fluids.WATER)) {
            level.setBlock(pos, block.defaultBlockState(), 2);
        }
    }
}
