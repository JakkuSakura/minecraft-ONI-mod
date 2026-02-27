package mconi.spigot

import mconi.common.AbstractModBootstrap
import mconi.common.LoaderType

/**
 * Spigot entry.
 */
class SpigotMain : AbstractModBootstrap() {
    init {
        loaderType = LoaderType.Spigot
        INSTANCE = this
    }

    override fun createBindings() {
    }

    override fun createServerProxy(isDedicated: Boolean): IEventProxy = SpigotServerProxy()

    override fun setupModCompat() {
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
