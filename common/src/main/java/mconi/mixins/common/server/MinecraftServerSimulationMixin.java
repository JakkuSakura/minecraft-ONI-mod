package mconi.mixins.common.server;

import mconi.common.debug.OniDebugHttpServer;
import mconi.common.sim.OniServices;
import mconi.common.world.OniSimulationPersistence;
import mconi.common.world.OniWorldSampler;
import mconi.common.world.OniPlayerBreathing;
import mconi.common.world.OniPowerSampler;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerSimulationMixin
{
	@Inject(method = "tickServer", at = @At("HEAD"))
	private void mconi$onServerTick(BooleanSupplier hasTimeLeft, CallbackInfo ci)
	{
		MinecraftServer server = (MinecraftServer) (Object) this;
		if (server.overworld() != null) {
			OniSimulationPersistence.ensureLoaded(server.overworld());
		}
		OniPowerSampler.sampleAroundPlayers(server);
		OniWorldSampler.sampleAroundPlayers(server);
		OniDebugHttpServer.ensureStarted(server);
		OniServices.simulationRuntime().onServerTick();
		if (server.overworld() != null) {
			OniPlayerBreathing.apply(server.overworld());
		}
	}

	@Inject(method = "stopServer", at = @At("HEAD"))
	private void mconi$onServerStop(CallbackInfo ci)
	{
		MinecraftServer server = (MinecraftServer) (Object) this;
		if (server.overworld() != null) {
			OniSimulationPersistence.save(server.overworld());
		}
		OniDebugHttpServer.stop();
		OniServices.simulationRuntime().onServerStopped();
	}
}
