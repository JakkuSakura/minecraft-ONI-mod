package conservecraft.common.item

import conservecraft.common.menu.AdvancedCraftingTableMenu
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

class PortableAdvancedCraftingTableItem(properties: Item.Properties) : OniDescribedItem(properties) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        if (!level.isClientSide) {
            player.openMenu(createMenuProvider())
        }
        return InteractionResult.SUCCESS
    }

    private fun createMenuProvider(): SimpleMenuProvider {
        return SimpleMenuProvider(
            { id, inventory, _ -> AdvancedCraftingTableMenu(id, inventory, ContainerLevelAccess.NULL) },
            Component.translatable("screen.conservecraft.portable_advanced_crafting_table.title")
        )
    }
}
