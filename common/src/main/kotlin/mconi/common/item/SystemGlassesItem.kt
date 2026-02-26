package mconi.common.item

import mconi.common.sim.OniServices
import mconi.common.sim.OniSystemInspector
import mconi.common.sim.model.SystemLens
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level

class SystemGlassesItem(
    properties: Item.Properties,
    private val systemLens: SystemLens,
) : Item(properties) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        if (!level.isClientSide) {
            val pos: BlockPos = player.blockPosition()
            val cell = OniServices.simulationRuntime().grid().getOrCreateCellAtBlock(
                pos.x,
                pos.y,
                pos.z,
                OniServices.simulationRuntime().config().cellSize()
            )
            player.displayClientMessage(
                Component.literal(
                    "System glasses [${systemLens.name}] at (${pos.x},${pos.y},${pos.z}):"
                ),
                false
            )
            for (property in OniSystemInspector.inspect(OniServices.simulationRuntime(), systemLens, cell)) {
                player.displayClientMessage(
                    Component.literal("[${property.layer()}] ${property.key()}=${property.value()}"),
                    false
                )
            }
        }
        return InteractionResult.SUCCESS
    }
}
