package mconi.neoforge

import mconi.common.AbstractModBootstrap
import mconi.common.block.OniBlockFactory
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
        DeferredRegister.create(Registries.BLOCK, AbstractModBootstrap.MOD_ID)
    private val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(AbstractModBootstrap.MOD_ID)
    private val BLOCK_HOLDERS: MutableMap<String, DeferredHolder<Block, Block>> = HashMap()

    init {
        for (entry in OniBlockFactory.entries()) {
            val id = Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:${entry.id}")
                ?: throw IllegalArgumentException("Invalid block id path: ${entry.id}")
            val holder = BLOCKS.register(entry.id, Supplier { OniBlockFactory.createBlock(entry.id) })
            BLOCK_HOLDERS[entry.id] = holder
            if (entry.kind == OniBlockFactory.BlockKind.SOLID) {
                val itemKey = ResourceKey.create(Registries.ITEM, id)
                ITEMS.register(entry.id, Supplier { BlockItem(holder.get(), Item.Properties().setId(itemKey)) })
            }
        }
    }

    fun register(eventBus: IEventBus) {
        BLOCKS.register(eventBus)
        ITEMS.register(eventBus)
    }

    fun blockHolder(id: String): DeferredHolder<Block, Block> {
        return BLOCK_HOLDERS[id] ?: throw IllegalArgumentException("Unknown block id: $id")
    }
}
