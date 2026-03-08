package conservecraft.mixins.common

import conservecraft.common.world.OniVanillaFluidItemInterop
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BucketItem
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.BlockHitResult
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(BucketItem::class)
abstract class BucketItemMixin {
    @Shadow
    @Final
    private lateinit var content: Fluid

    @Inject(method = ["use"], at = [At("HEAD")], cancellable = true)
    private fun `conservecraft$useOniFluidRules`(
        level: Level,
        player: Player,
        hand: InteractionHand,
        cir: CallbackInfoReturnable<InteractionResult>,
    ) {
        val heldStack = player.getItemInHand(hand)
        if (!OniVanillaFluidItemInterop.shouldHandleBucketUse(heldStack)) {
            return
        }
        val result = OniVanillaFluidItemInterop.playerUse(level, player, hand, heldStack) ?: return
        cir.returnValue = result
    }

    @Inject(method = ["emptyContents"], at = [At("HEAD")], cancellable = true)
    private fun `conservecraft$emptyBucketIntoOniFluid`(
        user: LivingEntity?,
        level: Level,
        pos: BlockPos,
        hitResult: BlockHitResult,
        cir: CallbackInfoReturnable<Boolean>,
    ) {
        if (!OniVanillaFluidItemInterop.placeLiquidFromBucket(level, pos, content)) {
            return
        }
        cir.returnValue = true
    }
}
