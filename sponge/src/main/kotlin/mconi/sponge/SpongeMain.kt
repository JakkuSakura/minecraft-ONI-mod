package mconi.sponge

import mconi.common.AbstractModInitializer
import mconi.common.LoaderType
import org.spongepowered.api.Server
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.lifecycle.StartedEngineEvent
import org.spongepowered.plugin.builtin.jvm.Plugin

/**
 * Sponge entrypoint.
 */
@Plugin(AbstractModInitializer.MOD_ID)
class SpongeMain : AbstractModInitializer() {
    init {
        loaderType = LoaderType.Sponge
        INSTANCE = this
    }

    @Listener
    fun onServerStart(event: StartedEngineEvent<Server>) {
        super.onInitializeServer()
    }

    override fun createInitialBindings() {
    }

    override fun createServerProxy(isDedicated: Boolean): IEventProxy = SpongeServerProxy()

    override fun initializeModCompat() {
    }

    override fun createClientProxy(): IEventProxy = NoopEventProxy

    companion object {
        @JvmStatic
        lateinit var INSTANCE: SpongeMain
            private set
    }

    private object NoopEventProxy : IEventProxy {
        override fun registerEvents() {
        }
    }
}
