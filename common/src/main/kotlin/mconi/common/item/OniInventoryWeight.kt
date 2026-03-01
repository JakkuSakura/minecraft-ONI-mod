package mconi.common.item

import net.minecraft.world.Container
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

object OniInventoryWeight {
    const val WEIGHT_PER_SLOT: Double = 64.0

    fun capacity(container: Container): Double {
        return container.containerSize.toDouble() * WEIGHT_PER_SLOT
    }

    fun totalWeight(container: Container): Double {
        var total = 0.0
        for (i in 0 until container.containerSize) {
            val stack = container.getItem(i)
            if (stack.isEmpty) {
                continue
            }
            total += stackWeight(stack)
        }
        return total
    }

    fun stackWeight(stack: ItemStack): Double {
        return OniItemWeight.stackWeight(stack)
    }

    fun perItemWeight(item: Item, stack: ItemStack? = null): Double {
        val spec = OniItemFactory.specByItem(item)
        val specWeight = spec?.properties?.mass
        if (specWeight != null) {
            return specWeight
        }
        val componentWeight = stack?.let { stackWeightFromComponents(it) }
        if (componentWeight != null) {
            return componentWeight
        }
        val maxStack = item.getDefaultMaxStackSize()
        return if (maxStack <= 0) {
            WEIGHT_PER_SLOT
        } else {
            WEIGHT_PER_SLOT / maxStack.toDouble()
        }
    }

    private fun stackWeightFromComponents(stack: ItemStack): Double? {
        if (stack.item !is BottledMatterItem) {
            return null
        }
        val data = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA) ?: return null
        val tag = data.copyTag()
        val weight = tag.getDouble(BottledMatterItem.TAG_WEIGHT).orElse(0.0)
        return if (weight > 0.0) weight else null
    }

    fun remainingCapacity(container: Container): Double {
        return (capacity(container) - totalWeight(container)).coerceAtLeast(0.0)
    }

    fun canFitStack(container: Container, stack: ItemStack, allowedSlots: Set<Int>? = null): Boolean {
        val available = remainingCapacityFiltered(container, allowedSlots)
        val stackWeight = stackWeight(stack)
        return stackWeight <= available + 1e-9
    }

    fun remainingCapacityFiltered(container: Container, allowedSlots: Set<Int>?): Double {
        if (allowedSlots == null) {
            return remainingCapacity(container)
        }
        var total = 0.0
        for (i in 0 until container.containerSize) {
            if (!allowedSlots.contains(i)) {
                continue
            }
            val stack = container.getItem(i)
            if (stack.isEmpty) {
                continue
            }
            total += stackWeight(stack)
        }
        val capacity = allowedSlots.size.toDouble() * WEIGHT_PER_SLOT
        return (capacity - total).coerceAtLeast(0.0)
    }

    fun inventorySlots(inventory: Inventory): Set<Int> {
        val slots = LinkedHashSet<Int>()
        for (i in 0 until inventory.containerSize) {
            slots.add(i)
        }
        return slots
    }
}
