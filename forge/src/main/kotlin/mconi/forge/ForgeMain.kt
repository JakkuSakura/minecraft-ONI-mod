package mconi.forge

import mconi.common.AbstractModBootstrap
import mconi.common.LoaderType
import mconi.forge.wrappers.ForgeModChecker
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import java.lang.invoke.MethodHandles

/**
 * main entry point on Forge
 */
@Mod(AbstractModBootstrap.MOD_ID)
class ForgeMain : AbstractModBootstrap() {
    init {
        loaderType = LoaderType.Forge
        val modBusGroup = FMLJavaModLoadingContext.get().modBusGroup
        ForgeBlocks.register(modBusGroup)
        ForgeBlockEntities.register(modBusGroup)
        ForgeItems.register(modBusGroup)
        ForgeMenus.register(modBusGroup)
        modBusGroup.register(MethodHandles.lookup(), object {
            @SubscribeEvent
            fun onClientSetup(@Suppress("unused") event: FMLClientSetupEvent) {
                onSetupClient()
            }

            @SubscribeEvent
            fun onServerSetup(@Suppress("unused") event: FMLDedicatedServerSetupEvent) {
                onSetupServer()
            }

            @SubscribeEvent
            fun onCommonSetup(@Suppress("unused") event: FMLCommonSetupEvent) {
                ForgeBlockEntities.bindTypes()
                ForgeMenus.bindTypes()
            }
        })
    }

    override fun createBindings() {
        ForgeModChecker()
        // Forge static Instances here
    }

    override fun createClientProxy(): IEventProxy = ForgeClientProxy()

    override fun createServerProxy(isDedicated: Boolean): IEventProxy = ForgeServerProxy(isDedicated)

    override fun setupModCompat() {
        // Forge mod menu integration if needed
    }
}
