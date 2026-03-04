package conservecraft.neoforge

import conservecraft.common.client.screen.BlueprintBookScreen
import conservecraft.common.client.worldgen.OniWorldgenPresetEditors
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
