package mconi.neoforge

import mconi.common.client.screen.BlueprintBookScreen
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent

object NeoforgeClientModEvents {
    fun registerMenuScreens(event: RegisterMenuScreensEvent) {
        event.register(NeoforgeMenus.blueprintBookType(), ::BlueprintBookScreen)
    }
}
