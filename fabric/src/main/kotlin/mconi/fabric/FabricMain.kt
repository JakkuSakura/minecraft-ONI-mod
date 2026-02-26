package mconi.fabric

import mconi.common.AbstractModInitializer
import mconi.common.LoaderType
import mconi.fabric.wrappers.FabricModChecker
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.DedicatedServerModInitializer

/**
 * main entry point on Fabric
 */
class FabricMain : AbstractModInitializer(), ClientModInitializer, DedicatedServerModInitializer {
    init {
        loaderType = LoaderType.Fabric
    }

    override fun createInitialBindings() {
        FabricModChecker()
        FabricBlocks.register()
        FabricItems.register()
        // Fabric static Instances here
    }

    override fun createClientProxy(): IEventProxy = FabricClientProxy()

    override fun createServerProxy(isDedicated: Boolean): IEventProxy = FabricServerProxy(isDedicated)

    override fun initializeModCompat() {
        // mod compatibility setup here
    }
}
