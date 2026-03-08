package conservecraft.common.client.screen

import conservecraft.common.menu.AdvancedCraftingTableMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Inventory

class AdvancedCraftingTableScreen(
    menu: AdvancedCraftingTableMenu,
    inventory: Inventory,
    title: Component,
) : AbstractContainerScreen<AdvancedCraftingTableMenu>(menu, inventory, title) {
    init {
        imageWidth = 176
        imageHeight = 166
        inventoryLabelY = imageHeight - 94
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick)
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        renderTooltip(guiGraphics, mouseX, mouseY)
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth.toFloat(), imageHeight.toFloat(), 256f, 256f)
    }

    companion object {
        private val TEXTURE: Identifier = Identifier.parse("minecraft:textures/gui/container/crafting_table.png")
    }
}
