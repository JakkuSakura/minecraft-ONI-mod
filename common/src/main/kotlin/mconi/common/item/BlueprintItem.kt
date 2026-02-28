package mconi.common.item

import mconi.common.item.OniBlueprintSelectionNbt
import mconi.common.item.OniBlueprintTargets
import mconi.common.sim.OniConstructionSitePlacer
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext

class BlueprintItem(properties: Item.Properties) : OniDescribedItem(properties) {
    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player ?: return InteractionResult.PASS
        val level = context.level
        val stack = context.itemInHand

        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        }

        val selection = OniBlueprintSelectionNbt.readFrom(stack)
        if (selection == null) {
            player.displayClientMessage(Component.literal("Blueprint has no selection."), true)
            return InteractionResult.CONSUME
        }

        val targetBlock = OniBlueprintTargets.blockIdFor(selection.blueprintId)
        if (targetBlock == null) {
            player.displayClientMessage(Component.literal("Blueprint target is missing."), true)
            return InteractionResult.CONSUME
        }

        val placed = OniConstructionSitePlacer.place(level, context.clickedPos, context.clickedFace, selection, player)
        if (!placed) {
            player.displayClientMessage(Component.literal("Cannot place construction site here."), true)
        }

        return InteractionResult.CONSUME
    }
}
