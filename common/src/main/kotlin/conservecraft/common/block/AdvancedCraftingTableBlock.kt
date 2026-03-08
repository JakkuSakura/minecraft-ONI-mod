package conservecraft.common.block

import conservecraft.common.menu.AdvancedCraftingTableMenu
import net.minecraft.network.chat.Component
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionResult
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.CraftingTableBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class AdvancedCraftingTableBlock(properties: BlockBehaviour.Properties) : CraftingTableBlock(properties) {
    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: net.minecraft.core.BlockPos,
        player: Player,
        hit: BlockHitResult,
    ): InteractionResult {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        }
        player.openMenu(
            SimpleMenuProvider(
                { id, inventory, _ ->
                    AdvancedCraftingTableMenu(id, inventory, ContainerLevelAccess.create(level, pos))
                },
                Component.translatable("block.conservecraft.advanced_crafting_table")
            )
        )
        player.awardStat(Stats.INTERACT_WITH_CRAFTING_TABLE)
        return InteractionResult.CONSUME
    }
}
