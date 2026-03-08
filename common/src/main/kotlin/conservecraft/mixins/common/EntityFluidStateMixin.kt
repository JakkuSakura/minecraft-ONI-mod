package conservecraft.mixins.common

import conservecraft.common.world.OniEntityFluidInterop
import it.unimi.dsi.fastutil.objects.Object2DoubleMap
import net.minecraft.tags.FluidTags
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.material.Fluid
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(Entity::class)
abstract class EntityFluidStateMixin {
    @Shadow
    protected var wasTouchingWater: Boolean = false

    @Shadow
    protected lateinit var fluidHeight: Object2DoubleMap<TagKey<Fluid>>

    @Inject(method = ["baseTick"], at = [At("TAIL")])
    private fun `conservecraft$applyOniLiquidContactEffects`(ci: CallbackInfo) {
        OniEntityFluidInterop.applyContactEffects(this as Entity)
    }

    @Inject(method = ["isInWater"], at = [At("HEAD")], cancellable = true)
    private fun `conservecraft$reportOniWater`(cir: CallbackInfoReturnable<Boolean>) {
        if (OniEntityFluidInterop.isInOniWater(this as Entity)) {
            cir.returnValue = true
        }
    }

    @Inject(method = ["isUnderWater"], at = [At("HEAD")], cancellable = true)
    private fun `conservecraft$reportOniUnderWater`(cir: CallbackInfoReturnable<Boolean>) {
        val entity = this as Entity
        if (OniEntityFluidInterop.isInOniWater(entity) && OniEntityFluidInterop.isEyeInOniWater(entity)) {
            cir.returnValue = true
        }
    }

    @Inject(method = ["isInLava"], at = [At("HEAD")], cancellable = true)
    private fun `conservecraft$reportOniLava`(cir: CallbackInfoReturnable<Boolean>) {
        if (OniEntityFluidInterop.isInOniLava(this as Entity)) {
            cir.returnValue = true
        }
    }

    @Inject(method = ["updateInWaterStateAndDoFluidPushing"], at = [At("RETURN")], cancellable = true)
    private fun `conservecraft$includeOniLiquidsInFluidState`(cir: CallbackInfoReturnable<Boolean>) {
        val entity = this as Entity
        val inOniWater = OniEntityFluidInterop.isInOniWater(entity)
        val inOniLava = OniEntityFluidInterop.isInOniLava(entity)
        if (inOniWater) {
            wasTouchingWater = true
        }
        if (inOniLava) {
            fluidHeight.put(FluidTags.LAVA, maxOf(fluidHeight.getDouble(FluidTags.LAVA), 1.0))
        }
        if (inOniWater || inOniLava) {
            cir.returnValue = cir.returnValue || inOniWater || inOniLava
        }
    }
}
