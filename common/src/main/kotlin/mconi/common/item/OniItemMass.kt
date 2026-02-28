package mconi.common.item

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.Container
import net.minecraft.world.item.component.CustomData
import kotlin.math.floor

object OniItemMass {
    const val TAG_WEIGHT_KG: String = "Weight"

    fun stackWeightKg(stack: ItemStack): Double {
        if (stack.isEmpty) {
            return 0.0
        }
        val data = stack.get(DataComponents.CUSTOM_DATA) ?: return stack.count.toDouble()
        val tag = data.copyTag()
        if (tag.contains(TAG_WEIGHT_KG)) {
            return tag.getDouble(TAG_WEIGHT_KG).orElse(0.0)
        }
        return stack.count.toDouble()
    }

    fun setStackWeightKg(stack: ItemStack, weightKg: Double) {
        if (weightKg <= 0.0) {
            stack.count = 0
            return
        }
        stack.count = 1
        CustomData.update(DataComponents.CUSTOM_DATA, stack) { root ->
            root.putDouble(TAG_WEIGHT_KG, weightKg)
        }
    }

    fun takeWeightKg(stack: ItemStack, weightKg: Double): Double {
        if (stack.isEmpty || weightKg <= 0.0) {
            return 0.0
        }
        val data = stack.get(DataComponents.CUSTOM_DATA)
        if (data != null && data.copyTag().contains(TAG_WEIGHT_KG)) {
            val current = stackWeightKg(stack)
            val take = minOf(current, weightKg)
            val remaining = current - take
            if (remaining <= 0.0) {
                stack.count = 0
            } else {
                setStackWeightKg(stack, remaining)
            }
            return take
        }

        val available = stack.count.toDouble()
        val take = minOf(available, floor(weightKg + 1e-9))
        if (take <= 0.0) {
            return 0.0
        }
        stack.count = (stack.count - take.toInt()).coerceAtLeast(0)
        return take
    }

    fun mergeIntoContainer(container: Container, stack: ItemStack): ItemStack {
        if (stack.isEmpty) {
            return stack
        }
        val capacity = OniInventoryWeights.remainingCapacityKg(container)
        if (capacity <= 0.0) {
            return stack
        }
        val available = stackWeightKg(stack)
        val toAdd = minOf(available, capacity)
        if (toAdd <= 0.0) {
            return stack
        }
        val addStack = ItemStack(stack.item, 1)
        setStackWeightKg(addStack, toAdd)
        if (!mergeWeightedStack(container, addStack)) {
            return stack
        }
        takeWeightKg(stack, toAdd)
        return stack
    }

    fun mergeStacksByWeight(stacks: List<ItemStack>): List<ItemStack> {
        if (stacks.isEmpty()) {
            return emptyList()
        }
        val grouped: MutableMap<net.minecraft.world.item.Item, Double> = LinkedHashMap()
        for (stack in stacks) {
            if (stack.isEmpty) {
                continue
            }
            val weight = stackWeightKg(stack)
            if (weight <= 0.0) {
                continue
            }
            grouped[stack.item] = (grouped[stack.item] ?: 0.0) + weight
        }
        val merged: MutableList<ItemStack> = ArrayList(grouped.size)
        for ((item, weight) in grouped) {
            val mergedStack = ItemStack(item, 1)
            setStackWeightKg(mergedStack, weight)
            merged.add(mergedStack)
        }
        return merged
    }

    private fun mergeWeightedStack(container: Container, stack: ItemStack): Boolean {
        val weight = stackWeightKg(stack)
        if (weight <= 0.0) {
            return false
        }
        val size = container.containerSize
        for (i in 0 until size) {
            val current = container.getItem(i)
            if (current.isEmpty) {
                continue
            }
            if (current.item != stack.item) {
                continue
            }
            if (!hasWeightTag(current)) {
                continue
            }
            val next = stackWeightKg(current) + weight
            setStackWeightKg(current, next)
            container.setChanged()
            return true
        }
        for (i in 0 until size) {
            val current = container.getItem(i)
            if (!current.isEmpty) {
                continue
            }
            container.setItem(i, stack)
            container.setChanged()
            return true
        }
        return false
    }

    private fun hasWeightTag(stack: ItemStack): Boolean {
        val data = stack.get(DataComponents.CUSTOM_DATA) ?: return false
        return data.copyTag().contains(TAG_WEIGHT_KG)
    }
}
