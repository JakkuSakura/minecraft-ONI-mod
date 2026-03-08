package conservecraft.mixins.common

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.LayeredCauldronBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(LayeredCauldronBlock::class)
class LayeredCauldronBlockMixin {
    @Inject(method = ["handlePrecipitation"], at = [At("HEAD")], cancellable = true)
    private fun `conservecraft$disableWaterCauldronRefill`(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        precipitation: Biome.Precipitation,
        ci: CallbackInfo,
    ) {
        if (state.`is`(Blocks.WATER_CAULDRON) && precipitation == Biome.Precipitation.RAIN) {
            ci.cancel()
        }
    }

    @Inject(method = ["receiveStalactiteDrip"], at = [At("HEAD")], cancellable = true)
    private fun `conservecraft$disableWaterCauldronDrips`(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        fluid: Fluid,
        ci: CallbackInfo,
    ) {
        if (state.`is`(Blocks.WATER_CAULDRON) && fluid == Fluids.WATER) {
            ci.cancel()
        }
    }
}
