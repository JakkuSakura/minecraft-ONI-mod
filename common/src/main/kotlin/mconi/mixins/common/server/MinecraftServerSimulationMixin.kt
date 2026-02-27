package mconi.mixins.common.server

import mconi.common.debug.OniDebugHttpServer
import mconi.common.sim.OniServices
import mconi.common.world.OniPlayerBreathing
import mconi.common.world.OniPowerSampler
import mconi.common.world.OniWorldSampler
import mconi.common.world.OniWorldWriter
import net.minecraft.server.MinecraftServer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import java.util.function.BooleanSupplier

@Mixin(MinecraftServer::class)
class MinecraftServerSimulationMixin {
    @Inject(method = ["tickServer"], at = [At("HEAD")])
    @Suppress("UNUSED_PARAMETER")
    private fun `mconi$onServerTick`(hasTimeLeft: BooleanSupplier, ci: CallbackInfo) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        val server = this as MinecraftServer
        OniPowerSampler.sampleAroundPlayers(server)
        OniWorldSampler.sampleAroundPlayers(server)
        OniDebugHttpServer.ensureStarted(server)
        OniServices.simulationRuntime().onServerTick(server)
        OniWorldWriter.applyAroundPlayers(server)
        OniPlayerBreathing.apply(server.overworld())
    }

    @Inject(method = ["stopServer"], at = [At("HEAD")])
    @Suppress("UNUSED_PARAMETER")
    private fun `mconi$onServerStop`(ci: CallbackInfo) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        val server = this as MinecraftServer
        OniDebugHttpServer.stop()
        OniServices.simulationRuntime().onServerStopped()
    }
}
