package mconi.fabric

import mconi.common.AbstractModBootstrap
import mconi.common.item.BlueprintBookItem
import mconi.common.item.BlueprintItem
import mconi.common.item.BottledMatterItem
import mconi.common.item.ElementItem
import mconi.common.item.OniBottledItems
import mconi.common.item.OniCreativeTabs
import mconi.common.item.OniItemFactory
import mconi.common.element.OniElements
import mconi.common.sim.model.SystemLens
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

            if (lens == SystemLens.OXYGEN) {
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
            val item = BottledMatterItem(
                Item.Properties().setId(key),
                spec.phase,
                spec.mass,
                spec.temperatureK
            )
            Registry.register(BuiltInRegistries.ITEM, id, item)
            OniItemFactory.registerItem(spec.id) { item }
        }

        run {
            val id = id(OniItemFactory.BLUEPRINT_BOOK)
            val key = ResourceKey.create(Registries.ITEM, id)
            val item = BlueprintBookItem(Item.Properties().setId(key).stacksTo(1))
            Registry.register(BuiltInRegistries.ITEM, id, item)
            OniItemFactory.registerItem(OniItemFactory.BLUEPRINT_BOOK) { item }
        }

        run {
            val id = id(OniItemFactory.BLUEPRINT)
            val key = ResourceKey.create(Registries.ITEM, id)
            val item = BlueprintItem(Item.Properties().setId(key))
            Registry.register(BuiltInRegistries.ITEM, id, item)
            OniItemFactory.registerItem(OniItemFactory.BLUEPRINT) { item }
        }

        for (elementId in OniItemFactory.ELEMENTS) {
            val id = id(elementId)
            val key = ResourceKey.create(Registries.ITEM, id)
            val item = ElementItem(Item.Properties().setId(key))
            Registry.register(BuiltInRegistries.ITEM, id, item)
            OniItemFactory.registerItem(elementId) { item }
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
