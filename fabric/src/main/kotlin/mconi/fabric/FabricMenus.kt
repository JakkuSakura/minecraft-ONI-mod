package mconi.fabric

import mconi.common.AbstractModInitializer
import mconi.common.menu.BlueprintBookMenu
import mconi.common.menu.OniMenuTypes
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.inventory.MenuType

object FabricMenus {
    private var registered = false

    fun register() {
        if (registered) {
            return
        }
        registered = true

        val id = Identifier.tryParse("${AbstractModInitializer.MOD_ID}:blueprint_book")
            ?: throw IllegalArgumentException("Invalid menu id")
        val type = MenuType({ containerId, inventory -> BlueprintBookMenu(containerId, inventory) }, FeatureFlagSet.of())
        Registry.register(BuiltInRegistries.MENU, id, type)
        OniMenuTypes.BLUEPRINT_BOOK = type
    }
}
