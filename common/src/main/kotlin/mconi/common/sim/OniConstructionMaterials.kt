package mconi.common.sim

import net.minecraft.tags.TagKey
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item

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
}
