package mconi.common.block

import mconi.common.content.OniMaterialMass
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import kotlin.math.min

open class OniMassBlock(
    private val blockId: String,
    properties: BlockBehaviour.Properties
) : Block(properties) {
    override fun getDrops(state: BlockState, params: LootParams.Builder): MutableList<ItemStack> {
        val item = asItem()
        if (item == Items.AIR) {
            return mutableListOf()
        }
        val total = OniMaterialMass.blockDigYieldKg(blockId)
        val maxStack = ItemStack(item).maxStackSize
        val drops = mutableListOf<ItemStack>()
        var remaining = total
        while (remaining > 0) {
            val count = min(remaining, maxStack)
            drops.add(ItemStack(item, count))
            remaining -= count
        }
        return drops
    }
}
