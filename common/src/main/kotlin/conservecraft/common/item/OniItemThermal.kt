package conservecraft.common.item

import conservecraft.common.thermal.OniThermalMath
import net.minecraft.core.component.DataComponents
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData

object OniItemThermal {
    const val DEFAULT_TEMP_K: Double = 293.15

    fun temperatureK(stack: ItemStack): Double {
        val data = stack.get(DataComponents.CUSTOM_DATA) ?: return DEFAULT_TEMP_K
        val tag = data.copyTag()
        if (!tag.contains(BottledMatterItem.TAG_TEMP_K)) {
            return DEFAULT_TEMP_K
        }
        return tag.getDouble(BottledMatterItem.TAG_TEMP_K).orElse(DEFAULT_TEMP_K)
    }

    fun setTemperatureK(stack: ItemStack, temperatureK: Double) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack) { root ->
            root.putDouble(BottledMatterItem.TAG_TEMP_K, temperatureK)
        }
    }

    fun conservedCraftingTemperatureK(container: Container): Double {
        val stacks = ArrayList<ItemStack>(container.containerSize)
        for (index in 0 until container.containerSize) {
            val stack = container.getItem(index)
            if (!stack.isEmpty) {
                stacks.add(stack)
            }
        }
        return OniThermalMath.averageItemTemperatureK(stacks, DEFAULT_TEMP_K)
    }

    fun hasMassTag(stack: ItemStack): Boolean {
        if (stack.count != 1) {
            return false
        }
        val data = stack.get(DataComponents.CUSTOM_DATA) ?: return false
        return data.copyTag().contains(OniItemMass.TAG_MASS)
    }
}
