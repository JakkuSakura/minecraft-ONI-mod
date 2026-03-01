package mconi.common.block

import mconi.common.block.entity.OniConduitBlockEntity
import mconi.common.block.entity.OniElementBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState

class ConduitBlock(properties: BlockBehaviour.Properties) : Block(properties), EntityBlock {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return OniConduitBlockEntity(pos, state)
    }

    override fun onPlace(
        state: BlockState,
        level: net.minecraft.world.level.Level,
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

    override fun <T : BlockEntity> getTicker(
        level: net.minecraft.world.level.Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? = null
}
