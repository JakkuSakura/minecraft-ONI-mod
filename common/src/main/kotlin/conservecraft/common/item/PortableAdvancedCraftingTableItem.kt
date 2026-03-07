package conservecraft.common.item

import net.minecraft.network.chat.Component
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.CraftingMenu
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

class PortableAdvancedCraftingTableItem(properties: Item.Properties) : OniDescribedItem(properties) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        }

        player.openMenu(createMenuProvider())
        player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE)
        return InteractionResult.CONSUME
    }

    private fun createMenuProvider(): MenuProvider {
        return SimpleMenuProvider(
            { id, inventory, _ -> CraftingMenu(id, inventory, ContainerLevelAccess.NULL) },
            Component.translatable("screen.conservecraft.portable_advanced_crafting_table.title")
        )
    }
}
