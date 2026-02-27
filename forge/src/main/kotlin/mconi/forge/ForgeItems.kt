package mconi.forge

import mconi.common.AbstractModInitializer
import mconi.common.item.OniItemFactory
import mconi.common.item.BlueprintBookItem
import mconi.common.item.BlueprintItem
import mconi.common.item.BottledMatterItem
import mconi.common.item.ElementItem
import mconi.common.item.OniBottledItems
import mconi.common.item.OniItems
import mconi.common.element.OniElements
import mconi.common.sim.model.SystemLens
import net.minecraft.world.item.Item
import net.minecraftforge.eventbus.api.bus.BusGroup
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ForgeItems {
    private val ITEMS: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, AbstractModInitializer.MOD_ID)

    init {
        for (lens in SystemLens.values()) {
            val path = OniItems.pathForLens(lens)
                ?: throw IllegalArgumentException("Missing item id path for lens: $lens")
            val holder = ITEMS.register(path) { OniItems.createGlassesItem(lens) }
            OniItemFactory.registerItem(path) { holder.get() }
        }

        for (spec in OniBottledItems.SPECS) {
            val holder = ITEMS.register(spec.id) {
                BottledMatterItem(
                    Item.Properties(),
                    spec.phase,
                    spec.massKg,
                    spec.temperatureK
                )
            }
            OniItemFactory.registerItem(spec.id) { holder.get() }
        }

        run {
            val holder = ITEMS.register(OniItemFactory.BLUEPRINT_BOOK) { BlueprintBookItem(Item.Properties().stacksTo(1)) }
            OniItemFactory.registerItem(OniItemFactory.BLUEPRINT_BOOK) { holder.get() }
        }
        run {
            val holder = ITEMS.register(OniItemFactory.BLUEPRINT) { BlueprintItem(Item.Properties()) }
            OniItemFactory.registerItem(OniItemFactory.BLUEPRINT) { holder.get() }
        }
        for (elementId in OniItemFactory.ELEMENTS) {
            val holder = ITEMS.register(elementId) { ElementItem(Item.Properties()) }
            OniItemFactory.registerItem(elementId) { holder.get() }
        }
        run {
            val holder = ITEMS.register(OniItemFactory.POWER_STATION_TOOLS) { Item(Item.Properties().stacksTo(1)) }
            OniItemFactory.registerItem(OniItemFactory.POWER_STATION_TOOLS) { holder.get() }
        }

        OniElements.refreshElementItems()
    }

    fun register(busGroup: BusGroup) {
        ITEMS.register(busGroup)
    }
}
