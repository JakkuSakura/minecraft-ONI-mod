package mconi.common.refining

import mconi.common.block.OniBlockFactory
import mconi.common.block.entity.OniElementBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState

class RefiningMachineBlock(properties: BlockBehaviour.Properties) : net.minecraft.world.level.block.Block(properties), EntityBlock {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return RefiningMachineBlockEntity(pos, state)
    }

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL

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
}
