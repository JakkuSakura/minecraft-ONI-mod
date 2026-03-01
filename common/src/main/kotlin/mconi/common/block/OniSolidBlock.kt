package mconi.common.block

import mconi.common.block.entity.OniElementBlockEntity
import mconi.common.block.entity.OniMatterBlockEntity
import mconi.common.element.ElementContents
import mconi.common.element.OniElements
import mconi.common.item.OniItemFactory
import mconi.common.item.OniItemMass
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams

open class OniSolidBlock(
    private val blockId: String,
    private val defaultElements: List<ElementContents> = emptyList(),
    properties: BlockBehaviour.Properties
) : Block(properties), EntityBlock {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return OniMatterBlockEntity(pos, state)
    }

    override fun getDrops(state: BlockState, params: LootParams.Builder): MutableList<ItemStack> {
        val entity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) as? OniElementBlockEntity
        val elements = entity?.elements().orEmpty()
        return dropStacks(elements)
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

    private fun dropStacks(elements: List<ElementContents>): MutableList<ItemStack> {
        if (elements.isEmpty()) {
            return mutableListOf()
        }
        val drops: MutableList<ItemStack> = ArrayList()
        for (element in elements) {
            if (element.mass <= 0.0) {
                continue
            }
            val spec = OniElements.REGISTRY.byId(element.elementId) ?: continue
            val item = OniItemFactory.itemById(spec.itemId.toString()) ?: continue
            val stack = ItemStack(item, 1)
            OniItemMass.setStackMass(stack, element.mass)
            drops.add(stack)
        }
        return drops
    }
}
