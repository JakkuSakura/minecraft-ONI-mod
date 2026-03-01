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
import net.minecraft.world.level.block.EntityBlock
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
import mconi.common.block.OniBlockFactory
import mconi.common.block.entity.OniElementBlockEntity
import java.util.Optional

class OniChunkGenerator(
    biomeSource: BiomeSource,
    private val minZ: Int,
    private val maxZ: Int
) : ChunkGenerator(biomeSource) {
    override fun codec(): MapCodec<out ChunkGenerator> = CODEC

    override fun validate() {
    }

    fun minZ(): Int = minZ
    fun maxZ(): Int = maxZ
    fun zThickness(): Int = maxZ - minZ + 1

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

    override fun getGenDepth(): Int = FALLBACK_HEIGHT

    override fun getSeaLevel(): Int = 63

    override fun getMinY(): Int = FALLBACK_MIN_Y

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
                    worldZ in minZ..maxZ
                val zBorderEnabled = (maxZ - minZ) >= 2
                val onBorder = worldX == OniWorldLayout.WORLD_MIN_X || worldX == OniWorldLayout.WORLD_MAX_X ||
                    (zBorderEnabled && (worldZ == minZ || worldZ == maxZ))

                for (y in minY..maxY) {
                    val state = when {
                        !inBounds -> Blocks.BARRIER.stateDefinition.any()
                        onBorder -> Blocks.BARRIER.stateDefinition.any()
                        y < activeMinY -> Blocks.BARRIER.stateDefinition.any()
                        y == activeMinY -> Blocks.BARRIER.stateDefinition.any()
                        y <= lavaTop -> OniBlockLookup.state(OniBlockFactory.LAVA)
                        y > activeMaxY -> Blocks.AIR.stateDefinition.any()
                        y >= spaceStart -> Blocks.AIR.stateDefinition.any()
                        y <= surfaceY -> solidStateFor(worldX, y, worldZ, surfaceY, lavaTop, minZ, maxZ)
                        else -> Blocks.AIR.stateDefinition.any()
                    }
                    setBlock(chunk, BlockPos(worldX, y, worldZ), state)
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
            z in minZ..maxZ
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
            z in minZ..maxZ
        val zBorderEnabled = (maxZ - minZ) >= 2
        val onBorder = x == OniWorldLayout.WORLD_MIN_X || x == OniWorldLayout.WORLD_MAX_X ||
            (zBorderEnabled && (z == minZ || z == maxZ))
        for (y in minY..maxY) {
            val state = when {
                !inBounds -> Blocks.BARRIER.stateDefinition.any()
                onBorder -> Blocks.BARRIER.stateDefinition.any()
                y < activeMinY -> Blocks.BARRIER.stateDefinition.any()
                y == activeMinY -> Blocks.BARRIER.stateDefinition.any()
                y <= lavaTop -> OniBlockLookup.state(OniBlockFactory.LAVA)
                y > activeMaxY -> Blocks.AIR.stateDefinition.any()
                y >= spaceStart -> Blocks.AIR.stateDefinition.any()
                y <= surfaceY -> solidStateFor(x, y, z, surfaceY, lavaTop, minZ, maxZ)
                else -> Blocks.AIR.stateDefinition.any()
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
        private const val FALLBACK_MIN_Y = -128
        private const val FALLBACK_HEIGHT = 256
        private const val TOPSOIL_DEPTH = OniWorldLayout.TOPSOIL_DEPTH
        private const val SEDIMENTARY_DEPTH = OniWorldLayout.SEDIMENTARY_DEPTH
        private const val IGNEOUS_DEPTH = OniWorldLayout.IGNEOUS_DEPTH

        @JvmField
        val CODEC: MapCodec<OniChunkGenerator> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                BiomeSource.CODEC.fieldOf("biome_source").forGetter(OniChunkGenerator::biomeSource),
                com.mojang.serialization.Codec.INT.optionalFieldOf("min_z")
                    .forGetter { generator -> Optional.of(generator.minZ) },
                com.mojang.serialization.Codec.INT.optionalFieldOf("max_z")
                    .forGetter { generator -> Optional.of(generator.maxZ) }
            ).apply(instance) { biomeSource, minZOpt, maxZOpt ->
                val minZ = if (minZOpt.isPresent) minZOpt.get() else null
                val maxZ = if (maxZOpt.isPresent) maxZOpt.get() else null
                val bounds = OniWorldgenConfig.resolveBounds(minZ, maxZ)
                OniChunkGenerator(biomeSource, bounds.minZ, bounds.maxZ)
            }
        }

        private fun setBlock(chunk: ChunkAccess, pos: BlockPos, state: BlockState) {
            chunk.setBlockState(pos, state, 0)
            val defaults = OniBlockFactory.defaultElements(state.block)
            if (defaults.isEmpty()) {
                return
            }
            val entityBlock = state.block as? EntityBlock ?: return
            val entity = entityBlock.newBlockEntity(pos, state) as? OniElementBlockEntity ?: return
            entity.setElements(defaults)
            chunk.setBlockEntity(entity)
        }

        private fun placeGuaranteedResources(chunk: ChunkAccess, chunkPos: ChunkPos) {
            if (chunkPos.x != 0 || chunkPos.z != 0) {
                return
            }

            val podX = OniWorldLayout.POD_X
            val podZ = OniWorldLayout.POD_Z
            val podY = OniWorldLayout.POD_Y
            carveStarterRoom(chunk, podX, podY, podZ)
            setBlock(chunk, BlockPos(podX, podY, podZ), OniBlockLookup.state(OniBlockFactory.PRINTING_POD))

            val algaeState = OniBlockLookup.state(OniBlockFactory.ALGAE)
            for (dx in -2..2) {
                for (dz in -2..2) {
                    setBlock(chunk, BlockPos(podX + dx, podY - 1, podZ + dz), OniBlockLookup.state(OniBlockFactory.SEDIMENTARY_ROCK))
                }
            }

            setBlock(chunk, BlockPos(podX + 4, podY - 2, podZ + 2), algaeState)
            setBlock(chunk, BlockPos(podX + 3, podY - 2, podZ + 2), algaeState)
            setBlock(chunk, BlockPos(podX + 4, podY - 2, podZ + 3), algaeState)

            // Starter lighting in the printing pod room.
            val torchState = Blocks.TORCH.stateDefinition.any()
            setBlock(chunk, BlockPos(podX + 3, podY, podZ + 3), torchState)
            setBlock(chunk, BlockPos(podX - 3, podY, podZ + 3), torchState)
            setBlock(chunk, BlockPos(podX + 3, podY, podZ - 3), torchState)
            setBlock(chunk, BlockPos(podX - 3, podY, podZ - 3), torchState)

            // Starter water pocket.
            for (dx in 6..9) {
                for (dz in -1..2) {
                    setBlock(chunk, BlockPos(podX + dx, podY - 3, podZ + dz), OniBlockLookup.state(OniBlockFactory.WATER))
                }
            }

            // Early metal node. Using vanilla ore by design.
            setBlock(chunk, BlockPos(podX - 6, podY - 3, podZ), Blocks.IRON_ORE.stateDefinition.any())
        }

        private fun carveStarterRoom(chunk: ChunkAccess, centerX: Int, centerY: Int, centerZ: Int) {
            for (dx in -4..4) {
                for (dz in -4..4) {
                    for (dy in -1..4) {
                        setBlock(chunk, BlockPos(centerX + dx, centerY + dy, centerZ + dz), Blocks.AIR.stateDefinition.any())
                    }
                }
            }
        }

        private fun solidStateFor(
            x: Int,
            y: Int,
            z: Int,
            surfaceY: Int,
            lavaTop: Int,
            minZ: Int,
            maxZ: Int
        ): BlockState {
            val depthFromSurface = surfaceY - y
            val hash = hash(x, y, z)
            val borderDistance = min(
                min(abs(x - OniWorldLayout.WORLD_MIN_X), abs(OniWorldLayout.WORLD_MAX_X - x)),
                min(abs(z - minZ), abs(maxZ - z))
            )
            if (borderDistance <= 1) {
                return OniBlockLookup.state(OniBlockFactory.ABYSSALITE)
            }
            if (depthFromSurface <= TOPSOIL_DEPTH) {
                if ((hash and 0xFF) < 18) {
                    return OniBlockLookup.state(OniBlockFactory.ALGAE)
                }
                return OniBlockLookup.state(OniBlockFactory.REGOLITH)
            }
            if (depthFromSurface <= SEDIMENTARY_DEPTH) {
                if ((hash and 0xFF) < 16) {
                    return OniBlockLookup.state(OniBlockFactory.POLLUTED_DIRT)
                }
                return OniBlockLookup.state(OniBlockFactory.SEDIMENTARY_ROCK)
            }
            val igneousStart = lavaTop + IGNEOUS_DEPTH
            if (y <= igneousStart) {
                return OniBlockLookup.state(OniBlockFactory.IGNEOUS_ROCK)
            }
            val oreRoll = (hash ushr 8) and 0xFF
            return when {
                // Reuse vanilla ores for now.
                oreRoll < 6 -> Blocks.COAL_ORE.stateDefinition.any()
                oreRoll < 12 -> Blocks.COPPER_ORE.stateDefinition.any()
                oreRoll < 16 -> Blocks.IRON_ORE.stateDefinition.any()
                oreRoll < 18 -> OniBlockLookup.state(OniBlockFactory.GRANITE)
                else -> OniBlockLookup.state(OniBlockFactory.IGNEOUS_ROCK)
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
