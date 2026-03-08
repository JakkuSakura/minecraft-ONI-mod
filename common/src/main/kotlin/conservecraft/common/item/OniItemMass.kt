package conservecraft.common.item

import conservecraft.common.thermal.OniThermalMath
import net.minecraft.core.component.DataComponents
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import kotlin.math.floor

object OniItemMass {
    const val TAG_MASS: String = "Mass"

    fun stackMass(stack: ItemStack): Double {
        if (stack.isEmpty) {
            return 0.0
        }
        val data = stack.get(DataComponents.CUSTOM_DATA)
        if (data != null) {
            val tag = data.copyTag()
            if (tag.contains(TAG_MASS)) {
                return tag.getDouble(TAG_MASS).orElse(0.0)
            }
        }
        val solidUnitMass = OniSolidItems.unitMassKgOf(stack.item)
        if (solidUnitMass != null) {
            return solidUnitMass * stack.count.toDouble()
        }
        val specMass = OniItemFactory.specByItem(stack.item)?.properties?.mass
        if (specMass != null) {
            return specMass * stack.count.toDouble()
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
        if (hasMassTag(stack)) {
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

        if (OniSolidItems.unitMassKgOf(stack.item) != null) {
            return OniSolidItems.takeMass(stack, mass)
        }

        val unitMass = OniItemFactory.specByItem(stack.item)?.properties?.mass ?: 1.0
        val units = floor((mass + 1e-9) / unitMass).toInt().coerceAtLeast(0)
        if (units <= 0) {
            return 0.0
        }
        val takenUnits = minOf(stack.count, units)
        if (takenUnits <= 0) {
            return 0.0
        }
        stack.count = (stack.count - takenUnits).coerceAtLeast(0)
        return takenUnits.toDouble() * unitMass
    }

    fun mergeIntoContainer(container: Container, stack: ItemStack): ItemStack {
        if (stack.isEmpty) {
            return stack
        }
        if (!hasMassTag(stack) && OniSolidItems.unitMassKgOf(stack.item) != null) {
            return mergeCountStack(container, stack)
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
        OniItemThermal.setTemperatureK(addStack, OniItemThermal.temperatureK(stack))
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
            val solidUnitMass = OniSolidItems.unitMassKgOf(item)
            if (solidUnitMass != null) {
                val units = floor((mass + 1e-9) / solidUnitMass).toInt()
                if (units > 0) {
                    merged.add(ItemStack(item, units))
                }
                continue
            }
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
        val extraSample = OniThermalMath.sampleOf(stack) ?: return false
        val size = container.containerSize
        for (i in 0 until size) {
            val current = container.getItem(i)
            if (current.isEmpty || current.item != stack.item || !hasMassTag(current)) {
                continue
            }
            val next = stackMass(current) + mass
            setStackMass(current, next)
            OniItemThermal.equalizeContainerTemperature(container, listOf(extraSample))
            return true
        }
        for (i in 0 until size) {
            if (!container.getItem(i).isEmpty) {
                continue
            }
            container.setItem(i, stack)
            OniItemThermal.equalizeContainerTemperature(container)
            return true
        }
        return false
    }

    private fun mergeCountStack(container: Container, stack: ItemStack): ItemStack {
        val unitMass = OniSolidItems.unitMassKgOf(stack.item) ?: return stack
        if (unitMass <= 0.0) {
            return stack
        }
        val capacityMass = OniInventoryMass.remainingCapacity(container)
        val maxByCapacity = floor((capacityMass + 1e-9) / unitMass).toInt().coerceAtLeast(0)
        if (maxByCapacity <= 0) {
            return stack
        }
        var remainingUnits = minOf(stack.count, maxByCapacity)
        if (remainingUnits <= 0) {
            return stack
        }
        val movedPreview = ItemStack(stack.item, remainingUnits)
        OniItemThermal.setTemperatureK(movedPreview, OniItemThermal.temperatureK(stack))
        val extraSample = OniThermalMath.sampleOf(movedPreview) ?: return stack
        for (index in 0 until container.containerSize) {
            val current = container.getItem(index)
            if (current.isEmpty || current.item != stack.item) {
                continue
            }
            val free = current.maxStackSize - current.count
            if (free <= 0) {
                continue
            }
            val moved = minOf(free, remainingUnits)
            if (moved <= 0) {
                continue
            }
            current.grow(moved)
            stack.shrink(moved)
            remainingUnits -= moved
            if (remainingUnits <= 0 || stack.isEmpty) {
                OniItemThermal.equalizeContainerTemperature(container, listOf(extraSample))
                return stack
            }
        }
        for (index in 0 until container.containerSize) {
            if (!container.getItem(index).isEmpty) {
                continue
            }
            val moved = remainingUnits
            val inserted = ItemStack(stack.item, moved)
            OniItemThermal.setTemperatureK(inserted, OniItemThermal.temperatureK(stack))
            container.setItem(index, inserted)
            stack.shrink(moved)
            OniItemThermal.equalizeContainerTemperature(container)
            return stack
        }
        return stack
    }

    private fun hasMassTag(stack: ItemStack): Boolean {
        val data = stack.get(DataComponents.CUSTOM_DATA) ?: return false
        return data.copyTag().contains(TAG_MASS)
    }
}
