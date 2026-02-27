package mconi.common.block

import mconi.common.element.ElementStack
import mconi.common.element.OniElementStore
import mconi.common.item.OniBlueprintRegistry
import mconi.common.item.OniBlueprintTargets
import mconi.common.item.OniItemFactory
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams

open class OniSolidBlock(
    private val blockId: String,
    private val dropElementId: String? = null,
    properties: BlockBehaviour.Properties
) : Block(properties) {
    override fun getDrops(state: BlockState, params: LootParams.Builder): MutableList<ItemStack> {
        val level = params.level as? ServerLevel
        val origin = params.getOptionalParameter(LootContextParams.ORIGIN)
        val pos = origin?.let { BlockPos.containing(it) }
        return dropStacks(level, pos)
    }

    private fun dropStacks(level: ServerLevel?, pos: BlockPos?): MutableList<ItemStack> {
        val elements = elementsForBreak(level, pos)
        val drops: MutableList<ItemStack> = ArrayList()
        for (element in elements) {
            val item = OniItemFactory.itemById(element.itemId) ?: continue
            var remaining = element.amount
            val maxStack = ItemStack(item).maxStackSize.coerceAtLeast(1)
            while (remaining > 0) {
                val count = minOf(remaining, maxStack)
                drops.add(ItemStack(item, count))
                remaining -= count
            }
        }
        return drops
    }

    private fun elementsForBreak(level: ServerLevel?, pos: BlockPos?): List<ElementStack> {
        if (level != null && pos != null) {
            val stored = OniElementStore.get(level).takeElements(pos)
            if (stored.isNotEmpty()) {
                return stored
            }
        }
        return elementsForBlock()
    }

    private fun elementsForBlock(): List<ElementStack> {
        if (dropElementId != null) {
            val amount = OniBlockFactory.blockDigYieldKg(blockId).coerceAtLeast(1)
            return listOf(ElementStack(dropElementId, amount))
        }

        val blueprintId = OniBlueprintTargets.blueprintIdFor(blockId) ?: return emptyList()
        val blueprint = OniBlueprintRegistry.get(blueprintId) ?: return emptyList()
        return blueprint.materialSlots
            .mapNotNull { slot ->
                val itemId = slot.allowedItems.firstOrNull() ?: return@mapNotNull null
                if (slot.amount <= 0) {
                    return@mapNotNull null
                }
                ElementStack(itemId, slot.amount)
            }
    }
}
