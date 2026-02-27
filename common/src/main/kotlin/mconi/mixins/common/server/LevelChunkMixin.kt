package mconi.mixins.common.server

import mconi.common.world.OniChunkDataAccess
import mconi.common.world.OniChunkDataHolder
import net.minecraft.world.level.chunk.LevelChunk
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(LevelChunk::class)
class LevelChunkMixin {
    @Inject(method = ["setLoaded"], at = [At("HEAD")])
    private fun `mconi$onSetLoaded`(loaded: Boolean, ci: CallbackInfo) {
        if (loaded) {
            return
        }
        @Suppress("CAST_NEVER_SUCCEEDS")
        val chunk = this as LevelChunk
        val holder = chunk as? OniChunkDataHolder ?: return
        val data = holder.`mconi$getOniChunkData`() ?: return
        data.clear()
        OniChunkDataAccess.clearChunkData(chunk)
    }
}
