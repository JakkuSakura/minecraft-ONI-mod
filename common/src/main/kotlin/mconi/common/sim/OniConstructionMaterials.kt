package mconi.common.sim

import net.minecraft.tags.TagKey
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

object OniConstructionMaterials {
    fun depositFromPlayer(player: Player, tag: TagKey<Item>, needed: Int): Int {
        if (needed <= 0) {
            return 0
        }

        val inventory = player.inventory
        var remaining = needed
        var deposited = 0

        val size = inventory.containerSize
        for (i in 0 until size) {
            val stack = inventory.getItem(i)
            if (stack.isEmpty) {
                continue
            }
            if (!stack.`is`(tag)) {
                continue
            }
            val take = minOf(remaining, stack.count)
            if (take <= 0) {
                continue
            }
            stack.shrink(take)
            deposited += take
            remaining -= take
            if (remaining <= 0) {
                break
            }
        }

        return deposited
    }

    fun depositFromPlayer(player: Player, item: Item, needed: Int): Int {
        if (needed <= 0) {
            return 0
        }

        val inventory = player.inventory
        var remaining = needed
        var deposited = 0

        val size = inventory.containerSize
        for (i in 0 until size) {
            val stack = inventory.getItem(i)
            if (stack.isEmpty) {
                continue
            }
            if (stack.item != item) {
                continue
            }
            val take = minOf(remaining, stack.count)
            if (take <= 0) {
                continue
            }
            stack.shrink(take)
            deposited += take
            remaining -= take
            if (remaining <= 0) {
                break
            }
        }

        return deposited
    }

    fun depositFromPlayer(player: Player, items: Set<Item>, needed: Int): Int {
        if (needed <= 0 || items.isEmpty()) {
            return 0
        }

        val inventory = player.inventory
        var remaining = needed
        var deposited = 0

        val size = inventory.containerSize
        for (i in 0 until size) {
            val stack = inventory.getItem(i)
            if (stack.isEmpty) {
                continue
            }
            if (!items.contains(stack.item)) {
                continue
            }
            val take = minOf(remaining, stack.count)
            if (take <= 0) {
                continue
            }
            stack.shrink(take)
            deposited += take
            remaining -= take
            if (remaining <= 0) {
                break
            }
        }

        return deposited
    }

    fun refundStacks(item: Item, amount: Int): List<ItemStack> {
        if (amount <= 0) {
            return emptyList()
        }

        val stacks: MutableList<ItemStack> = ArrayList()
        var remaining = amount
        val maxStack = ItemStack(item).maxStackSize.coerceAtLeast(1)
        while (remaining > 0) {
            val count = minOf(remaining, maxStack)
            stacks.add(ItemStack(item, count))
            remaining -= count
        }
        return stacks
    }
}
