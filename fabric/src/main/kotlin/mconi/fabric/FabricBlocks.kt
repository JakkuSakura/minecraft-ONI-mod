package mconi.fabric

import mconi.common.AbstractModInitializer
import mconi.common.block.OniBlockFactory
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

object FabricBlocks {
    private var registered = false

    fun register() {
        if (registered) {
            return
        }
        registered = true

        for (entry in OniBlockFactory.entries()) {
            val id = id(entry.id)
            val block: Block = OniBlockFactory.createBlock(entry.id)
            Registry.register(BuiltInRegistries.BLOCK, id, block)

            if (OniBlockFactory.SOLIDS.contains(block)) {
                val itemKey = ResourceKey.create(Registries.ITEM, id)
                val blockItem = BlockItem(block, Item.Properties().setId(itemKey))
                Registry.register(BuiltInRegistries.ITEM, id, blockItem)
            }
        }
    }

    private fun id(path: String): Identifier {
        return Identifier.tryParse("${AbstractModInitializer.MOD_ID}:$path")
            ?: throw IllegalArgumentException("Invalid block id path: $path")
    }
}
