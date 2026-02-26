package mconi.neoforge.world

import mconi.common.AbstractModInitializer
import mconi.common.sim.OniServices
import mconi.common.sim.OniSimulationConfig
import mconi.common.sim.OniWorldFoundation
import mconi.common.world.OniWorldLayout
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.border.WorldBorder
import org.apache.logging.log4j.Logger
import mconi.common.block.OniBlockLookup
import mconi.common.content.OniBlockIds

object OniWorldEnforcer {
    private val LOGGER: Logger = AbstractModInitializer.LOGGER

    fun applyWorldBorder(level: ServerLevel) {
        if (level.dimension() != Level.OVERWORLD) {
            return
        }
        val config: OniSimulationConfig = OniServices.simulationRuntime().config()
        val minX = config.worldMinX()
        val maxX = config.worldMaxX()
        val minZ = config.worldMinZ()
        val maxZ = config.worldMaxZ()
        val sizeX = maxOf(1, maxX - minX + 1)
        val sizeZ = maxOf(1, maxZ - minZ + 1)
        val size = maxOf(sizeX.toDouble(), sizeZ.toDouble())
        val centerX = minX + sizeX / 2.0
        val centerZ = minZ + sizeZ / 2.0

        val border: WorldBorder = level.worldBorder
        border.setCenter(centerX, centerZ)
        border.setSize(size)
        LOGGER.info("Applied ONI world border center=({},{}) size={}", centerX, centerZ, size)
    }

    fun enforceChunk(level: ServerLevel, chunk: LevelChunk) {
        if (level.dimension() != Level.OVERWORLD) {
            return
        }
        val config: OniSimulationConfig = OniServices.simulationRuntime().config()
        val minY = level.minY
        val maxY = level.maxY - 1
        val minX = config.worldMinX()
        val maxX = config.worldMaxX()
        val minZ = config.worldMinZ()
        val maxZ = config.worldMaxZ()
        val pos: ChunkPos = chunk.pos
        val chunkMinX = pos.minBlockX
        val chunkMaxX = pos.maxBlockX
        val chunkMinZ = pos.minBlockZ
        val chunkMaxZ = pos.maxBlockZ

        val touchesMinX = chunkMinX <= minX && minX <= chunkMaxX
        val touchesMaxX = chunkMinX <= maxX && maxX <= chunkMaxX
        val touchesMinZ = chunkMinZ <= minZ && minZ <= chunkMaxZ
        val touchesMaxZ = chunkMinZ <= maxZ && maxZ <= chunkMaxZ

        if (touchesMinX) {
            buildBedrockWall(level, minX, chunkMinZ, chunkMaxZ, minY, maxY)
        }
        if (touchesMaxX) {
            buildBedrockWall(level, maxX, chunkMinZ, chunkMaxZ, minY, maxY)
        }
        if (touchesMinZ) {
            buildBedrockWallZ(level, minZ, chunkMinX, chunkMaxX, minY, maxY)
        }
        if (touchesMaxZ) {
            buildBedrockWallZ(level, maxZ, chunkMinX, chunkMaxX, minY, maxY)
        }

        ensureSinglePrintingPod(level, chunk, minY, maxY)

        for (x in chunkMinX..chunkMaxX) {
            for (z in chunkMinZ..chunkMaxZ) {
                if (!OniWorldFoundation.isWithinHorizontalBounds(x, z, config)) {
                    continue
                }
                for (y in maxY downTo minY) {
                    if (OniWorldFoundation.isVoidBand(y, maxY, config)) {
                        setIfNot(level, x, y, z, Blocks.AIR)
                        continue
                    }
                    if (OniWorldFoundation.isLavaBand(y, minY, config)) {
                        setIfReplaceable(level, x, y, z, Blocks.LAVA)
                    }
                }
            }
        }
    }

    private fun ensureSinglePrintingPod(level: ServerLevel, chunk: LevelChunk, minY: Int, maxY: Int) {
        val podPos = BlockPos(OniWorldLayout.POD_X, OniWorldLayout.POD_Y, OniWorldLayout.POD_Z)
        val chunkPos = chunk.pos
        val chunkMinX = chunkPos.minBlockX
        val chunkMaxX = chunkPos.maxBlockX
        val chunkMinZ = chunkPos.minBlockZ
        val chunkMaxZ = chunkPos.maxBlockZ
        val podState = OniBlockLookup.state(OniBlockIds.PRINTING_POD)
        val fillerState = OniBlockLookup.state(OniBlockIds.IGNEOUS_ROCK)

        if (podPos.x in chunkMinX..chunkMaxX && podPos.z in chunkMinZ..chunkMaxZ && podPos.y in minY..maxY) {
            if (level.getBlockState(podPos).block != podState.block) {
                level.setBlock(podPos, podState, 3)
            }
        }

        for (x in chunkMinX..chunkMaxX) {
            for (z in chunkMinZ..chunkMaxZ) {
                for (y in minY..maxY) {
                    val pos = BlockPos(x, y, z)
                    val state = level.getBlockState(pos)
                    if (state.block == podState.block && pos != podPos) {
                        level.setBlock(pos, fillerState, 3)
                    }
                }
            }
        }
    }

    private fun buildBedrockWall(level: ServerLevel, x: Int, minZ: Int, maxZ: Int, minY: Int, maxY: Int) {
        for (z in minZ..maxZ) {
            for (y in minY..maxY) {
                level.setBlock(BlockPos(x, y, z), Blocks.BEDROCK.defaultBlockState(), 3)
            }
        }
    }

    private fun buildBedrockWallZ(level: ServerLevel, z: Int, minX: Int, maxX: Int, minY: Int, maxY: Int) {
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                level.setBlock(BlockPos(x, y, z), Blocks.BEDROCK.defaultBlockState(), 3)
            }
        }
    }

    private fun setIfNot(level: ServerLevel, x: Int, y: Int, z: Int, block: Block) {
        val pos = BlockPos(x, y, z)
        if (level.getBlockState(pos).block != block) {
            level.setBlock(pos, block.defaultBlockState(), 2)
        }
    }

    private fun setIfReplaceable(level: ServerLevel, x: Int, y: Int, z: Int, block: Block) {
        val pos = BlockPos(x, y, z)
        val state: BlockState = level.getBlockState(pos)
        if (state.isAir || state.fluidState.`is`(Fluids.WATER)) {
            level.setBlock(pos, block.defaultBlockState(), 2)
        }
    }
}
