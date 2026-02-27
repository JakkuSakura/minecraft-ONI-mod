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
import kotlin.math.max
import kotlin.math.min
import kotlin.math.abs
import mconi.common.block.OniBlockLookup
import mconi.common.content.OniBlockIds

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
        val activeMinY = max(minY, OniWorldLayout.WORLD_TARGET_MIN_Y)
        val activeMaxY = min(maxY, OniWorldLayout.WORLD_TARGET_MAX_Y)
        val surfaceY = min(OniWorldLayout.SURFACE_Y, activeMaxY - 1)
        val lavaTop = activeMinY + OniWorldLayout.LAVA_BAND_HEIGHT - 1
        val spaceStart = activeMaxY - OniWorldLayout.SPACE_BAND_HEIGHT + 1

        for (dx in 0..15) {
            for (dz in 0..15) {
                val worldX = chunkPos.minBlockX + dx
                val worldZ = chunkPos.minBlockZ + dz
                val inBounds = worldX in OniWorldLayout.WORLD_MIN_X..OniWorldLayout.WORLD_MAX_X &&
                    worldZ in OniWorldLayout.WORLD_MIN_Z..OniWorldLayout.WORLD_MAX_Z
                val onBorder = worldX == OniWorldLayout.WORLD_MIN_X || worldX == OniWorldLayout.WORLD_MAX_X ||
                    worldZ == OniWorldLayout.WORLD_MIN_Z || worldZ == OniWorldLayout.WORLD_MAX_Z

                for (y in minY..maxY) {
                    val state = when {
                        !inBounds -> Blocks.BEDROCK.defaultBlockState()
                        onBorder -> Blocks.BEDROCK.defaultBlockState()
                        y < activeMinY -> Blocks.BEDROCK.defaultBlockState()
                        y == activeMinY -> Blocks.BEDROCK.defaultBlockState()
                        y <= lavaTop -> OniBlockLookup.state(OniBlockIds.LAVA)
                        y > activeMaxY -> Blocks.AIR.defaultBlockState()
                        y >= spaceStart -> Blocks.AIR.defaultBlockState()
                        y <= surfaceY -> solidStateFor(worldX, y, worldZ, surfaceY, lavaTop)
                        else -> Blocks.AIR.defaultBlockState()
                    }
                    chunk.setBlockState(BlockPos(worldX, y, worldZ), state, 0)
                }
            }
        }

        placeGuaranteedResources(chunk, chunkPos)
        return CompletableFuture.completedFuture(chunk)
    }

    override fun getBaseHeight(
        x: Int,
        z: Int,
        heightmap: Heightmap.Types,
        level: LevelHeightAccessor,
        randomState: RandomState
    ): Int {
        val inBounds = x in OniWorldLayout.WORLD_MIN_X..OniWorldLayout.WORLD_MAX_X &&
            z in OniWorldLayout.WORLD_MIN_Z..OniWorldLayout.WORLD_MAX_Z
        return if (inBounds) min(OniWorldLayout.SURFACE_Y, OniWorldLayout.WORLD_TARGET_MAX_Y) else level.minY
    }

    override fun getBaseColumn(x: Int, z: Int, level: LevelHeightAccessor, randomState: RandomState): NoiseColumn {
        val minY = level.minY
        val maxY = minY + level.height - 1
        val states = arrayOfNulls<BlockState>(level.height)
        val activeMinY = max(minY, OniWorldLayout.WORLD_TARGET_MIN_Y)
        val activeMaxY = min(maxY, OniWorldLayout.WORLD_TARGET_MAX_Y)
        val surfaceY = min(OniWorldLayout.SURFACE_Y, activeMaxY - 1)
        val lavaTop = activeMinY + OniWorldLayout.LAVA_BAND_HEIGHT - 1
        val spaceStart = activeMaxY - OniWorldLayout.SPACE_BAND_HEIGHT + 1
        val inBounds = x in OniWorldLayout.WORLD_MIN_X..OniWorldLayout.WORLD_MAX_X &&
            z in OniWorldLayout.WORLD_MIN_Z..OniWorldLayout.WORLD_MAX_Z
        val onBorder = x == OniWorldLayout.WORLD_MIN_X || x == OniWorldLayout.WORLD_MAX_X ||
            z == OniWorldLayout.WORLD_MIN_Z || z == OniWorldLayout.WORLD_MAX_Z
        for (y in minY..maxY) {
            val state = when {
                !inBounds -> Blocks.BEDROCK.defaultBlockState()
                onBorder -> Blocks.BEDROCK.defaultBlockState()
                y < activeMinY -> Blocks.BEDROCK.defaultBlockState()
                y == activeMinY -> Blocks.BEDROCK.defaultBlockState()
                        y <= lavaTop -> OniBlockLookup.state(OniBlockIds.LAVA)
                y > activeMaxY -> Blocks.AIR.defaultBlockState()
                y >= spaceStart -> Blocks.AIR.defaultBlockState()
                y <= surfaceY -> solidStateFor(x, y, z, surfaceY, lavaTop)
                else -> Blocks.AIR.defaultBlockState()
            }
            states[y - minY] = state
        }
        @Suppress("UNCHECKED_CAST")
        return NoiseColumn(minY, states as Array<BlockState>)
    }

    override fun addDebugScreenInfo(info: MutableList<String>, randomState: RandomState, pos: BlockPos) {
        info.add("ONI Generator")
        info.add("surface_y=${OniWorldLayout.SURFACE_Y}")
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
        private const val DEFAULT_MIN_Y = -128
        private const val DEFAULT_HEIGHT = 256
        private const val TOPSOIL_DEPTH = OniWorldLayout.TOPSOIL_DEPTH
        private const val SEDIMENTARY_DEPTH = OniWorldLayout.SEDIMENTARY_DEPTH
        private const val IGNEOUS_DEPTH = OniWorldLayout.IGNEOUS_DEPTH

        @JvmField
        val CODEC: MapCodec<OniChunkGenerator> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                BiomeSource.CODEC.fieldOf("biome_source").forGetter(OniChunkGenerator::biomeSource)
            ).apply(instance, ::OniChunkGenerator)
        }

        private fun placeGuaranteedResources(chunk: ChunkAccess, chunkPos: ChunkPos) {
            if (chunkPos.x != 0 || chunkPos.z != 0) {
                return
            }

            val podX = OniWorldLayout.POD_X
            val podZ = OniWorldLayout.POD_Z
            val podY = OniWorldLayout.POD_Y
            carveStarterRoom(chunk, podX, podY, podZ)
            chunk.setBlockState(
                BlockPos(podX, podY, podZ),
                OniBlockLookup.state(OniBlockIds.PRINTING_POD),
                0
            )

            val algaeState = OniBlockLookup.state(OniBlockIds.ALGAE)
            for (dx in -2..2) {
                for (dz in -2..2) {
                    chunk.setBlockState(BlockPos(podX + dx, podY - 1, podZ + dz), OniBlockLookup.state(OniBlockIds.SEDIMENTARY_ROCK), 0)
                }
            }

            chunk.setBlockState(BlockPos(podX + 4, podY - 2, podZ + 2), algaeState, 0)
            chunk.setBlockState(BlockPos(podX + 3, podY - 2, podZ + 2), algaeState, 0)
            chunk.setBlockState(BlockPos(podX + 4, podY - 2, podZ + 3), algaeState, 0)

            // Starter water pocket.
            for (dx in 6..9) {
                for (dz in -1..2) {
                    chunk.setBlockState(BlockPos(podX + dx, podY - 3, podZ + dz), OniBlockLookup.state(OniBlockIds.WATER), 0)
                }
            }

            // Early metal node. Using vanilla ore by design.
            chunk.setBlockState(BlockPos(podX - 6, podY - 3, podZ), Blocks.IRON_ORE.defaultBlockState(), 0)
        }

        private fun carveStarterRoom(chunk: ChunkAccess, centerX: Int, centerY: Int, centerZ: Int) {
            for (dx in -4..4) {
                for (dz in -4..4) {
                    for (dy in -1..4) {
                        chunk.setBlockState(BlockPos(centerX + dx, centerY + dy, centerZ + dz), Blocks.AIR.defaultBlockState(), 0)
                    }
                }
            }
        }

        private fun solidStateFor(x: Int, y: Int, z: Int, surfaceY: Int, lavaTop: Int): BlockState {
            val depthFromSurface = surfaceY - y
            val hash = hash(x, y, z)
            val borderDistance = min(
                min(abs(x - OniWorldLayout.WORLD_MIN_X), abs(OniWorldLayout.WORLD_MAX_X - x)),
                min(abs(z - OniWorldLayout.WORLD_MIN_Z), abs(OniWorldLayout.WORLD_MAX_Z - z))
            )
            if (borderDistance <= 1) {
                return OniBlockLookup.state(OniBlockIds.ABYSSALITE)
            }
            if (depthFromSurface <= TOPSOIL_DEPTH) {
                if ((hash and 0xFF) < 18) {
                    return OniBlockLookup.state(OniBlockIds.ALGAE)
                }
                return OniBlockLookup.state(OniBlockIds.REGOLITH)
            }
            if (depthFromSurface <= SEDIMENTARY_DEPTH) {
                if ((hash and 0xFF) < 16) {
                    return OniBlockLookup.state(OniBlockIds.POLLUTED_DIRT)
                }
                return OniBlockLookup.state(OniBlockIds.SEDIMENTARY_ROCK)
            }
            val igneousStart = lavaTop + IGNEOUS_DEPTH
            if (y <= igneousStart) {
                return OniBlockLookup.state(OniBlockIds.IGNEOUS_ROCK)
            }
            val oreRoll = (hash ushr 8) and 0xFF
            return when {
                // Reuse vanilla ores for now.
                oreRoll < 6 -> Blocks.COAL_ORE.defaultBlockState()
                oreRoll < 12 -> Blocks.COPPER_ORE.defaultBlockState()
                oreRoll < 16 -> Blocks.IRON_ORE.defaultBlockState()
                oreRoll < 18 -> OniBlockLookup.state(OniBlockIds.GRANITE)
                else -> OniBlockLookup.state(OniBlockIds.IGNEOUS_ROCK)
            }
        }

        private fun hash(x: Int, y: Int, z: Int): Int {
            var h = x * 734287
            h = h xor (z * 912271)
            h = h xor (y * 19349663)
            h *= 0x9E3779B9.toInt()
            return h
        }
    }
}
