package mconi.common.item

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.Container
import net.minecraft.world.item.component.CustomData
import kotlin.math.floor

object OniItemWeight {
    const val TAG_WEIGHT: String = "Weight"
    const val TAG_LEGACY_MASS: String = "Mass"

    fun stackWeight(stack: ItemStack): Double {
        if (stack.isEmpty) {
            return 0.0
        }
        val data = stack.get(DataComponents.CUSTOM_DATA) ?: return stack.count.toDouble()
        val tag = data.copyTag()
        if (tag.contains(TAG_WEIGHT)) {
            return tag.getDouble(TAG_WEIGHT).orElse(0.0)
        }
        if (tag.contains(TAG_LEGACY_MASS)) {
            return tag.getDouble(TAG_LEGACY_MASS).orElse(0.0)
        }
        return stack.count.toDouble()
    }

    fun setStackWeight(stack: ItemStack, weight: Double) {
        if (weight <= 0.0) {
            stack.count = 0
            return
        }
        stack.count = 1
        CustomData.update(DataComponents.CUSTOM_DATA, stack) { root ->
            root.putDouble(TAG_WEIGHT, weight)
        }
    }

    fun takeWeight(stack: ItemStack, weight: Double): Double {
        if (stack.isEmpty || weight <= 0.0) {
            return 0.0
        }
        val data = stack.get(DataComponents.CUSTOM_DATA)
        if (data != null && hasAnyWeightTag(data.copyTag())) {
            val current = stackWeight(stack)
            val take = minOf(current, weight)
            val remaining = current - take
            if (remaining <= 0.0) {
                stack.count = 0
            } else {
                setStackWeight(stack, remaining)
            }
            return take
        }

        val available = stack.count.toDouble()
        val take = minOf(available, floor(weight + 1e-9))
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
        val capacity = OniInventoryWeight.remainingCapacity(container)
        if (capacity <= 0.0) {
            return stack
        }
        val available = stackWeight(stack)
        val toAdd = minOf(available, capacity)
        if (toAdd <= 0.0) {
            return stack
        }
        val addStack = ItemStack(stack.item, 1)
        setStackWeight(addStack, toAdd)
        if (!mergeWeightStack(container, addStack)) {
            return stack
        }
        takeWeight(stack, toAdd)
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
            val weight = stackWeight(stack)
            if (weight <= 0.0) {
                continue
            }
            grouped[stack.item] = (grouped[stack.item] ?: 0.0) + weight
        }
        val merged: MutableList<ItemStack> = ArrayList(grouped.size)
        for ((item, weight) in grouped) {
            val mergedStack = ItemStack(item, 1)
            setStackWeight(mergedStack, weight)
            merged.add(mergedStack)
        }
        return merged
    }

    private fun mergeWeightStack(container: Container, stack: ItemStack): Boolean {
        val weight = stackWeight(stack)
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
            val next = stackWeight(current) + weight
            setStackWeight(current, next)
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
        return hasAnyWeightTag(data.copyTag())
    }

    private fun hasAnyWeightTag(tag: net.minecraft.nbt.CompoundTag): Boolean {
        return tag.contains(TAG_WEIGHT) || tag.contains(TAG_LEGACY_MASS)
    }
}
