package mconi.common.block

import mconi.common.block.entity.OniMatterBlockEntity
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
    val massKg: Int,
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

    override fun <T : BlockEntity> getTicker(
        level: net.minecraft.world.level.Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? = null
}
