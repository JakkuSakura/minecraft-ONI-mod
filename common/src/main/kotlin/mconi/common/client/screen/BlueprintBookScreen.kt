package mconi.common.client.screen

import mconi.common.item.BlueprintItem
import mconi.common.item.OniBlueprint
import mconi.common.item.OniBlueprintRegistry
import mconi.common.item.OniBlueprintSelection
import mconi.common.item.OniBlueprintSelectionNbt
import mconi.common.item.OniBlueprintTargets
import mconi.common.item.OniItemFactory
import mconi.common.item.OniMaterialChoice
import mconi.common.menu.BlueprintBookMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import kotlin.math.ceil
import kotlin.math.max

class BlueprintBookScreen(
    menu: BlueprintBookMenu,
    inventory: Inventory,
    title: Component
) : AbstractContainerScreen<BlueprintBookMenu>(menu, inventory, title) {
    private var selection: OniBlueprintSelection? = null
    private var blueprintScrollRow = 0

    private val blueprintColumns = 6
    private val blueprintRows = 5
    private val cellSize = 18
    private val cellPadding = 1

    init {
        imageWidth = 236
        imageHeight = 180
        titleLabelX = 8
        titleLabelY = 6
    }

    override fun init() {
        super.init()
        refreshSelectionFromStack()
        ensureSelection()
    }

    override fun containerTick() {
        super.containerTick()
        refreshSelectionFromStack()
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick)
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        renderTooltip(guiGraphics, mouseX, mouseY)
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        val gridX = leftPos + 8
        val gridY = topPos + 24
        val gridWidth = blueprintColumns * cellSize
        val gridHeight = blueprintRows * cellSize
        val detailsX = gridX + gridWidth + 12
        val detailsY = topPos + 22

        val currentSelection = selection
        if (currentSelection == null) {
            guiGraphics.drawString(font, "No blueprint selected", gridX, gridY, 0xE0E0E0, false)
            return
        }

        val blueprintIds = OniBlueprintRegistry.allIds().toList()
        val totalRows = ceil(blueprintIds.size / blueprintColumns.toDouble()).toInt()
        val maxScroll = max(0, totalRows - blueprintRows)
        blueprintScrollRow = blueprintScrollRow.coerceIn(0, maxScroll)

        drawBlueprintGrid(guiGraphics, gridX, gridY, gridWidth, gridHeight, blueprintIds, currentSelection)
        drawBlueprintScrollbar(guiGraphics, gridX + gridWidth + 2, gridY, gridHeight, blueprintScrollRow, maxScroll)

        val blueprint = OniBlueprintRegistry.get(currentSelection.blueprintId)
        val blueprintName = if (blueprint == null) {
            currentSelection.blueprintId
        } else {
            resolveBlueprintName(blueprint, currentSelection.blueprintId)
        }

        val iconStack = blueprintIconStack(currentSelection.blueprintId)
        guiGraphics.renderItem(iconStack, detailsX, detailsY)
        guiGraphics.renderItemDecorations(font, iconStack, detailsX, detailsY)
        guiGraphics.drawString(font, blueprintName, detailsX + 22, detailsY + 4, 0xE0E0E0, false)

        var y = detailsY + 24
        if (blueprint == null) {
            guiGraphics.drawString(font, "Missing blueprint data", detailsX, y, 0xC0C0C0, false)
            return
        }

        y = drawMaterialPicker(guiGraphics, detailsX, y, blueprint, currentSelection)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, deltaX: Double, deltaY: Double): Boolean {
        val gridX = leftPos + 8
        val gridY = topPos + 24
        val gridWidth = blueprintColumns * cellSize
        val gridHeight = blueprintRows * cellSize
        if (mouseX >= gridX && mouseX < gridX + gridWidth && mouseY >= gridY && mouseY < gridY + gridHeight) {
            val blueprintCount = OniBlueprintRegistry.allIds().count()
            val totalRows = ceil(blueprintCount / blueprintColumns.toDouble()).toInt()
            val maxScroll = max(0, totalRows - blueprintRows)
            if (maxScroll > 0) {
                blueprintScrollRow = (blueprintScrollRow - deltaY.toInt()).coerceIn(0, maxScroll)
                return true
            }
        }
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button)
        }

        val gridX = leftPos + 8
        val gridY = topPos + 24
        val clickedBlueprint = findBlueprintAt(mouseX.toInt(), mouseY.toInt(), gridX, gridY)
        if (clickedBlueprint != null) {
            selectBlueprint(clickedBlueprint)
            return true
        }

        val currentSelection = selection
        if (currentSelection != null) {
            val blueprint = OniBlueprintRegistry.get(currentSelection.blueprintId)
            if (blueprint != null) {
                val detailsX = gridX + blueprintColumns * cellSize + 12
                val detailsY = topPos + 22 + 24
                val materialHit = findMaterialAt(mouseX.toInt(), mouseY.toInt(), detailsX, detailsY, blueprint)
                if (materialHit != null) {
                    selectMaterial(materialHit.slotIndex, materialHit.itemIndex, blueprint)
                    return true
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    private fun drawBlueprintGrid(
        guiGraphics: GuiGraphics,
        gridX: Int,
        gridY: Int,
        gridWidth: Int,
        gridHeight: Int,
        blueprintIds: List<String>,
        currentSelection: OniBlueprintSelection
    ) {
        val startIndex = blueprintScrollRow * blueprintColumns
        val visibleCount = blueprintColumns * blueprintRows
        val endIndex = (startIndex + visibleCount).coerceAtMost(blueprintIds.size)

        for (index in startIndex until endIndex) {
            val localIndex = index - startIndex
            val col = localIndex % blueprintColumns
            val row = localIndex / blueprintColumns
            val x = gridX + col * cellSize
            val y = gridY + row * cellSize
            val blueprintId = blueprintIds[index]
            val isSelected = blueprintId == currentSelection.blueprintId

            drawCellBackground(guiGraphics, x, y, isSelected)
            val icon = blueprintIconStack(blueprintId)
            guiGraphics.renderItem(icon, x + cellPadding, y + cellPadding)
            guiGraphics.renderItemDecorations(font, icon, x + cellPadding, y + cellPadding)
        }

        if (blueprintIds.isEmpty()) {
            guiGraphics.drawString(font, "No blueprints", gridX, gridY, 0xE0E0E0, false)
        }
    }

    private fun drawBlueprintScrollbar(
        guiGraphics: GuiGraphics,
        x: Int,
        y: Int,
        height: Int,
        scrollRow: Int,
        maxScroll: Int
    ) {
        if (maxScroll <= 0) {
            return
        }
        guiGraphics.fill(x, y, x + 3, y + height, 0xFF2B2B2B.toInt())
        val thumbHeight = max(10, height / (maxScroll + blueprintRows))
        val maxThumbY = height - thumbHeight
        val thumbY = if (maxScroll == 0) 0 else (scrollRow.toFloat() / maxScroll * maxThumbY).toInt()
        guiGraphics.fill(x, y + thumbY, x + 3, y + thumbY + thumbHeight, 0xFFB0B0B0.toInt())
    }

    private fun drawMaterialPicker(
        guiGraphics: GuiGraphics,
        startX: Int,
        startY: Int,
        blueprint: OniBlueprint,
        currentSelection: OniBlueprintSelection
    ): Int {
        var y = startY
        val columns = 4

        for ((slotIndex, slot) in blueprint.materialSlots.withIndex()) {
            val title = "Material ${slotIndex + 1}: ${slot.amount} kg"
            guiGraphics.drawString(font, title, startX, y, 0xC0C0C0, false)
            y += 12

            val allowed = slot.allowedItems
            for ((itemIndex, itemId) in allowed.withIndex()) {
                val col = itemIndex % columns
                val row = itemIndex / columns
                val x = startX + col * cellSize
                val itemY = y + row * cellSize
                val isSelected = currentSelection.materials.any { it.slotId == slot.slotId && it.itemId == itemId }

                drawCellBackground(guiGraphics, x, itemY, isSelected)
                val item = OniItemFactory.itemById(itemId)
                val stack = if (item == null) ItemStack.EMPTY else ItemStack(item)
                guiGraphics.renderItem(stack, x + cellPadding, itemY + cellPadding)
                guiGraphics.renderItemDecorations(font, stack, x + cellPadding, itemY + cellPadding)
            }

            val rows = ceil(allowed.size / columns.toDouble()).toInt().coerceAtLeast(1)
            y += rows * cellSize + 6
        }

        return y
    }

    private fun drawCellBackground(guiGraphics: GuiGraphics, x: Int, y: Int, selected: Boolean) {
        val borderColor = if (selected) 0xFFB0B0B0.toInt() else 0xFF3A3A3A.toInt()
        val fillColor = if (selected) 0xFF2E2E2E.toInt() else 0xFF1E1E1E.toInt()
        guiGraphics.fill(x, y, x + cellSize, y + cellSize, borderColor)
        guiGraphics.fill(x + 1, y + 1, x + cellSize - 1, y + cellSize - 1, fillColor)
    }

    private fun findBlueprintAt(mouseX: Int, mouseY: Int, gridX: Int, gridY: Int): String? {
        val gridWidth = blueprintColumns * cellSize
        val gridHeight = blueprintRows * cellSize
        if (mouseX < gridX || mouseY < gridY || mouseX >= gridX + gridWidth || mouseY >= gridY + gridHeight) {
            return null
        }
        val col = (mouseX - gridX) / cellSize
        val row = (mouseY - gridY) / cellSize
        val index = blueprintScrollRow * blueprintColumns + row * blueprintColumns + col
        val blueprintIds = OniBlueprintRegistry.allIds().toList()
        if (index < 0 || index >= blueprintIds.size) {
            return null
        }
        return blueprintIds[index]
    }

    private data class MaterialHit(val slotIndex: Int, val itemIndex: Int)

    private fun findMaterialAt(mouseX: Int, mouseY: Int, startX: Int, startY: Int, blueprint: OniBlueprint): MaterialHit? {
        var y = startY
        val columns = 4

        for ((slotIndex, slot) in blueprint.materialSlots.withIndex()) {
            y += 12
            val allowed = slot.allowedItems
            for (itemIndex in allowed.indices) {
                val col = itemIndex % columns
                val row = itemIndex / columns
                val x = startX + col * cellSize
                val itemY = y + row * cellSize
                if (mouseX in x until (x + cellSize) && mouseY in itemY until (itemY + cellSize)) {
                    return MaterialHit(slotIndex, itemIndex)
                }
            }
            val rows = ceil(allowed.size / columns.toDouble()).toInt().coerceAtLeast(1)
            y += rows * cellSize + 6
        }

        return null
    }

    private fun selectBlueprint(blueprintId: String) {
        val blueprint = OniBlueprintRegistry.get(blueprintId) ?: return
        selection = OniBlueprintSelectionNbt.starterSelection(blueprint)
        val index = OniBlueprintRegistry.allIds().indexOf(blueprintId)
        if (index >= 0) {
            sendButton(BlueprintBookMenu.encodeBlueprintSelection(index))
        }
    }

    private fun selectMaterial(slotIndex: Int, itemIndex: Int, blueprint: OniBlueprint) {
        if (slotIndex !in blueprint.materialSlots.indices) {
            return
        }
        val slot = blueprint.materialSlots[slotIndex]
        if (itemIndex !in slot.allowedItems.indices) {
            return
        }
        val itemId = slot.allowedItems[itemIndex]
        val current = selection ?: return
        val updated = current.materials.toMutableList()
        val idx = updated.indexOfFirst { it.slotId == slot.slotId }
        val newChoice = OniMaterialChoice(slot.slotId, itemId)
        if (idx >= 0) {
            updated[idx] = newChoice
        } else {
            updated.add(newChoice)
        }
        selection = OniBlueprintSelection(current.blueprintId, updated)
        sendButton(BlueprintBookMenu.encodeMaterialSelection(slotIndex, itemIndex))
    }

    private fun ensureSelection() {
        if (selection != null) {
            return
        }
        val firstId = OniBlueprintRegistry.allIds().firstOrNull() ?: return
        val blueprint = OniBlueprintRegistry.get(firstId) ?: return
        selection = OniBlueprintSelectionNbt.starterSelection(blueprint)
    }

    private fun refreshSelectionFromStack() {
        val stack = currentBookStack() ?: return
        val fromStack = OniBlueprintSelectionNbt.readFrom(stack) ?: return
        if (selection != fromStack) {
            selection = fromStack
        }
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

    private fun blueprintIconStack(blueprintId: String): ItemStack {
        val blockId = OniBlueprintTargets.blockIdFor(blueprintId)
        val item = blockId?.let { OniItemFactory.itemByBlockId(it) }
            ?: OniItemFactory.itemById(OniItemFactory.BLUEPRINT)
        return if (item == null) ItemStack.EMPTY else ItemStack(item)
    }

    private fun sendButton(id: Int) {
        minecraft?.gameMode?.handleInventoryButtonClick(menu.containerId, id)
    }
}
