package mconi.common.block

import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import mconi.common.sim.OniServices

class PrintingPodBlock(
    blockId: String,
    elements: List<mconi.common.element.ElementStack> = emptyList(),
    properties: BlockBehaviour.Properties
) : OniSolidBlock(blockId, elements, properties) {
    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: net.minecraft.core.BlockPos,
        player: Player,
        hit: BlockHitResult,
    ): InteractionResult {
        return handleUse(level, player)
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
        return handleUse(level, player)
    }

    private fun handleUse(level: Level, player: Player): InteractionResult {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        }

        val construction = OniServices.systemRuntime().constructionState()
        val task = construction.nextTaskNeedingMaterials()
        if (task == null) {
            player.displayClientMessage(Component.literal("No pending construction tasks."), true)
            return InteractionResult.CONSUME
        }

        val needed = task.requiredMaterialUnits - task.depositedMaterials
        if (needed <= 0) {
            player.displayClientMessage(Component.literal("Construction already fully supplied."), true)
            return InteractionResult.CONSUME
        }

        player.displayClientMessage(
            Component.literal("Printing Pod no longer accepts mixed materials."),
            true
        )

        return InteractionResult.CONSUME
    }
}
