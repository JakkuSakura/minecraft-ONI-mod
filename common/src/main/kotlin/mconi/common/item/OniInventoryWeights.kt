package mconi.common.item

import net.minecraft.world.Container
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

object OniInventoryWeights {
    const val KG_PER_SLOT: Double = 64.0

    fun capacityKg(container: Container): Double {
        return container.containerSize.toDouble() * KG_PER_SLOT
    }

    fun totalWeightKg(container: Container): Double {
        var total = 0.0
        for (i in 0 until container.containerSize) {
            val stack = container.getItem(i)
            if (stack.isEmpty) {
                continue
            }
            total += stackWeightKg(stack)
        }
        return total
    }

    fun stackWeightKg(stack: ItemStack): Double {
        val perItem = perItemMassKg(stack.item, stack)
        return OniItemMass.stackWeightKg(stack)
    }

    fun perItemMassKg(item: Item, stack: ItemStack? = null): Double {
        val spec = OniItemFactory.specByItem(item)
        val specMass = spec?.properties?.massKg
        if (specMass != null) {
            return specMass
        }
        val componentMass = stack?.let { stackMassFromComponents(it) }
        if (componentMass != null) {
            return componentMass
        }
        val maxStack = item.getDefaultMaxStackSize()
        return if (maxStack <= 0) {
            KG_PER_SLOT
        } else {
            KG_PER_SLOT / maxStack.toDouble()
        }
    }

    private fun stackMassFromComponents(stack: ItemStack): Double? {
        if (stack.item !is BottledMatterItem) {
            return null
        }
        val data = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA) ?: return null
        val tag = data.copyTag()
        val mass = tag.getDouble(BottledMatterItem.TAG_WEIGHT_KG).orElse(0.0)
        return if (mass > 0.0) mass else null
    }

    fun remainingCapacityKg(container: Container): Double {
        return (capacityKg(container) - totalWeightKg(container)).coerceAtLeast(0.0)
    }

    fun canFitStack(container: Container, stack: ItemStack, allowedSlots: Set<Int>? = null): Boolean {
        val available = remainingCapacityKgFiltered(container, allowedSlots)
        val stackWeight = stackWeightKg(stack)
        return stackWeight <= available + 1e-9
    }

    fun remainingCapacityKgFiltered(container: Container, allowedSlots: Set<Int>?): Double {
        if (allowedSlots == null) {
            return remainingCapacityKg(container)
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
            total += stackWeightKg(stack)
        }
        val capacity = allowedSlots.size.toDouble() * KG_PER_SLOT
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
