package conservecraft.mixins.common

import conservecraft.common.world.OniVanillaFluidItemInterop
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.AbstractCauldronBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(AbstractCauldronBlock::class)
class AbstractCauldronBlockMixin {
    @Inject(method = ["useItemOn"], at = [At("HEAD")], cancellable = true)
    private fun `conservecraft$disableVanillaFluidCauldrons`(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult,
        cir: CallbackInfoReturnable<InteractionResult>,
    ) {
        if (!OniVanillaFluidItemInterop.shouldDisableCauldronInteraction(state, stack)) {
            return
        }
        cir.returnValue = InteractionResult.FAIL
    }
}
