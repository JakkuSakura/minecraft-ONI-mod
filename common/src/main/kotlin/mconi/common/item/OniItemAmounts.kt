package mconi.common.item

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import kotlin.math.floor

object OniItemAmounts {
    const val SCALE: Int = 1000

    fun amountUnits(stack: ItemStack): Double {
        return amountUnits(stack.item, stack.count)
    }

    fun amountUnits(item: Item, count: Int): Double {
        return if (OniItemFactory.specByItem(item) != null) {
            count.toDouble() / SCALE.toDouble()
        } else {
            count.toDouble()
        }
    }

    fun countFromUnits(item: Item, units: Double): Int {
        if (units <= 0.0) {
            return 0
        }
        return if (OniItemFactory.specByItem(item) != null) {
            floor(units * SCALE.toDouble() + 1e-9).toInt()
        } else {
            floor(units + 1e-9).toInt()
        }
    }

    fun takeFromStack(stack: ItemStack, units: Double): Double {
        if (units <= 0.0 || stack.isEmpty) {
            return 0.0
        }
        val available = amountUnits(stack)
        val toTake = minOf(available, units)
        val remaining = available - toTake
        val newCount = countFromUnits(stack.item, remaining)
        stack.count = newCount
        return toTake
    }
}
