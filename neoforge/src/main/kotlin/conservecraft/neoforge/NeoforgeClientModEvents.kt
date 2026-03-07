package conservecraft.neoforge

import conservecraft.common.client.worldgen.OniWorldgenPresetEditors
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.event.RegisterPresetEditorsEvent

object NeoforgeClientModEvents {
    fun registerMenuScreens(event: RegisterMenuScreensEvent) {
    }

    fun registerPresetEditors(event: RegisterPresetEditorsEvent) {
        OniWorldgenPresetEditors.registerEditors(event::register)
    }
}
