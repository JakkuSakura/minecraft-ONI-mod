package conservecraft.forge

import conservecraft.common.AbstractModBootstrap
import conservecraft.common.item.OniItemFactory
import conservecraft.common.item.BottledMatterItem
import conservecraft.common.item.PortableAdvancedCraftingTableItem
import conservecraft.common.item.ElementItem
import conservecraft.common.item.OniBottledItems
import conservecraft.common.sim.model.SystemLens
import net.minecraft.world.item.Item
import net.minecraftforge.eventbus.api.bus.BusGroup
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ForgeItems {
    private val ITEMS: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, AbstractModBootstrap.MOD_ID)

    init {
        for (lens in SystemLens.values()) {
            val path = OniItemFactory.pathForLens(lens)
                ?: throw IllegalArgumentException("Missing item id path for lens: $lens")
            val holder = ITEMS.register(path) { OniItemFactory.createGlassesItem(lens) }
            OniItemFactory.registerItem(path) { holder.get() }
        }

        for (spec in OniBottledItems.SPECS) {
            val holder = ITEMS.register(spec.id) {
                BottledMatterItem(
                    Item.Properties(),
                    spec.phase,
                    spec.mass,
                    spec.temperatureK
                )
            }
            OniItemFactory.registerItem(spec.id) { holder.get() }
        }
        for (elementId in OniItemFactory.ELEMENTS) {
            val holder = ITEMS.register(elementId) { ElementItem(Item.Properties()) }
            OniItemFactory.registerItem(elementId) { holder.get() }
        }
        run {
            val holder = ITEMS.register(OniItemFactory.POWER_STATION_TOOLS) { Item(Item.Properties().stacksTo(1)) }
            OniItemFactory.registerItem(OniItemFactory.POWER_STATION_TOOLS) { holder.get() }
        }

    }

    fun register(busGroup: BusGroup) {
        ITEMS.register(busGroup)
    }
}
