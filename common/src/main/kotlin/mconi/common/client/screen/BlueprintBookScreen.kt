package mconi.common.client.screen

import mconi.common.item.BlueprintItem
import mconi.common.item.OniItemFactory
import mconi.common.menu.BlueprintBookMenu
import mconi.common.item.OniBlueprint
import mconi.common.item.OniBlueprintRegistry
import mconi.common.item.OniBlueprintSelection
import mconi.common.item.OniBlueprintSelectionNbt
import mconi.common.item.OniMaterialChoice
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack

class BlueprintBookScreen(
    menu: BlueprintBookMenu,
    inventory: Inventory,
    title: Component
) : AbstractContainerScreen<BlueprintBookMenu>(menu, inventory, title) {
    private var selection: OniBlueprintSelection? = null

    init {
        imageWidth = 236
        imageHeight = 180
        titleLabelX = 8
        titleLabelY = 6
    }

    override fun init() {
        super.init()
        refreshSelection()
        rebuildButtons()
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick)
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        renderTooltip(guiGraphics, mouseX, mouseY)
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        val x = leftPos + 8
        var y = topPos + 24
        val font = font

        val currentSelection = selection
        if (currentSelection == null) {
            guiGraphics.drawString(font, "No blueprint selected", x, y, 0xE0E0E0, false)
            return
        }

        val blueprint = OniBlueprintRegistry.get(currentSelection.blueprintId)
        val blueprintName = if (blueprint == null) {
            currentSelection.blueprintId
        } else {
            resolveBlueprintName(blueprint, currentSelection.blueprintId)
        }
        guiGraphics.drawString(font, "Blueprint: $blueprintName", x, y, 0xE0E0E0, false)
        y += 14

        if (blueprint != null) {
            for ((index, slot) in blueprint.materialSlots.withIndex()) {
                val choice = currentSelection.materials.firstOrNull { it.slotId == slot.slotId }
                val itemName = resolveItemName(choice?.itemId)
                guiGraphics.drawString(
                    font,
                    "Material ${index + 1}: $itemName (${slot.amount})",
                    x,
                    y,
                    0xC0C0C0,
                    false
                )
                y += 12
            }
        }
    }

    private fun rebuildButtons() {
        clearWidgets()
        val x = leftPos + 140
        var y = topPos + 20

        addRenderableWidget(
            Button.builder(Component.literal("<")) {
                shiftBlueprint(-1)
                sendButton(BlueprintBookMenu.ACTION_PREV_BLUEPRINT)
            }.bounds(x, y, 18, 18).build()
        )
        addRenderableWidget(
            Button.builder(Component.literal(">")) {
                shiftBlueprint(1)
                sendButton(BlueprintBookMenu.ACTION_NEXT_BLUEPRINT)
            }.bounds(x + 20, y, 18, 18).build()
        )
        y += 28

        val current = selection ?: return
        val blueprint = OniBlueprintRegistry.get(current.blueprintId) ?: return
        for (slotIndex in blueprint.materialSlots.indices) {
            addRenderableWidget(
                Button.builder(Component.literal("<")) {
                    shiftMaterial(slotIndex, -1)
                    sendButton(BlueprintBookMenu.encodeMaterialAction(slotIndex, -1))
                }.bounds(x, y, 18, 18).build()
            )
            addRenderableWidget(
                Button.builder(Component.literal(">")) {
                    shiftMaterial(slotIndex, 1)
                    sendButton(BlueprintBookMenu.encodeMaterialAction(slotIndex, 1))
                }.bounds(x + 20, y, 18, 18).build()
            )
            y += 22
        }
    }

    private fun refreshSelection() {
        val stack = currentBookStack()
        selection = stack?.let { OniBlueprintSelectionNbt.readFrom(it) }
        if (selection == null) {
            val firstId = OniBlueprintRegistry.allIds().firstOrNull() ?: return
            val blueprint = OniBlueprintRegistry.get(firstId) ?: return
            selection = OniBlueprintSelectionNbt.defaultSelection(blueprint)
        }
    }

    private fun shiftBlueprint(direction: Int) {
        val blueprintIds = OniBlueprintRegistry.allIds().toList()
        if (blueprintIds.isEmpty()) {
            return
        }
        val currentId = selection?.blueprintId ?: blueprintIds.first()
        val currentIndex = blueprintIds.indexOf(currentId).takeIf { it >= 0 } ?: 0
        val nextIndex = (currentIndex + direction).floorMod(blueprintIds.size)
        val nextId = blueprintIds[nextIndex]
        val blueprint = OniBlueprintRegistry.get(nextId) ?: return
        selection = OniBlueprintSelectionNbt.defaultSelection(blueprint)
        rebuildButtons()
    }

    private fun shiftMaterial(slotIndex: Int, direction: Int) {
        val currentSelection = selection ?: return
        val blueprint = OniBlueprintRegistry.get(currentSelection.blueprintId) ?: return
        if (slotIndex !in blueprint.materialSlots.indices) {
            return
        }
        val slot = blueprint.materialSlots[slotIndex]
        if (slot.allowedItems.isEmpty()) {
            return
        }
        val currentChoice = currentSelection.materials.firstOrNull { it.slotId == slot.slotId }
        val currentIndex = slot.allowedItems.indexOf(currentChoice?.itemId).takeIf { it >= 0 } ?: 0
        val nextIndex = (currentIndex + direction).floorMod(slot.allowedItems.size)
        val nextItemId = slot.allowedItems[nextIndex]

        val updated = currentSelection.materials.toMutableList()
        val idx = updated.indexOfFirst { it.slotId == slot.slotId }
        val newChoice = OniMaterialChoice(slot.slotId, nextItemId)
        if (idx >= 0) {
            updated[idx] = newChoice
        } else {
            updated.add(newChoice)
        }
        selection = OniBlueprintSelection(currentSelection.blueprintId, updated)
        rebuildButtons()
    }

    private fun currentBookStack(): ItemStack? {
        val player = minecraft?.player ?: return null
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

    private fun resolveBlueprintName(blueprint: OniBlueprint, fallbackId: String): String {
        val key = "blueprint.mconi.${blueprint.id}"
        val translated = Component.translatable(key).string
        return if (translated == key) fallbackId else translated
    }

    private fun resolveItemName(itemId: String?): String {
        if (itemId.isNullOrBlank()) {
            return "Unselected"
        }
        val item = OniItemFactory.itemById(itemId) ?: return itemId
        return ItemStack(item).hoverName.string
    }

    private fun sendButton(id: Int) {
        minecraft?.gameMode?.handleInventoryButtonClick(menu.containerId, id)
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
}
