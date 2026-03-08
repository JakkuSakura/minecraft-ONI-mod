package conservecraft.common.item

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.TooltipContext
import java.util.function.Consumer

open class SolidElementItem(
    properties: Item.Properties,
    val elementId: String,
    val unitMassKg: Double,
    private val unitSuffix: String?,
) : OniDescribedItem(properties) {
    override fun getName(stack: ItemStack): Component {
        val baseName = Component.translatable("item.conservecraft.${OniSolidItems.basePath(elementId)}")
        return if (unitSuffix == null) {
            baseName
        } else {
            Component.empty().append(baseName).append(" (${unitSuffix})")
        }
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        display: TooltipDisplay,
        tooltip: Consumer<Component>,
        flag: TooltipFlag,
    ) {
        OniItemTooltip.appendDetails(stack, tooltip, includeMass = false, includeTemperature = true)
    }
}
