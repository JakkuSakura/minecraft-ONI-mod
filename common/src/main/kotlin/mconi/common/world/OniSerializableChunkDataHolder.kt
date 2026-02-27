package mconi.common.world

import net.minecraft.nbt.CompoundTag

interface OniSerializableChunkDataHolder {
    fun `mconi$getOniChunkTag`(): CompoundTag?
    fun `mconi$setOniChunkTag`(tag: CompoundTag?)
}
