package mconi.common.item

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.item.Item.TooltipContext

open class OniDescribedItem(properties: Properties) : Item(properties) {
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        display: TooltipDisplay,
        tooltip: java.util.function.Consumer<Component>,
        flag: TooltipFlag
    ) {
        OniItemTooltip.appendDetails(stack, tooltip)
    }
}
