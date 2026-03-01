package mconi.common.block

import mconi.common.block.entity.OniElementBlockEntity
import mconi.common.block.entity.OniMatterBlockEntity
import mconi.common.element.ElementContents
import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams

class OniLiquidBlock(
    private val defaultElements: List<ElementContents>,
    properties: BlockBehaviour.Properties
) : Block(properties), EntityBlock {
    override fun getDrops(state: BlockState, params: LootParams.Builder): MutableList<ItemStack> {
        return mutableListOf()
    }

    override fun asItem(): net.minecraft.world.item.Item {
        return Items.AIR
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return OniMatterBlockEntity(pos, state)
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
        if (defaultElements.isEmpty()) {
            return
        }
        entity.setElements(defaultElements)
    }

    override fun <T : BlockEntity> getTicker(
        level: net.minecraft.world.level.Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? = null
}
