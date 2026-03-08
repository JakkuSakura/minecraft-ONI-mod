package conservecraft.mixins.common

import conservecraft.common.item.OniItemThermal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.inventory.ResultSlot
import net.minecraft.world.item.ItemStack
import org.spongepowered.asm.mixin.Final
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(ResultSlot::class)
class CraftingResultTemperatureMixin {
    @Shadow
    @Final
    private lateinit var craftSlots: CraftingContainer

    @Inject(method = ["onTake"], at = [At("HEAD")])
    @Suppress("UNUSED_PARAMETER")
    private fun `conservecraft$applyCraftedTemperature`(player: Player, stack: ItemStack, ci: CallbackInfo) {
        if (stack.isEmpty) {
            return
        }
        OniItemThermal.setTemperatureK(stack, OniItemThermal.conservedCraftingTemperatureK(craftSlots))
    }
}
