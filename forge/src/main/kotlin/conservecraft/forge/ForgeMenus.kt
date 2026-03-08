package conservecraft.forge

import conservecraft.common.AbstractModBootstrap
import conservecraft.common.menu.AdvancedCraftingTableMenu
import conservecraft.common.menu.OniMenuTypes
import conservecraft.common.menu.RecyclingTableMenu
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.MenuType
import net.minecraftforge.eventbus.api.bus.BusGroup
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ForgeMenus {
    private val MENUS: DeferredRegister<MenuType<*>> = DeferredRegister.create(ForgeRegistries.MENU_TYPES, AbstractModBootstrap.MOD_ID)
    private val ADVANCED = MENUS.register(OniMenuTypes.ADVANCED_CRAFTING_TABLE) {
        MenuType(::AdvancedCraftingTableMenu, FeatureFlags.DEFAULT_FLAGS)
    }
    private val RECYCLING = MENUS.register(OniMenuTypes.RECYCLING_TABLE) {
        MenuType(::RecyclingTableMenu, FeatureFlags.DEFAULT_FLAGS)
    }

    fun register(busGroup: BusGroup) {
        MENUS.register(busGroup)
    }

    fun bindTypes() {
        OniMenuTypes.bindAdvancedCraftingTable(ADVANCED.get())
        OniMenuTypes.bindRecyclingTable(RECYCLING.get())
    }
}
