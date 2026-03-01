package mconi.common.block

import mconi.common.block.entity.ConstructionSiteBlockEntity
import mconi.common.block.entity.OniElementBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams

class ConstructionSiteBlock(properties: BlockBehaviour.Properties) : net.minecraft.world.level.block.Block(properties), EntityBlock {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return ConstructionSiteBlockEntity(pos, state)
    }

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL

    override fun onPlace(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        oldState: BlockState,
        movedByPiston: Boolean
    ) {
        super.onPlace(state, level, pos, oldState, movedByPiston)
        if (level.isClientSide || oldState.block == state.block) {
            return
        }
        val entity = level.getBlockEntity(pos) as? OniElementBlockEntity ?: return
        if (entity.elements().isNotEmpty()) {
            return
        }
        val defaults = OniBlockFactory.defaultElements(state.block)
        if (defaults.isEmpty()) {
            return
        }
        entity.setElements(defaults)
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hit: BlockHitResult
    ): InteractionResult {
        return handleUse(level, pos, player)
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        return handleUse(level, pos, player)
    }

    private fun handleUse(level: Level, pos: BlockPos, player: Player): InteractionResult {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        }
        val blockEntity = level.getBlockEntity(pos) as? ConstructionSiteBlockEntity
            ?: return InteractionResult.CONSUME
        blockEntity.startInteraction(player)
        return InteractionResult.CONSUME
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        type: net.minecraft.world.level.block.entity.BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        if (level.isClientSide) {
            return null
        }
        return BlockEntityTicker { tickerLevel, pos, _, blockEntity ->
            if (blockEntity is ConstructionSiteBlockEntity) {
                blockEntity.serverTick(tickerLevel as net.minecraft.server.level.ServerLevel)
            }
        }
    }

    override fun getDrops(state: BlockState, params: LootParams.Builder): MutableList<ItemStack> {
        val blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) as? ConstructionSiteBlockEntity
            ?: return mutableListOf()
        return blockEntity.refundStacks()
    }
}
