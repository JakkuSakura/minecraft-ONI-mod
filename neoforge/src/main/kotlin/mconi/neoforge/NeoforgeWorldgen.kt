package mconi.neoforge

import net.neoforged.bus.api.IEventBus
import mconi.common.world.OniWorldgenBootstrap

object NeoforgeWorldgen {
    @Suppress("UNUSED_PARAMETER")
    fun register(eventBus: IEventBus) {
        OniWorldgenBootstrap.register()
    }
}
