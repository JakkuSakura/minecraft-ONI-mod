package mconi.common.item

import net.minecraft.network.chat.Component
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.component.CustomData

class BottledMatterItem(
    properties: Properties,
    private val phase: MatterPhase,
    private val mass: Double,
    private val temperatureK: Double
) : OniDescribedItem(properties.stacksTo(16)) {
    override fun getDefaultInstance(): ItemStack {
        val stack = super.getDefaultInstance()
        CustomData.update(DataComponents.CUSTOM_DATA, stack) { root ->
            root.putDouble(TAG_MASS, mass)
            root.putDouble(TAG_TEMP_K, temperatureK)
        }
        return stack
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        display: TooltipDisplay,
        tooltip: java.util.function.Consumer<Component>,
        flag: TooltipFlag
    ) {
        val data = stack.get(DataComponents.CUSTOM_DATA)
        val tag = data?.copyTag()
        val mass = tag?.getDouble(TAG_MASS) ?: this.mass
        val temp = tag?.getDouble(TAG_TEMP_K) ?: temperatureK
        tooltip.accept(Component.literal("Phase: ${phase.name}"))
        tooltip.accept(Component.literal(String.format("Mass: %.2f", mass)))
        tooltip.accept(Component.literal(String.format("Temp: %.1f K", temp)))
        OniItemTooltip.appendDetails(stack, tooltip, includeMass = false, includeTemperature = false)
    }

    companion object {
        const val TAG_MASS = "Mass"
        const val TAG_TEMP_K = "TemperatureK"
    }
}

enum class MatterPhase {
    GAS,
    LIQUID
}
