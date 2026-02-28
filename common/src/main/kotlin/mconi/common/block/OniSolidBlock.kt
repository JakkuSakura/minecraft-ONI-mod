package mconi.common.block

import mconi.common.element.ElementStack
import mconi.common.item.OniItemFactory
import mconi.common.item.OniItemMass
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
// TODO: is it BlockSpec or instance of Block?
open class OniSolidBlock(
    private val blockId: String,
    private val elements: List<ElementStack> = emptyList(),
    properties: BlockBehaviour.Properties
) : Block(properties) {
    override fun getDrops(state: BlockState, params: LootParams.Builder): MutableList<ItemStack> {
        val level = params.level as? ServerLevel
        val origin = params.getOptionalParameter(LootContextParams.ORIGIN)
        val pos = origin?.let { BlockPos.containing(it) }
        return dropStacks(level, pos)
    }

    private fun dropStacks(level: ServerLevel?, pos: BlockPos?): MutableList<ItemStack> {
        val elements = elementsForBlock()
        val drops: MutableList<ItemStack> = ArrayList()
        for (element in elements) {
            val item = OniItemFactory.itemById(element.itemId) ?: continue
            if (element.amount <= 0) {
                continue
            }
            val stack = ItemStack(item, 1)
            OniItemMass.setStackWeightKg(stack, element.amount.toDouble())
            drops.add(stack)
        }
        return drops
    }

    private fun elementsForBlock(): List<ElementStack> {
        if (elements.isNotEmpty()) {
            return elements
        }
        return emptyList()
    }
}
