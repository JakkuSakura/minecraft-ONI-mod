package mconi.neoforge

import mconi.common.client.screen.BlueprintBookScreen
import mconi.common.client.worldgen.OniWorldgenPresetEditors
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.event.RegisterPresetEditorsEvent

object NeoforgeClientModEvents {
    fun registerMenuScreens(event: RegisterMenuScreensEvent) {
        event.register(NeoforgeMenus.blueprintBookType(), ::BlueprintBookScreen)
    }

    fun registerPresetEditors(event: RegisterPresetEditorsEvent) {
        OniWorldgenPresetEditors.registerEditors(event::register)
    }
}
