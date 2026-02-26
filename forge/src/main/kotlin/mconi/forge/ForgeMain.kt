package mconi.forge

import mconi.common.AbstractModInitializer
import mconi.common.LoaderType
import mconi.forge.wrappers.ForgeModChecker
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import java.lang.invoke.MethodHandles

/**
 * main entry point on Forge
 */
@Mod(AbstractModInitializer.MOD_ID)
class ForgeMain : AbstractModInitializer() {
    init {
        loaderType = LoaderType.Forge
        val modBusGroup = FMLJavaModLoadingContext.get().modBusGroup
        ForgeItems.register(modBusGroup)
        modBusGroup.register(MethodHandles.lookup(), object {
            @SubscribeEvent
            fun onClientSetup(@Suppress("unused") event: FMLClientSetupEvent) {
                onInitializeClient()
            }

            @SubscribeEvent
            fun onServerSetup(@Suppress("unused") event: FMLDedicatedServerSetupEvent) {
                onInitializeServer()
            }
        })
    }

    override fun createInitialBindings() {
        ForgeModChecker()
        // Forge static Instances here
    }

    override fun createClientProxy(): IEventProxy = ForgeClientProxy()

    override fun createServerProxy(isDedicated: Boolean): IEventProxy = ForgeServerProxy(isDedicated)

    override fun initializeModCompat() {
        // Forge mod menu integration if needed
    }
}
