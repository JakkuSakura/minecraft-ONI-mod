package mconi.common.item

import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import java.util.function.Consumer

object OniItemTooltip {
    fun appendDetails(
        stack: ItemStack,
        tooltip: Consumer<Component>,
        includeMass: Boolean = true,
        includeTemperature: Boolean = true,
    ) {
        val data = stack.get(DataComponents.CUSTOM_DATA)
        val tag = data?.copyTag()

        if (includeMass) {
            val hasMassTag = tag?.contains(OniItemMass.TAG_MASS) == true
            if (hasMassTag) {
                val mass = OniItemMass.stackMass(stack)
                if (mass > 0.0) {
                    tooltip.accept(Component.literal(String.format("Mass: %.2f", mass)))
                }
            }
        }

        if (includeTemperature) {
            val tempK = if (tag != null && tag.contains(BottledMatterItem.TAG_TEMP_K)) {
                tag.getDouble(BottledMatterItem.TAG_TEMP_K).orElse(0.0)
            } else {
                0.0
            }
            if (tempK > 0.0) {
                tooltip.accept(Component.literal(String.format("Temp: %.1f K", tempK)))
            }
        }
    }
}
