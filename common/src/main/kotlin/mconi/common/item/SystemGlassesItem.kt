package mconi.common.item

import mconi.common.sim.OniServices
import mconi.common.sim.OniSystemInspector
import mconi.common.sim.model.SystemLens
import mconi.common.world.OniChunkDataAccess
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.server.level.ServerLevel

class SystemGlassesItem(
    properties: Item.Properties,
    private val systemLens: SystemLens,
) : OniDescribedItem(properties) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        if (!level.isClientSide) {
            val serverLevel = level as? ServerLevel ?: return InteractionResult.SUCCESS
            val pos: BlockPos = player.blockPosition()
            val cell = OniChunkDataAccess.getOrCreate(serverLevel, pos)
            player.displayClientMessage(
                Component.literal(
                    "System glasses [${systemLens.name}] at (${pos.x},${pos.y},${pos.z}):"
                ),
                false
            )
            for (property in OniSystemInspector.inspect(OniServices.simulationRuntime(), systemLens, cell, player)) {
                player.displayClientMessage(
                    Component.literal("[${property.layer()}] ${property.key()}=${property.value()}"),
                    false
                )
            }
        }
        return InteractionResult.SUCCESS
    }
}
