package conservecraft.mixins.common.server

import conservecraft.common.debug.OniDebugHttpServer
import conservecraft.common.sim.OniServices
import conservecraft.common.world.OniPlayerBreathing
import net.minecraft.server.MinecraftServer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.function.BooleanSupplier

@Mixin(MinecraftServer::class)
class MinecraftServerSystemMixin {
    @Inject(method = ["tickServer"], at = [At("HEAD")])
    @Suppress("UNUSED_PARAMETER")
    private fun `conservecraft$onServerTick`(hasTimeLeft: BooleanSupplier, ci: CallbackInfo) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        val server = this as MinecraftServer
        OniDebugHttpServer.ensureStarted(server)
        OniServices.systemRuntime().onServerTick(server)
        OniPlayerBreathing.apply(server.overworld())
    }

    @Inject(method = ["stopServer"], at = [At("HEAD")])
    @Suppress("UNUSED_PARAMETER")
    private fun `conservecraft$onServerStop`(ci: CallbackInfo) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        val server = this as MinecraftServer
        OniDebugHttpServer.stop()
        OniServices.systemRuntime().onServerStopped()
    }
}
