package conservecraft.common.block

import conservecraft.common.menu.RecyclingTableMenu
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.inventory.ContainerLevelAccess

class RecyclingTableBlock(properties: BlockBehaviour.Properties) : Block(properties) {
    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: net.minecraft.core.BlockPos,
        player: Player,
        hit: BlockHitResult,
    ): InteractionResult {
        return openMenu(level, pos, player)
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: net.minecraft.core.BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult,
    ): InteractionResult {
        return openMenu(level, pos, player)
    }

    private fun openMenu(level: Level, pos: net.minecraft.core.BlockPos, player: Player): InteractionResult {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        }
        player.openMenu(
            SimpleMenuProvider(
                { id, inventory, _ -> RecyclingTableMenu(id, inventory, ContainerLevelAccess.create(level, pos)) },
                Component.translatable("block.conservecraft.recycling_table")
            )
        )
        return InteractionResult.CONSUME
    }
}
