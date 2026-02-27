package mconi.common.block

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams

class OniFluidBlock(properties: BlockBehaviour.Properties) : Block(properties) {
    override fun getDrops(state: BlockState, params: LootParams.Builder): MutableList<ItemStack> {
        return mutableListOf()
    }

    override fun asItem(): net.minecraft.world.item.Item {
        return Items.AIR
    }
}
