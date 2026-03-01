package mconi.common.item

import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import java.util.function.Consumer

object OniItemTooltip {
    fun appendDetails(
        stack: ItemStack,
        tooltip: Consumer<Component>,
        includeWeight: Boolean = true,
        includeTemperature: Boolean = true,
    ) {
        val data = stack.get(DataComponents.CUSTOM_DATA)
        val tag = data?.copyTag()

        if (includeWeight) {
            val hasWeightTag = tag?.contains(OniItemWeight.TAG_WEIGHT) == true
            if (hasWeightTag) {
                val weight = OniItemWeight.stackWeight(stack)
                if (weight > 0.0) {
                    tooltip.accept(Component.literal(String.format("Weight: %.2f", weight)))
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
