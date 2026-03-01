package mconi.common.world

import mconi.common.AbstractModBootstrap
import mconi.common.block.OniBlockLookup
import mconi.common.block.OniBlockFactory
import mconi.common.sim.OniServices
import mconi.common.sim.OniSystemConfig
import mconi.common.sim.OniWorldFoundation
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

object OniWorldEnforcer {
    private val LOGGER: Logger = AbstractModBootstrap.LOGGER

    fun applyWorldBorder(level: ServerLevel) {
        if (level.dimension() != Level.OVERWORLD) {
            return
        }
        syncWorldgenBounds(level)
        val config: OniSystemConfig = OniServices.systemRuntime().config()
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
        syncWorldgenBounds(level)
        val config: OniSystemConfig = OniServices.systemRuntime().config()
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
        val zThickness = maxZ - minZ + 1
        val zBordersEnabled = zThickness >= 3

        if (touchesMinX) {
            buildBarrierWall(level, minX, chunkMinZ, chunkMaxZ, minY, maxY)
        }
        if (touchesMaxX) {
            buildBarrierWall(level, maxX, chunkMinZ, chunkMaxZ, minY, maxY)
        }
        if (zBordersEnabled && touchesMinZ) {
            buildBarrierWallZ(level, minZ, chunkMinX, chunkMaxX, minY, maxY)
        }
        if (zBordersEnabled && touchesMaxZ) {
            buildBarrierWallZ(level, maxZ, chunkMinX, chunkMaxX, minY, maxY)
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
                        setIfReplaceable(level, x, y, z, OniBlockLookup.block(OniBlockFactory.LAVA))
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
        val podState = OniBlockLookup.state(OniBlockFactory.PRINTING_POD)
        val fillerState = OniBlockLookup.state(OniBlockFactory.IGNEOUS_ROCK)

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

    private fun buildBarrierWall(level: ServerLevel, x: Int, minZ: Int, maxZ: Int, minY: Int, maxY: Int) {
        for (z in minZ..maxZ) {
            for (y in minY..maxY) {
                level.setBlock(BlockPos(x, y, z), Blocks.BARRIER.stateDefinition.any(), 3)
            }
        }
    }

    private fun buildBarrierWallZ(level: ServerLevel, z: Int, minX: Int, maxX: Int, minY: Int, maxY: Int) {
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                level.setBlock(BlockPos(x, y, z), Blocks.BARRIER.stateDefinition.any(), 3)
            }
        }
    }

    private fun setIfNot(level: ServerLevel, x: Int, y: Int, z: Int, block: Block) {
        val pos = BlockPos(x, y, z)
        if (level.getBlockState(pos).block != block) {
            level.setBlock(pos, block.stateDefinition.any(), 2)
        }
    }

    private fun setIfReplaceable(level: ServerLevel, x: Int, y: Int, z: Int, block: Block) {
        val pos = BlockPos(x, y, z)
        val state: BlockState = level.getBlockState(pos)
        val isLiquid = state.fluidState.`is`(Fluids.WATER) || state.fluidState.`is`(Fluids.LAVA)
        if (state.isAir || isLiquid || state.`is`(OniBlockLookup.block(OniBlockFactory.WATER)) ||
            state.`is`(OniBlockLookup.block(OniBlockFactory.POLLUTED_WATER)) ||
            state.`is`(OniBlockLookup.block(OniBlockFactory.CRUDE_OIL)) ||
            state.`is`(OniBlockLookup.block(OniBlockFactory.LAVA))
        ) {
            level.setBlock(pos, block.stateDefinition.any(), 2)
        }
    }

    fun syncWorldgenBounds(level: ServerLevel) {
        if (level.dimension() != Level.OVERWORLD) {
            return
        }
        val generator = level.chunkSource.generator
        val config = OniServices.systemRuntime().config()
        if (generator is OniChunkGenerator) {
            config.setWorldMinZ(generator.minZ())
            config.setWorldMaxZ(generator.maxZ())
        }
    }
}
