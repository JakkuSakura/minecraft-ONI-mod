package conservecraft.common.item

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.Container
import net.minecraft.world.item.component.CustomData
import kotlin.math.floor

object OniItemMass {
    const val TAG_MASS: String = "Mass"

    fun stackMass(stack: ItemStack): Double {
        if (stack.isEmpty) {
            return 0.0
        }
        val data = stack.get(DataComponents.CUSTOM_DATA) ?: return stack.count.toDouble()
        val tag = data.copyTag()
        if (tag.contains(TAG_MASS)) {
            return tag.getDouble(TAG_MASS).orElse(0.0)
        }
        return stack.count.toDouble()
    }

    fun setStackMass(stack: ItemStack, mass: Double) {
        if (mass <= 0.0) {
            stack.count = 0
            return
        }
        stack.count = 1
        CustomData.update(DataComponents.CUSTOM_DATA, stack) { root ->
            root.putDouble(TAG_MASS, mass)
        }
    }

    fun takeMass(stack: ItemStack, mass: Double): Double {
        if (stack.isEmpty || mass <= 0.0) {
            return 0.0
        }
        val data = stack.get(DataComponents.CUSTOM_DATA)
        if (data != null && hasMassTag(stack)) {
            val current = stackMass(stack)
            val take = minOf(current, mass)
            val remaining = current - take
            if (remaining <= 0.0) {
                stack.count = 0
            } else {
                setStackMass(stack, remaining)
            }
            return take
        }

        val available = stack.count.toDouble()
        val take = minOf(available, floor(mass + 1e-9))
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
        val capacity = OniInventoryMass.remainingCapacity(container)
        if (capacity <= 0.0) {
            return stack
        }
        val available = stackMass(stack)
        val toAdd = minOf(available, capacity)
        if (toAdd <= 0.0) {
            return stack
        }
        val addStack = ItemStack(stack.item, 1)
        setStackMass(addStack, toAdd)
        if (!mergeMassStack(container, addStack)) {
            return stack
        }
        takeMass(stack, toAdd)
        return stack
    }

    fun mergeStacksByMass(stacks: List<ItemStack>): List<ItemStack> {
        if (stacks.isEmpty()) {
            return emptyList()
        }
        val grouped: MutableMap<net.minecraft.world.item.Item, Double> = LinkedHashMap()
        for (stack in stacks) {
            if (stack.isEmpty) {
                continue
            }
            val mass = stackMass(stack)
            if (mass <= 0.0) {
                continue
            }
            grouped[stack.item] = (grouped[stack.item] ?: 0.0) + mass
        }
        val merged: MutableList<ItemStack> = ArrayList(grouped.size)
        for ((item, mass) in grouped) {
            val mergedStack = ItemStack(item, 1)
            setStackMass(mergedStack, mass)
            merged.add(mergedStack)
        }
        return merged
    }

    private fun mergeMassStack(container: Container, stack: ItemStack): Boolean {
        val mass = stackMass(stack)
        if (mass <= 0.0) {
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
            if (!hasMassTag(current)) {
                continue
            }
            val next = stackMass(current) + mass
            setStackMass(current, next)
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

    private fun hasMassTag(stack: ItemStack): Boolean {
        val data = stack.get(DataComponents.CUSTOM_DATA) ?: return false
        return data.copyTag().contains(TAG_MASS)
    }
}
