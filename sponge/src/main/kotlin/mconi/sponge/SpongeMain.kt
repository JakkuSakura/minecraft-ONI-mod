package mconi.sponge

import mconi.common.AbstractModBootstrap
import mconi.common.LoaderType
import org.spongepowered.api.Server
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.lifecycle.StartedEngineEvent
import org.spongepowered.plugin.builtin.jvm.Plugin

/**
 * Sponge entrypoint.
 */
@Plugin(AbstractModBootstrap.MOD_ID)
class SpongeMain : AbstractModBootstrap() {
    init {
        loaderType = LoaderType.Sponge
        INSTANCE = this
    }

    @Listener
    fun onServerStart(event: StartedEngineEvent<Server>) {
        super.onSetupServer()
    }

    override fun createBindings() {
    }

    override fun createServerProxy(isDedicated: Boolean): IEventProxy = SpongeServerProxy()

    override fun setupModCompat() {
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
