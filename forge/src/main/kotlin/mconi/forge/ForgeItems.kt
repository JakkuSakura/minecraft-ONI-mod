package mconi.forge

import mconi.common.AbstractModInitializer
import mconi.common.item.OniItems
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
            ITEMS.register(path) { OniItems.createGlassesItem(lens) }
        }
    }

    fun register(busGroup: BusGroup) {
        ITEMS.register(busGroup)
    }
}
