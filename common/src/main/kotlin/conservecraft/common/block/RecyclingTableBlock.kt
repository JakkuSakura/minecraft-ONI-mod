package conservecraft.common.block

import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class RecyclingTableBlock(properties: BlockBehaviour.Properties) : net.minecraft.world.level.block.Block(properties) {
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
        player.displayClientMessage(Component.literal("Use an item on the Recycling Table to recover its elements."), true)
        return InteractionResult.CONSUME
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
        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        }
        val serverLevel = level as? net.minecraft.server.level.ServerLevel ?: return InteractionResult.CONSUME
        val recycled = RecyclingTableLogic.recycleHeldItem(serverLevel, player, pos, stack)
        if (!recycled) {
            player.displayClientMessage(Component.literal("That item cannot be recycled into known elements."), true)
            return InteractionResult.PASS
        }
        player.displayClientMessage(Component.literal("Recovered elemental materials."), true)
        return InteractionResult.CONSUME
    }
}
