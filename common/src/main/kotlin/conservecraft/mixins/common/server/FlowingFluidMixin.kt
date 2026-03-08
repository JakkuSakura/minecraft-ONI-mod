package conservecraft.mixins.common.server

import conservecraft.common.world.OniVanillaFluidInterop
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.FlowingFluid
import net.minecraft.world.level.material.FluidState
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(FlowingFluid::class)
class FlowingFluidMixin {
    @Inject(method = ["tick"], at = [At("HEAD")], cancellable = true)
    @Suppress("UNUSED_PARAMETER")
    private fun `conservecraft$replaceVanillaFluidTick`(
        level: ServerLevel,
        pos: BlockPos,
        state: BlockState,
        fluidState: FluidState,
        ci: CallbackInfo,
    ) {
        if (!OniVanillaFluidInterop.isVanillaManagedFluid(fluidState)) {
            return
        }
        OniVanillaFluidInterop.convertVanillaFluid(level, pos, fluidState)
        ci.cancel()
    }
}
