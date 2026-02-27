package mconi.common.world

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.LevelChunk

object OniChunkDataAccess {
    @JvmStatic
    fun get(level: ServerLevel, pos: BlockPos): mconi.common.sim.model.OniBlockData? {
        val chunk = level.getChunkAt(pos)
        return chunkData(chunk).get(pos)
    }

    @JvmStatic
    fun getOrCreate(level: ServerLevel, pos: BlockPos): mconi.common.sim.model.OniBlockData {
        val chunk = level.getChunkAt(pos)
        return chunkData(chunk).getOrCreate(pos)
    }

    @JvmStatic
    fun blockEntries(level: ServerLevel): List<BlockEntryView> {
        val entries = ArrayList<BlockEntryView>()
        level.chunkSource.chunkMap.forEachReadyToSendChunk { chunk: LevelChunk ->
            chunkData(chunk).entriesInto(entries)
        }
        return entries
    }

    @JvmStatic
    fun blockCount(level: ServerLevel): Int {
        var total = 0
        level.chunkSource.chunkMap.forEachReadyToSendChunk { chunk: LevelChunk ->
            total += chunkData(chunk).blockCount()
        }
        return total
    }

    @JvmStatic
    fun chunkDataIfPresent(chunk: ChunkAccess): OniChunkData? {
        return (chunk as? OniChunkDataHolder)?.`mconi$getOniChunkData`()
    }

    @JvmStatic
    fun chunkData(chunk: ChunkAccess): OniChunkData {
        val holder = chunk as? OniChunkDataHolder
            ?: throw IllegalStateException("ChunkAccess missing OniChunkDataHolder mixin")
        val existing = holder.`mconi$getOniChunkData`()
        if (existing != null) {
            return existing
        }
        val created = OniChunkData(chunk.pos)
        holder.`mconi$setOniChunkData`(created)
        return created
    }

    @JvmStatic
    fun attachChunkData(chunk: ChunkAccess, data: OniChunkData?) {
        val holder = chunk as? OniChunkDataHolder
            ?: throw IllegalStateException("ChunkAccess missing OniChunkDataHolder mixin")
        holder.`mconi$setOniChunkData`(data)
    }

    @JvmStatic
    fun clearChunkData(chunk: ChunkAccess) {
        attachChunkData(chunk, null)
    }
}
