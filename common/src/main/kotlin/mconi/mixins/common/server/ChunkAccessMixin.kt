package mconi.mixins.common.server

import mconi.common.world.OniChunkData
import mconi.common.world.OniChunkDataHolder
import net.minecraft.world.level.chunk.ChunkAccess
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Unique

@Mixin(ChunkAccess::class)
class ChunkAccessMixin : OniChunkDataHolder {
    @Unique
    private var oniChunkData: OniChunkData? = null

    override fun `mconi$getOniChunkData`(): OniChunkData? = oniChunkData

    override fun `mconi$setOniChunkData`(data: OniChunkData?) {
        oniChunkData = data
    }
}
