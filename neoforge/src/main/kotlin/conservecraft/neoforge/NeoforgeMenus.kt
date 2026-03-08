package conservecraft.neoforge

import conservecraft.common.AbstractModBootstrap
import conservecraft.common.menu.AdvancedCraftingTableMenu
import conservecraft.common.menu.OniMenuTypes
import conservecraft.common.menu.RecyclingTableMenu
import net.minecraft.core.registries.Registries
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.MenuType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object NeoforgeMenus {
    private val MENUS: DeferredRegister<MenuType<*>> = DeferredRegister.create(Registries.MENU, AbstractModBootstrap.MOD_ID)
    private val ADVANCED: DeferredHolder<MenuType<*>, MenuType<AdvancedCraftingTableMenu>> =
        MENUS.register(OniMenuTypes.ADVANCED_CRAFTING_TABLE, Supplier { MenuType(::AdvancedCraftingTableMenu, FeatureFlags.DEFAULT_FLAGS) })
    private val RECYCLING: DeferredHolder<MenuType<*>, MenuType<RecyclingTableMenu>> =
        MENUS.register(OniMenuTypes.RECYCLING_TABLE, Supplier { MenuType(::RecyclingTableMenu, FeatureFlags.DEFAULT_FLAGS) })

    fun register(eventBus: IEventBus) {
        MENUS.register(eventBus)
    }

    fun advancedType(): MenuType<AdvancedCraftingTableMenu> = ADVANCED.get()

    fun recyclingType(): MenuType<RecyclingTableMenu> = RECYCLING.get()

    fun bindTypes() {
        OniMenuTypes.bindAdvancedCraftingTable(advancedType())
        OniMenuTypes.bindRecyclingTable(recyclingType())
    }
}
