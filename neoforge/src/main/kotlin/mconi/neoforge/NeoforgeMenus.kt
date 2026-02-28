package mconi.neoforge

import mconi.common.AbstractModBootstrap
import mconi.common.menu.BlueprintBookMenu
import mconi.common.menu.OniMenuTypes
import net.minecraft.core.registries.Registries
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.inventory.MenuType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object NeoforgeMenus {
    private val MENUS: DeferredRegister<MenuType<*>> =
        DeferredRegister.create(Registries.MENU, AbstractModBootstrap.MOD_ID)

    private val BLUEPRINT_BOOK: DeferredHolder<MenuType<*>, MenuType<BlueprintBookMenu>> = MENUS.register("blueprint_book", Supplier {
        MenuType(
            { containerId, inventory -> BlueprintBookMenu(containerId, inventory) },
            FeatureFlagSet.of()
        )
    })

    fun register(eventBus: IEventBus) {
        MENUS.register(eventBus)
    }

    fun bindTypes() {
        @Suppress("UNCHECKED_CAST")
        OniMenuTypes.BLUEPRINT_BOOK = BLUEPRINT_BOOK.get() as MenuType<BlueprintBookMenu>
    }

    fun blueprintBookType(): MenuType<BlueprintBookMenu> {
        @Suppress("UNCHECKED_CAST")
        return BLUEPRINT_BOOK.get() as MenuType<BlueprintBookMenu>
    }
}
