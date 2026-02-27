package mconi.mixins.common.server

import mconi.common.world.OniChunkDataAccess
import mconi.common.world.OniChunkDataNbt
import mconi.common.world.OniSerializableChunkDataHolder
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.LevelHeightAccessor
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.PalettedContainerFactory
import net.minecraft.world.level.chunk.ProtoChunk
import net.minecraft.world.level.chunk.storage.SerializableChunkData
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Unique
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(SerializableChunkData::class)
class SerializableChunkDataMixin : OniSerializableChunkDataHolder {
    @Unique
    private var oniChunkTag: CompoundTag? = null

    override fun `mconi$getOniChunkTag`(): CompoundTag? = oniChunkTag

    override fun `mconi$setOniChunkTag`(tag: CompoundTag?) {
        oniChunkTag = tag
    }

    @Inject(method = ["copyOf"], at = [At("RETURN")])
    private fun `mconi$copyOf`(
        level: ServerLevel,
        chunk: ChunkAccess,
        cir: CallbackInfoReturnable<SerializableChunkData>
    ) {
        val data = OniChunkDataAccess.chunkDataIfPresent(chunk) ?: return
        val tag = OniChunkDataNbt.writeChunkData(data) ?: return
        val value = cir.returnValue ?: return
        (value as OniSerializableChunkDataHolder).`mconi$setOniChunkTag`(tag)
    }

    @Inject(method = ["write"], at = [At("RETURN")])
    private fun `mconi$write`(cir: CallbackInfoReturnable<CompoundTag>) {
        val tag = oniChunkTag ?: return
        val out = cir.returnValue
        out.put(OniChunkDataNbt.ROOT_TAG, tag)
    }

    @Inject(method = ["parse"], at = [At("RETURN")])
    private fun `mconi$parse`(
        heightAccessor: LevelHeightAccessor,
        containerFactory: PalettedContainerFactory,
        tag: CompoundTag,
        cir: CallbackInfoReturnable<SerializableChunkData>
    ) {
        val dataTag = tag.getCompound(OniChunkDataNbt.ROOT_TAG).orElse(null) ?: return
        val value = cir.returnValue ?: return
        (value as OniSerializableChunkDataHolder).`mconi$setOniChunkTag`(dataTag)
    }

    @Inject(method = ["read"], at = [At("RETURN")])
    private fun `mconi$read`(
        level: ServerLevel,
        poiManager: net.minecraft.world.entity.ai.village.poi.PoiManager,
        storageInfo: net.minecraft.world.level.chunk.storage.RegionStorageInfo,
        chunkPos: net.minecraft.world.level.ChunkPos,
        cir: CallbackInfoReturnable<ProtoChunk>
    ) {
        val tag = oniChunkTag ?: return
        val chunk = cir.returnValue ?: return
        OniChunkDataNbt.readIntoChunk(chunk, tag)
    }
}
