package conservecraft.neoforge

import conservecraft.common.client.screen.AdvancedCraftingTableScreen
import conservecraft.common.client.screen.RecyclingTableScreen
import conservecraft.common.client.worldgen.OniWorldgenPresetEditors
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.event.RegisterPresetEditorsEvent

object NeoforgeClientModEvents {
    fun registerMenuScreens(event: RegisterMenuScreensEvent) {
        event.register(NeoforgeMenus.advancedType(), ::AdvancedCraftingTableScreen)
        event.register(NeoforgeMenus.recyclingType(), ::RecyclingTableScreen)
    }

    fun registerPresetEditors(event: RegisterPresetEditorsEvent) {
        OniWorldgenPresetEditors.registerEditors(event::register)
    }
}
