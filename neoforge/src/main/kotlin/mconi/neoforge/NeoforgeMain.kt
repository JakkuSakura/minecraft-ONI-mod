package mconi.neoforge

import mconi.common.AbstractModBootstrap
import mconi.common.LoaderType
import mconi.common.config.OniConfigPaths
import mconi.common.element.OniElements
import mconi.neoforge.wrappers.NeoForgeModChecker
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.fml.loading.FMLPaths

/**
 * main entry point on NeoForge
 */
@Mod(AbstractModBootstrap.MOD_ID)
class NeoforgeMain(eventBus: IEventBus) : AbstractModBootstrap() {
    init {
        loaderType = LoaderType.NeoForge
        OniConfigPaths.init(FMLPaths.CONFIGDIR.get())
        NeoforgeBlocks.register(eventBus)
        NeoforgeBlockEntities.register(eventBus)
        NeoforgeItems.register(eventBus)
        NeoforgeMenus.register(eventBus)
        NeoforgeWorldgen.register(eventBus)
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            eventBus.addListener(NeoforgeClientModEvents::registerMenuScreens)
            eventBus.addListener(NeoforgeClientModEvents::registerPresetEditors)
        }
        eventBus.addListener { _: FMLClientSetupEvent -> onSetupClient() }
        eventBus.addListener { _: FMLDedicatedServerSetupEvent -> onSetupServer() }
        eventBus.addListener { _: FMLCommonSetupEvent ->
            NeoforgeBlockEntities.bindTypes()
            NeoforgeMenus.bindTypes()
            OniElements.refreshElementItems()
        }
    }

    override fun createBindings() {
        NeoForgeModChecker()
        // NeoForge static Instances here
    }

    override fun createServerProxy(isDedicated: Boolean): IEventProxy = NeoforgeServerProxy(isDedicated)

    override fun createClientProxy(): IEventProxy = NeoforgeClientProxy()

    override fun setupModCompat() {
        // config screen hookup if needed
    }
}
