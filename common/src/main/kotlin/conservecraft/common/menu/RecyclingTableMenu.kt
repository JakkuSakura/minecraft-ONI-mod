package conservecraft.common.menu

import conservecraft.common.block.RecyclingTableLogic
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

class RecyclingTableMenu(
    containerId: Int,
    playerInventory: Inventory,
    private val access: ContainerLevelAccess = ContainerLevelAccess.NULL,
) : AbstractContainerMenu(OniMenuTypes.recyclingTable(), containerId) {
    private val inputContainer = object : SimpleContainer(1) {
        override fun setChanged() {
            super.setChanged()
            this@RecyclingTableMenu.slotsChanged(this)
        }
    }
    private val outputContainer = SimpleContainer(9)

    init {
        addSlot(object : Slot(inputContainer, 0, INPUT_X, INPUT_Y) {
            override fun getMaxStackSize(): Int = 1

            override fun setChanged() {
                super.setChanged()
                this@RecyclingTableMenu.slotsChanged(container)
            }
        })

        for (row in 0 until 3) {
            for (column in 0 until 3) {
                val slotIndex = row * 3 + column
                addSlot(object : Slot(outputContainer, slotIndex, OUTPUT_START_X + column * 18, OUTPUT_START_Y + row * 18) {
                    override fun mayPlace(stack: ItemStack): Boolean = false
                })
            }
        }

        for (row in 0 until 3) {
            for (column in 0 until 9) {
                addSlot(Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18))
            }
        }
        for (column in 0 until 9) {
            addSlot(Slot(playerInventory, column, 8 + column * 18, 142))
        }
    }

    override fun stillValid(player: Player): Boolean = true

    override fun slotsChanged(container: net.minecraft.world.Container) {
        super.slotsChanged(container)
        if (container !== inputContainer) {
            return
        }
        rebuildOutputs()
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        val slot = slots.getOrNull(index) ?: return ItemStack.EMPTY
        if (!slot.hasItem()) {
            return ItemStack.EMPTY
        }
        val stack = slot.item
        val original = stack.copy()

        when {
            index in OUTPUT_SLOT_RANGE -> {
                if (!moveItemStackTo(stack, PLAYER_INV_START, PLAYER_INV_END, true)) {
                    return ItemStack.EMPTY
                }
            }
            index == INPUT_SLOT -> {
                if (!moveItemStackTo(stack, PLAYER_INV_START, PLAYER_INV_END, false)) {
                    return ItemStack.EMPTY
                }
            }
            index in PLAYER_SLOT_RANGE -> {
                if (slots[INPUT_SLOT].hasItem()) {
                    return ItemStack.EMPTY
                }
                if (!moveItemStackTo(stack, INPUT_SLOT, INPUT_SLOT + 1, false)) {
                    return ItemStack.EMPTY
                }
            }
            else -> return ItemStack.EMPTY
        }

        if (stack.isEmpty) {
            slot.set(ItemStack.EMPTY)
        } else {
            slot.setChanged()
        }
        return original
    }

    override fun removed(player: Player) {
        super.removed(player)
        access.execute { _, _ ->
            clearContainer(player, inputContainer)
            clearContainer(player, outputContainer)
        }
    }

    private fun rebuildOutputs() {
        clearOutputs()
        val input = inputContainer.getItem(0)
        if (input.isEmpty) {
            return
        }
        val outputs = RecyclingTableLogic.outputStacksFor(input)
        if (outputs.isEmpty()) {
            return
        }
        for ((index, stack) in outputs.take(outputContainer.containerSize).withIndex()) {
            outputContainer.setItem(index, stack)
        }
        inputContainer.removeItemNoUpdate(0)
    }

    private fun clearOutputs() {
        for (index in 0 until outputContainer.containerSize) {
            outputContainer.setItem(index, ItemStack.EMPTY)
        }
    }

    companion object {
        private const val INPUT_SLOT = 0
        private const val INPUT_X = 124
        private const val INPUT_Y = 35
        private const val OUTPUT_START_X = 30
        private const val OUTPUT_START_Y = 17
        private const val PLAYER_INV_START = 10
        private const val PLAYER_INV_END = 46
        private val OUTPUT_SLOT_RANGE = 1..9
        private val PLAYER_SLOT_RANGE = 10..45
    }
}
