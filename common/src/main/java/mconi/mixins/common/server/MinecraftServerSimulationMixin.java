package mconi.mixins.common.server;

import mconi.common.debug.OniDebugHttpServer;
import mconi.common.sim.OniServices;
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
		OniDebugHttpServer.ensureStarted((MinecraftServer) (Object) this);
		OniServices.simulationRuntime().onServerTick();
	}

	@Inject(method = "stopServer", at = @At("HEAD"))
	private void mconi$onServerStop(CallbackInfo ci)
	{
		OniDebugHttpServer.stop();
		OniServices.simulationRuntime().onServerStopped();
	}
}
