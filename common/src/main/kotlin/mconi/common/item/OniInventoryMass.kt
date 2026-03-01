package mconi.common.item

import net.minecraft.world.Container
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

object OniInventoryMass {
    const val MASS_PER_SLOT: Double = 64.0

    fun capacity(container: Container): Double {
        return container.containerSize.toDouble() * MASS_PER_SLOT
    }

    fun totalMass(container: Container): Double {
        var total = 0.0
        for (i in 0 until container.containerSize) {
            val stack = container.getItem(i)
            if (stack.isEmpty) {
                continue
            }
            total += stackMass(stack)
        }
        return total
    }

    fun stackMass(stack: ItemStack): Double {
        return OniItemMass.stackMass(stack)
    }

    fun perItemMass(item: Item, stack: ItemStack? = null): Double {
        val spec = OniItemFactory.specByItem(item)
        val specMass = spec?.properties?.mass
        if (specMass != null) {
            return specMass
        }
        val componentMass = stack?.let { stackMassFromComponents(it) }
        if (componentMass != null) {
            return componentMass
        }
        val maxStack = item.getDefaultMaxStackSize()
        return if (maxStack <= 0) {
            MASS_PER_SLOT
        } else {
            MASS_PER_SLOT / maxStack.toDouble()
        }
    }

    private fun stackMassFromComponents(stack: ItemStack): Double? {
        if (stack.item !is BottledMatterItem) {
            return null
        }
        val data = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA) ?: return null
        val tag = data.copyTag()
        val mass = tag.getDouble(BottledMatterItem.TAG_MASS).orElse(0.0)
        return if (mass > 0.0) mass else null
    }

    fun remainingCapacity(container: Container): Double {
        return (capacity(container) - totalMass(container)).coerceAtLeast(0.0)
    }

    fun canFitStack(container: Container, stack: ItemStack, allowedSlots: Set<Int>? = null): Boolean {
        val available = remainingCapacityFiltered(container, allowedSlots)
        val stackMass = stackMass(stack)
        return stackMass <= available + 1e-9
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
            total += stackMass(stack)
        }
        val capacity = allowedSlots.size.toDouble() * MASS_PER_SLOT
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
