package mconi.fabric

import mconi.common.AbstractModBootstrap
import mconi.common.LoaderType
import mconi.common.config.OniConfigPaths
import mconi.fabric.wrappers.FabricModChecker
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.loader.api.FabricLoader

/**
 * main entry point on Fabric
 */
class FabricMain : AbstractModBootstrap(), ClientModInitializer, DedicatedServerModInitializer {
    init {
        loaderType = LoaderType.Fabric
        OniConfigPaths.init(FabricLoader.getInstance().configDir)
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
