package mconi.common.menu

import mconi.common.item.BlueprintItem
import mconi.common.item.OniBlueprintRegistry
import mconi.common.item.OniBlueprintSelection
import mconi.common.item.OniBlueprintSelectionNbt
import mconi.common.item.OniMaterialChoice
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack

class BlueprintBookMenu(
    containerId: Int,
    private val inventory: Inventory,
    menuType: MenuType<BlueprintBookMenu>
) : AbstractContainerMenu(menuType, containerId) {
    private val blueprintIds: List<String> = OniBlueprintRegistry.allIds().toList()

    constructor(containerId: Int, inventory: Inventory) : this(containerId, inventory, OniMenuTypes.BLUEPRINT_BOOK)

    fun player(): Player = inventory.player

    override fun stillValid(player: Player): Boolean = true

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        return ItemStack.EMPTY
    }

    override fun clickMenuButton(player: Player, id: Int): Boolean {
        val stack = findBlueprintStack(player) ?: return false
        val current = resolveSelection(stack) ?: return false
        val blueprint = OniBlueprintRegistry.get(current.blueprintId) ?: return false

        return when {
            id == ACTION_PREV_BLUEPRINT -> {
                val next = shiftBlueprint(current, -1)
                OniBlueprintSelectionNbt.writeTo(stack, next)
                true
            }
            id == ACTION_NEXT_BLUEPRINT -> {
                val next = shiftBlueprint(current, 1)
                OniBlueprintSelectionNbt.writeTo(stack, next)
                true
            }
            id in ACTION_MATERIAL_RANGE -> {
                val (slotIndex, direction) = decodeMaterialAction(id)
                val updated = shiftMaterial(blueprint, current, slotIndex, direction)
                OniBlueprintSelectionNbt.writeTo(stack, updated)
                true
            }
            else -> false
        }
    }

    private fun shiftBlueprint(selection: OniBlueprintSelection, direction: Int): OniBlueprintSelection {
        if (blueprintIds.isEmpty()) {
            return selection
        }
        val currentIndex = blueprintIds.indexOf(selection.blueprintId).takeIf { it >= 0 } ?: 0
        val nextIndex = (currentIndex + direction).floorMod(blueprintIds.size)
        val nextId = blueprintIds[nextIndex]
        val blueprint = OniBlueprintRegistry.get(nextId) ?: return selection
        return OniBlueprintSelectionNbt.starterSelection(blueprint)
    }

    private fun shiftMaterial(
        blueprint: mconi.common.item.OniBlueprint,
        selection: OniBlueprintSelection,
        slotIndex: Int,
        direction: Int
    ): OniBlueprintSelection {
        if (slotIndex < 0 || slotIndex >= blueprint.materialSlots.size) {
            return selection
        }
        val slot = blueprint.materialSlots[slotIndex]
        if (slot.allowedItems.isEmpty()) {
            return selection
        }
        val current = selection.materials.firstOrNull { it.slotId == slot.slotId }
        val currentIndex = slot.allowedItems.indexOf(current?.itemId).takeIf { it >= 0 } ?: 0
        val nextIndex = (currentIndex + direction).floorMod(slot.allowedItems.size)
        val nextItemId = slot.allowedItems[nextIndex]

        val updated = selection.materials.toMutableList()
        val idx = updated.indexOfFirst { it.slotId == slot.slotId }
        val newChoice = OniMaterialChoice(slot.slotId, nextItemId)
        if (idx >= 0) {
            updated[idx] = newChoice
        } else {
            updated.add(newChoice)
        }
        return OniBlueprintSelection(selection.blueprintId, updated)
    }

    private fun resolveSelection(stack: ItemStack): OniBlueprintSelection? {
        val existing = OniBlueprintSelectionNbt.readFrom(stack)
        if (existing != null) {
            return existing
        }
        val firstId = blueprintIds.firstOrNull() ?: return null
        val blueprint = OniBlueprintRegistry.get(firstId) ?: return null
        val selection = OniBlueprintSelectionNbt.starterSelection(blueprint)
        OniBlueprintSelectionNbt.writeTo(stack, selection)
        return selection
    }

    private fun findBlueprintStack(player: Player): ItemStack? {
        val main = player.mainHandItem
        if (main.item is BlueprintItem) {
            return main
        }
        val off = player.offhandItem
        if (off.item is BlueprintItem) {
            return off
        }
        val inv = player.inventory
        for (i in 0 until inv.containerSize) {
            val stack = inv.getItem(i)
            if (stack.item is BlueprintItem) {
                return stack
            }
        }
        return null
    }

    private fun Int.floorMod(mod: Int): Int {
        if (mod == 0) {
            return 0
        }
        var result = this % mod
        if (result < 0) {
            result += mod
        }
        return result
    }

    companion object {
        const val ACTION_PREV_BLUEPRINT = 0
        const val ACTION_NEXT_BLUEPRINT = 1
        const val ACTION_MATERIAL_BASE = 1000
        val ACTION_MATERIAL_RANGE = 1000..1999

        fun encodeMaterialAction(slotIndex: Int, direction: Int): Int {
            val dir = if (direction >= 0) 1 else 0
            return ACTION_MATERIAL_BASE + slotIndex * 2 + dir
        }

        fun decodeMaterialAction(id: Int): Pair<Int, Int> {
            val offset = id - ACTION_MATERIAL_BASE
            val slotIndex = offset / 2
            val direction = if (offset % 2 == 0) -1 else 1
            return slotIndex to direction
        }
    }
}
