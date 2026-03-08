package conservecraft.fabric

import conservecraft.common.AbstractModBootstrap
import conservecraft.common.element.OniElements
import conservecraft.common.item.BottledMatterItem
import conservecraft.common.item.OniBottledItems
import conservecraft.common.item.OniCreativeTabs
import conservecraft.common.item.OniItemFactory
import conservecraft.common.item.OniSolidItems
import conservecraft.common.item.PortableAdvancedCraftingTableItem
import conservecraft.common.sim.model.SystemLens
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item

object FabricItems {
    private var registered = false

    fun register() {
        if (registered) {
            return
        }
        registered = true

        for (lens in SystemLens.values()) {
            val path = OniItemFactory.pathForLens(lens)
                ?: throw IllegalArgumentException("Missing item id path for lens: $lens")
            val id = id(path)
            val key = ResourceKey.create(Registries.ITEM, id)
            val item = OniItemFactory.createGlassesItem(lens, Item.Properties().setId(key).stacksTo(1))
            Registry.register(BuiltInRegistries.ITEM, id, item)
            OniItemFactory.registerItem(path) { item }

            if (lens == SystemLens.GAS) {
                Registry.register(
                    BuiltInRegistries.CREATIVE_MODE_TAB,
                    OniCreativeTabs.TAB_KEY,
                    OniCreativeTabs.createTab(item)
                )
            }
        }

        for (spec in OniBottledItems.SPECS) {
            val id = id(spec.id)
            val key = ResourceKey.create(Registries.ITEM, id)
            val item = BottledMatterItem(Item.Properties().setId(key), spec.phase, spec.mass, spec.temperatureK)
            Registry.register(BuiltInRegistries.ITEM, id, item)
            OniItemFactory.registerItem(spec.id) { item }
        }

        for (path in OniSolidItems.allRegistryPaths()) {
            val id = id(path)
            val key = ResourceKey.create(Registries.ITEM, id)
            val item = OniSolidItems.createItem(path, Item.Properties().setId(key))
            Registry.register(BuiltInRegistries.ITEM, id, item)
            OniItemFactory.registerItem(path) { item }
        }

        run {
            val path = OniItemFactory.PORTABLE_ADVANCED_CRAFTING_TABLE
            val id = id(path)
            val key = ResourceKey.create(Registries.ITEM, id)
            val item = PortableAdvancedCraftingTableItem(Item.Properties().setId(key).stacksTo(1))
            Registry.register(BuiltInRegistries.ITEM, id, item)
            OniItemFactory.registerItem(path) { item }
        }

        run {
            val id = id(OniItemFactory.POWER_STATION_TOOLS)
            val key = ResourceKey.create(Registries.ITEM, id)
            val item = Item(Item.Properties().setId(key).stacksTo(1))
            Registry.register(BuiltInRegistries.ITEM, id, item)
            OniItemFactory.registerItem(OniItemFactory.POWER_STATION_TOOLS) { item }
        }

        OniElements.refreshElementItems()
    }

    private fun id(path: String): Identifier {
        return Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:$path")
            ?: throw IllegalArgumentException("Invalid item id path: $path")
    }
}
