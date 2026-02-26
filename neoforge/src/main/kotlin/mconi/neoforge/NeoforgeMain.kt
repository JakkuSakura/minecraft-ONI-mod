package mconi.neoforge

import mconi.common.AbstractModInitializer
import mconi.common.LoaderType
import mconi.neoforge.wrappers.NeoForgeModChecker
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent

/**
 * main entry point on NeoForge
 */
@Mod(AbstractModInitializer.MOD_ID)
class NeoforgeMain(eventBus: IEventBus) : AbstractModInitializer() {
    init {
        loaderType = LoaderType.NeoForge
        NeoforgeBlocks.register(eventBus)
        NeoforgeItems.register(eventBus)
        NeoforgeWorldgen.register(eventBus)
        eventBus.addListener { _: FMLClientSetupEvent -> onInitializeClient() }
        eventBus.addListener { _: FMLDedicatedServerSetupEvent -> onInitializeServer() }
    }

    override fun createInitialBindings() {
        NeoForgeModChecker()
        // NeoForge static Instances here
    }

    override fun createServerProxy(isDedicated: Boolean): IEventProxy = NeoforgeServerProxy(isDedicated)

    override fun createClientProxy(): IEventProxy = NeoforgeClientProxy()

    override fun initializeModCompat() {
        // config screen hookup if needed
    }
}
