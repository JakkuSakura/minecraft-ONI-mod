package conservecraft.mixins.common

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.CauldronBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(CauldronBlock::class)
class CauldronBlockMixin {
    @Inject(method = ["handlePrecipitation"], at = [At("HEAD")], cancellable = true)
    private fun `conservecraft$disableRainWaterCauldrons`(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        precipitation: Biome.Precipitation,
        ci: CallbackInfo,
    ) {
        if (precipitation == Biome.Precipitation.RAIN) {
            ci.cancel()
        }
    }

    @Inject(method = ["receiveStalactiteDrip"], at = [At("HEAD")], cancellable = true)
    private fun `conservecraft$disableVanillaFluidDrips`(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        fluid: Fluid,
        ci: CallbackInfo,
    ) {
        if (fluid == Fluids.WATER || fluid == Fluids.LAVA) {
            ci.cancel()
        }
    }
}
