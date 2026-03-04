package conservecraft.forge

import conservecraft.common.AbstractModBootstrap
import conservecraft.common.LoaderType
import conservecraft.common.config.OniConfigPaths
import conservecraft.common.client.worldgen.OniWorldgenPresetEditors
import conservecraft.common.element.OniElements
import conservecraft.forge.wrappers.ForgeModChecker
import net.minecraftforge.client.event.RegisterPresetEditorsEvent
import net.minecraftforge.fml.loading.FMLPaths
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
        OniConfigPaths.init(FMLPaths.CONFIGDIR.get())
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
                OniElements.refreshElementItems()
            }

            @SubscribeEvent
            fun onRegisterPresetEditors(event: RegisterPresetEditorsEvent) {
                OniWorldgenPresetEditors.registerEditors(event::register)
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
