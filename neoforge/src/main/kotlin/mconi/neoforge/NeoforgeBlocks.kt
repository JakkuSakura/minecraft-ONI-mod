package mconi.neoforge

import mconi.common.AbstractModInitializer
import mconi.common.block.OniBlockFactory
import mconi.common.content.OniBlockIds
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.HashMap
import java.util.function.Supplier

object NeoforgeBlocks {
    private val BLOCKS: DeferredRegister<Block> =
        DeferredRegister.create(Registries.BLOCK, AbstractModInitializer.MOD_ID)
    private val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(AbstractModInitializer.MOD_ID)
    private val BLOCK_HOLDERS: MutableMap<String, DeferredHolder<Block, Block>> = HashMap()

    init {
        for (path in OniBlockIds.ALL) {
            val id = Identifier.tryParse("${AbstractModInitializer.MOD_ID}:$path")
                ?: throw IllegalArgumentException("Invalid block id path: $path")
            val blockKey = ResourceKey.create(Registries.BLOCK, id)
            val holder = BLOCKS.register(path, Supplier { OniBlockFactory.createBlock(path, blockKey) })
            BLOCK_HOLDERS[path] = holder
            if (OniBlockIds.SOLIDS.contains(path)) {
                val itemKey = ResourceKey.create(Registries.ITEM, id)
                ITEMS.register(path, Supplier { BlockItem(holder.get(), Item.Properties().setId(itemKey)) })
            }
        }
    }

    fun register(eventBus: IEventBus) {
        BLOCKS.register(eventBus)
        ITEMS.register(eventBus)
    }
}
