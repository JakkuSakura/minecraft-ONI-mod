package mconi.common.world

import mconi.common.sim.model.OniBlockData
import net.minecraft.core.BlockPos
import net.minecraft.world.level.ChunkPos

class OniChunkData(val chunkPos: ChunkPos) {
    private val blocks: MutableMap<Long, OniBlockData> = HashMap()

    fun get(pos: BlockPos): OniBlockData? = blocks[pos.asLong()]

    fun getOrCreate(pos: BlockPos): OniBlockData {
        val key = pos.asLong()
        val existing = blocks[key]
        if (existing != null) {
            return existing
        }
        val created = OniBlockData()
        blocks[key] = created
        return created
    }

    fun isEmpty(): Boolean = blocks.isEmpty()

    fun entries(): List<BlockEntryView> {
        val entries = ArrayList<BlockEntryView>(blocks.size)
        for ((key, data) in blocks) {
            entries.add(BlockEntryView(BlockPos.of(key), data))
        }
        return entries
    }

    fun entriesInto(target: MutableList<BlockEntryView>) {
        for ((key, data) in blocks) {
            target.add(BlockEntryView(BlockPos.of(key), data))
        }
    }

    fun blockCount(): Int = blocks.size

    fun clear() {
        blocks.clear()
    }

    fun rawBlocks(): MutableMap<Long, OniBlockData> = blocks
}

class BlockEntryView(val pos: BlockPos, val data: OniBlockData)
