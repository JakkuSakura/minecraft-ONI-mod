package mconi.common.world

import mconi.common.element.OniElements
import mconi.common.sim.model.OccupancyState
import mconi.common.sim.model.OniBlockData
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.chunk.ChunkAccess

object OniChunkDataNbt {
    const val ROOT_TAG: String = "mconi"
    private const val BLOCKS_TAG = "blocks"

    fun writeChunkData(data: OniChunkData): CompoundTag? {
        if (data.isEmpty()) {
            return null
        }
        val blocks = ListTag()
        for (entry in data.entries()) {
            blocks.add(writeBlockEntry(entry.pos, entry.data, data.chunkPos))
        }
        if (blocks.isEmpty()) {
            return null
        }
        val root = CompoundTag()
        root.put(BLOCKS_TAG, blocks)
        return root
    }

    fun readIntoChunk(chunk: ChunkAccess, tag: CompoundTag) {
        val list = tag.getList(BLOCKS_TAG).orElse(null) ?: return
        if (list.isEmpty()) {
            return
        }
        val data = OniChunkData(chunk.pos)
        for (i in 0 until list.size) {
            val entry = list.getCompoundOrEmpty(i)
            val blockPos = readBlockPos(entry, chunk.pos)
            val blockData = data.getOrCreate(blockPos)
            readBlockData(entry, blockData)
        }
        OniChunkDataAccess.attachChunkData(chunk, data)
    }

    private fun writeBlockEntry(pos: BlockPos, data: OniBlockData, chunkPos: ChunkPos): CompoundTag {
        val tag = CompoundTag()
        val relX = pos.x - chunkPos.minBlockX
        val relZ = pos.z - chunkPos.minBlockZ
        tag.putInt("x", relX)
        tag.putInt("y", pos.y)
        tag.putInt("z", relZ)
        tag.putString("occupancy", data.occupancyState().name)
        tag.putString("liquid", data.liquidId())
        tag.putDouble("liquidMass", data.liquidMassKg())
        tag.putDouble("temperatureK", data.temperatureK())
        tag.putDouble("pressureKpa", data.pressureKpa())
        tag.putDouble("o2Mass", data.gasMassKg(OniElements.GAS_OXYGEN))
        tag.putDouble("co2Mass", data.gasMassKg(OniElements.GAS_CARBON_DIOXIDE))
        tag.putDouble("h2Mass", data.gasMassKg(OniElements.GAS_HYDROGEN))
        tag.putBoolean("overheated", data.overheated())
        return tag
    }

    private fun readBlockPos(tag: CompoundTag, chunkPos: ChunkPos): BlockPos {
        val relX = tag.getIntOr("x", 0)
        val y = tag.getIntOr("y", 0)
        val relZ = tag.getIntOr("z", 0)
        return BlockPos(chunkPos.minBlockX + relX, y, chunkPos.minBlockZ + relZ)
    }

    private fun readBlockData(tag: CompoundTag, data: OniBlockData) {
        val occupancy = parseOccupancy(tag.getStringOr("occupancy", OccupancyState.VACUUM.name))
        data.setOccupancyState(occupancy)
        val liquidId = tag.getStringOr("liquid", OniElements.LIQUID_NONE)
        val parsed = OniElements.parseLiquidId(liquidId) ?: liquidId
        val normalized = if (parsed == OniElements.LIQUID_NONE || OniElements.isLiquid(parsed)) {
            parsed
        } else {
            OniElements.LIQUID_NONE
        }
        data.setLiquidState(normalized, tag.getDoubleOr("liquidMass", 0.0))
        data.setTemperatureK(tag.getDoubleOr("temperatureK", 293.15))
        data.setPressureKpa(tag.getDoubleOr("pressureKpa", 0.0))
        data.setGasMassKg(OniElements.GAS_OXYGEN, tag.getDoubleOr("o2Mass", 0.0))
        data.setGasMassKg(OniElements.GAS_CARBON_DIOXIDE, tag.getDoubleOr("co2Mass", 0.0))
        data.setGasMassKg(OniElements.GAS_HYDROGEN, tag.getDoubleOr("h2Mass", 0.0))
        data.setOverheated(tag.getBooleanOr("overheated", false))
    }

    private fun parseOccupancy(value: String): OccupancyState {
        val normalized = value.trim().uppercase()
        if (normalized == "FLUID") {
            return OccupancyState.LIQUID
        }
        return OccupancyState.valueOf(normalized)
    }
}
