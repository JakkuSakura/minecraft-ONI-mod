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
import mconi.common.content.OniItemTags
import mconi.common.sim.OniConstructionMaterials
import mconi.common.sim.OniServices

class PrintingPodBlock(
    blockId: String,
    properties: BlockBehaviour.Properties
) : OniMassBlock(blockId, properties) {
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

        val construction = OniServices.simulationRuntime().constructionState()
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

        val deposited = OniConstructionMaterials.depositFromPlayer(player, OniItemTags.BUILD_MATERIALS, needed)
        if (deposited <= 0) {
            player.displayClientMessage(Component.literal("No build materials in inventory."), true)
            return InteractionResult.CONSUME
        }

        task.depositedMaterials += deposited
        val remaining = (task.requiredMaterialUnits - task.depositedMaterials).coerceAtLeast(0)
        player.displayClientMessage(
            Component.literal("Deposited $deposited kg. Remaining: $remaining kg."),
            true
        )

        return InteractionResult.CONSUME
    }
}
