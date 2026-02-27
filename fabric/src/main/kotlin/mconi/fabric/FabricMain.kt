package mconi.fabric

import mconi.common.AbstractModBootstrap
import mconi.common.LoaderType
import mconi.fabric.wrappers.FabricModChecker
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer

/**
 * main entry point on Fabric
 */
class FabricMain : AbstractModBootstrap(), ClientModInitializer, DedicatedServerModInitializer {
    init {
        loaderType = LoaderType.Fabric
    }

    override fun onInitializeClient() {
        onSetupClient()
    }

    override fun onInitializeServer() {
        onSetupServer()
    }

    override fun createBindings() {
        FabricModChecker()
        FabricBlocks.register()
        FabricBlockEntities.register()
        FabricItems.register()
        FabricMenus.register()
        FabricWorldgen.register()
        // Fabric static Instances here
    }

    override fun createClientProxy(): IEventProxy = FabricClientProxy()

    override fun createServerProxy(isDedicated: Boolean): IEventProxy = FabricServerProxy(isDedicated)

    override fun setupModCompat() {
        // mod compatibility setup here
    }
}
