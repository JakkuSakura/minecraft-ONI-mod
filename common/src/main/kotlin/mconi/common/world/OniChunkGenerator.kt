package mconi.common.world

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.HolderLookup
import net.minecraft.server.level.WorldGenRegion
import net.minecraft.util.random.WeightedList
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.LevelHeightAccessor
import net.minecraft.world.level.NoiseColumn
import net.minecraft.world.level.StructureManager
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.BiomeManager
import net.minecraft.world.level.biome.BiomeSource
import net.minecraft.world.level.biome.MobSpawnSettings
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.RandomState
import net.minecraft.world.level.levelgen.blending.Blender
import net.minecraft.world.level.levelgen.structure.StructureSet
import java.util.concurrent.CompletableFuture

class OniChunkGenerator(biomeSource: BiomeSource) : ChunkGenerator(biomeSource) {
    override fun codec(): MapCodec<out ChunkGenerator> = CODEC

    override fun validate() {
    }

    override fun createState(
        structureSets: HolderLookup<StructureSet>,
        randomState: RandomState,
        seed: Long
    ): ChunkGeneratorStructureState {
        return ChunkGeneratorStructureState.createForNormal(randomState, seed, biomeSource, structureSets)
    }

    override fun createBiomes(
        randomState: RandomState,
        blender: Blender,
        structureManager: StructureManager,
        chunkAccess: ChunkAccess
    ): CompletableFuture<ChunkAccess> {
        val sampler = randomState.sampler()
        chunkAccess.fillBiomesFromNoise({ x, y, z, climateSampler -> biomeSource.getNoiseBiome(x, y, z, climateSampler) }, sampler)
        return CompletableFuture.completedFuture(chunkAccess)
    }

    override fun applyCarvers(
        region: WorldGenRegion,
        seed: Long,
        randomState: RandomState,
        biomeManager: BiomeManager,
        structureManager: StructureManager,
        chunk: ChunkAccess
    ) {
    }

    override fun buildSurface(
        region: WorldGenRegion,
        structureManager: StructureManager,
        randomState: RandomState,
        chunk: ChunkAccess
    ) {
    }

    override fun spawnOriginalMobs(region: WorldGenRegion) {
    }

    override fun getGenDepth(): Int = DEFAULT_HEIGHT

    override fun getSeaLevel(): Int = 63

    override fun getMinY(): Int = DEFAULT_MIN_Y

    override fun fillFromNoise(
        blender: Blender,
        randomState: RandomState,
        structureManager: StructureManager,
        chunk: ChunkAccess
    ): CompletableFuture<ChunkAccess> {
        val chunkPos = chunk.pos
        val minY = chunk.minY
        val maxY = minY + chunk.height - 1
        val surfaceY = minOf(SURFACE_Y, maxY - 1)
        val lavaTop = minY + LAVA_BAND_HEIGHT - 1
        val spaceStart = maxY - SPACE_BAND_HEIGHT + 1

        for (dx in 0..15) {
            for (dz in 0..15) {
                val worldX = chunkPos.minBlockX + dx
                val worldZ = chunkPos.minBlockZ + dz

                for (y in minY..maxY) {
                    val state = when {
                        y == minY -> Blocks.BEDROCK.defaultBlockState()
                        y <= lavaTop -> Blocks.LAVA.defaultBlockState()
                        y <= surfaceY -> Blocks.STONE.defaultBlockState()
                        y >= spaceStart -> Blocks.AIR.defaultBlockState()
                        else -> Blocks.AIR.defaultBlockState()
                    }
                    chunk.setBlockState(BlockPos(worldX, y, worldZ), state, 0)
                }
            }
        }

        placeGuaranteedResources(chunk, chunkPos, surfaceY)
        return CompletableFuture.completedFuture(chunk)
    }

    override fun getBaseHeight(
        x: Int,
        z: Int,
        heightmap: Heightmap.Types,
        level: LevelHeightAccessor,
        randomState: RandomState
    ): Int {
        return SURFACE_Y
    }

    override fun getBaseColumn(x: Int, z: Int, level: LevelHeightAccessor, randomState: RandomState): NoiseColumn {
        val minY = level.minY
        val maxY = minY + level.height - 1
        val states = arrayOfNulls<BlockState>(level.height)
        val surfaceY = minOf(SURFACE_Y, maxY - 1)
        val lavaTop = minY + LAVA_BAND_HEIGHT - 1
        val spaceStart = maxY - SPACE_BAND_HEIGHT + 1
        for (y in minY..maxY) {
            val state = when {
                y == minY -> Blocks.BEDROCK.defaultBlockState()
                y <= lavaTop -> Blocks.LAVA.defaultBlockState()
                y <= surfaceY -> Blocks.STONE.defaultBlockState()
                y >= spaceStart -> Blocks.AIR.defaultBlockState()
                else -> Blocks.AIR.defaultBlockState()
            }
            states[y - minY] = state
        }
        @Suppress("UNCHECKED_CAST")
        return NoiseColumn(minY, states as Array<BlockState>)
    }

    override fun addDebugScreenInfo(info: MutableList<String>, randomState: RandomState, pos: BlockPos) {
        info.add("ONI Generator")
        info.add("surface_y=$SURFACE_Y")
    }

    override fun getMobsAt(
        biome: Holder<Biome>,
        structureManager: StructureManager,
        category: MobCategory,
        pos: BlockPos
    ): WeightedList<MobSpawnSettings.SpawnerData> {
        return biome.value().mobSettings.getMobs(category)
    }

    companion object {
        private const val DEFAULT_MIN_Y = -64
        private const val DEFAULT_HEIGHT = 384
        private const val SURFACE_Y = 80
        private const val LAVA_BAND_HEIGHT = 24
        private const val SPACE_BAND_HEIGHT = 32

        val CODEC: MapCodec<OniChunkGenerator> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                BiomeSource.CODEC.fieldOf("biome_source").forGetter(OniChunkGenerator::biomeSource)
            ).apply(instance, ::OniChunkGenerator)
        }

        private fun placeGuaranteedResources(chunk: ChunkAccess, chunkPos: ChunkPos, surfaceY: Int) {
            if (chunkPos.x == 0 && chunkPos.z == 0) {
                val y = surfaceY - 4
                for (dx in 4..7) {
                    for (dz in 4..7) {
                        chunk.setBlockState(
                            BlockPos(chunkPos.minBlockX + dx, y, chunkPos.minBlockZ + dz),
                            Blocks.WATER.defaultBlockState(),
                            0
                        )
                    }
                }
                chunk.setBlockState(
                    BlockPos(chunkPos.minBlockX + 10, y + 1, chunkPos.minBlockZ + 10),
                    Blocks.MOSS_BLOCK.defaultBlockState(),
                    0
                )
                chunk.setBlockState(
                    BlockPos(chunkPos.minBlockX + 11, y + 1, chunkPos.minBlockZ + 10),
                    Blocks.MOSS_BLOCK.defaultBlockState(),
                    0
                )
                chunk.setBlockState(
                    BlockPos(chunkPos.minBlockX + 12, y + 1, chunkPos.minBlockZ + 10),
                    Blocks.IRON_ORE.defaultBlockState(),
                    0
                )
                chunk.setBlockState(
                    BlockPos(chunkPos.minBlockX + 13, y - 6, chunkPos.minBlockZ + 13),
                    Blocks.MAGMA_BLOCK.defaultBlockState(),
                    0
                )
            }
        }
    }
}
