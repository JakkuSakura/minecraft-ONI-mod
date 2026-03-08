package conservecraft.fabric

import conservecraft.common.AbstractModBootstrap
import conservecraft.common.menu.AdvancedCraftingTableMenu
import conservecraft.common.menu.OniMenuTypes
import conservecraft.common.menu.RecyclingTableMenu
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.MenuType

object FabricMenus {
    fun register() {
        val advancedId = Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:${OniMenuTypes.ADVANCED_CRAFTING_TABLE}")
            ?: throw IllegalArgumentException("Invalid menu id")
        val recyclingId = Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:${OniMenuTypes.RECYCLING_TABLE}")
            ?: throw IllegalArgumentException("Invalid menu id")
        OniMenuTypes.bindAdvancedCraftingTable(
            Registry.register(
                BuiltInRegistries.MENU,
                advancedId,
                MenuType(::AdvancedCraftingTableMenu, FeatureFlags.DEFAULT_FLAGS)
            )
        )
        OniMenuTypes.bindRecyclingTable(
            Registry.register(
                BuiltInRegistries.MENU,
                recyclingId,
                MenuType(::RecyclingTableMenu, FeatureFlags.DEFAULT_FLAGS)
            )
        )
    }
}
