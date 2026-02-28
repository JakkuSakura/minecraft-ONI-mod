package mconi.common.item

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.item.Item.TooltipContext

class BottledMatterItem(
    properties: Properties,
    private val phase: MatterPhase,
    private val massKg: Double,
    private val temperatureK: Double
) : Item(properties.stacksTo(16)) {
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        display: TooltipDisplay,
        tooltip: java.util.function.Consumer<Component>,
        flag: TooltipFlag
    ) {
        val data = stack.get(DataComponents.CUSTOM_DATA)
        val tag = data?.copyTag()
        val phaseName = tag?.getString(TAG_PHASE) ?: phase.name
        val mass = tag?.getDouble(TAG_WEIGHT_KG) ?: massKg
        val temp = tag?.getDouble(TAG_TEMP_K) ?: temperatureK
        tooltip.accept(Component.literal("Phase: $phaseName"))
        tooltip.accept(Component.literal(String.format("Mass: %.2f kg", mass)))
        tooltip.accept(Component.literal(String.format("Temp: %.1f K", temp)))
    }

    companion object {
        const val TAG_PHASE = "mconi_phase"
        const val TAG_WEIGHT_KG = "Weight"
        const val TAG_TEMP_K = "mconi_temp_k"
    }
}

enum class MatterPhase {
    GAS,
    LIQUID
}
