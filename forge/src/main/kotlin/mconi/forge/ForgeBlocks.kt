package mconi.forge

import mconi.common.AbstractModInitializer
import mconi.common.block.OniBlockFactory
import net.minecraft.resources.Identifier
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraftforge.eventbus.api.bus.BusGroup
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ForgeBlocks {
    private val BLOCKS: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, AbstractModInitializer.MOD_ID)
    private val ITEMS: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, AbstractModInitializer.MOD_ID)
    private val BLOCK_HOLDERS: MutableMap<String, RegistryObject<Block>> = HashMap()

    init {
        for (entry in OniBlockFactory.entries()) {
            val id = Identifier.tryParse("${AbstractModInitializer.MOD_ID}:${entry.id}")
                ?: throw IllegalArgumentException("Invalid block id path: ${entry.id}")
            val holder = BLOCKS.register(entry.id) { OniBlockFactory.createBlock(entry.id) }
            BLOCK_HOLDERS[entry.id] = holder
            if (OniBlockFactory.SOLIDS.contains(holder.get())) {
                ITEMS.register(entry.id) { BlockItem(holder.get(), Item.Properties()) }
            }
        }
    }

    fun register(busGroup: BusGroup) {
        BLOCKS.register(busGroup)
        ITEMS.register(busGroup)
    }

    fun blockHolder(id: String): RegistryObject<Block> {
        return BLOCK_HOLDERS[id] ?: throw IllegalArgumentException("Unknown block id: $id")
    }
}
