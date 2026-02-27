package mconi.forge

import mconi.common.AbstractModInitializer
import mconi.common.menu.BlueprintBookMenu
import mconi.common.menu.OniMenuTypes
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.inventory.MenuType
import net.minecraftforge.eventbus.api.bus.BusGroup
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ForgeMenus {
    private val MENUS: DeferredRegister<MenuType<*>> =
        DeferredRegister.create(ForgeRegistries.MENU_TYPES, AbstractModInitializer.MOD_ID)

    private val BLUEPRINT_BOOK = MENUS.register("blueprint_book") {
        MenuType(
            { containerId, inventory ->
                @Suppress("UNCHECKED_CAST")
                BlueprintBookMenu(containerId, inventory, BLUEPRINT_BOOK.get() as MenuType<BlueprintBookMenu>)
            },
            FeatureFlagSet.of()
        )
    }

    fun register(busGroup: BusGroup) {
        MENUS.register(busGroup)
    }

    fun bindTypes() {
        @Suppress("UNCHECKED_CAST")
        OniMenuTypes.BLUEPRINT_BOOK = BLUEPRINT_BOOK.get() as MenuType<BlueprintBookMenu>
    }
}
