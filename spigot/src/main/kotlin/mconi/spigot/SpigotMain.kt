package mconi.spigot

import mconi.common.AbstractModInitializer
import mconi.common.LoaderType

/**
 * Spigot entry.
 */
class SpigotMain : AbstractModInitializer() {
    init {
        loaderType = LoaderType.Spigot
        INSTANCE = this
    }

    override fun createInitialBindings() {
    }

    override fun createServerProxy(isDedicated: Boolean): IEventProxy = SpigotServerProxy()

    override fun initializeModCompat() {
    }

    override fun createClientProxy(): IEventProxy = NoopEventProxy

    companion object {
        @JvmStatic
        lateinit var INSTANCE: SpigotMain
            private set
    }

    private object NoopEventProxy : IEventProxy {
        override fun registerEvents() {
        }
    }
}
