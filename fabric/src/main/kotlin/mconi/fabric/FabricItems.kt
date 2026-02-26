package mconi.fabric

import mconi.common.AbstractModInitializer
import mconi.common.item.BottledMatterItem
import mconi.common.item.OniBottledItems
import mconi.common.item.OniCreativeTabs
import mconi.common.item.OniItems
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
            val path = OniItems.pathForLens(lens)
                ?: throw IllegalArgumentException("Missing item id path for lens: $lens")
            val id = id(path)
            val key = ResourceKey.create(Registries.ITEM, id)
            val item = OniItems.createGlassesItem(lens, Item.Properties().setId(key).stacksTo(1))
            Registry.register(BuiltInRegistries.ITEM, id, item)

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
                spec.massKg,
                spec.temperatureK
            )
            Registry.register(BuiltInRegistries.ITEM, id, item)
        }
    }

    private fun id(path: String): Identifier {
        return Identifier.tryParse("${AbstractModInitializer.MOD_ID}:$path")
            ?: throw IllegalArgumentException("Invalid item id path: $path")
    }
}
