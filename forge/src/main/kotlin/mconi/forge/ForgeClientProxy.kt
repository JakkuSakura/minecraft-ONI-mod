package mconi.forge

import com.mojang.brigadier.CommandDispatcher
import mconi.common.AbstractModBootstrap
import mconi.common.client.screen.BlueprintBookScreen
import mconi.common.menu.OniMenuTypes
import net.minecraft.commands.CommandSourceStack
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraftforge.client.event.RegisterClientCommandsEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import org.apache.logging.log4j.Logger

/**
 * This handles all events sent to the client
 */
class ForgeClientProxy : AbstractModBootstrap.IEventProxy {
    override fun registerEvents() {
        LOGGER.info("Registering Forge Client Events")
        MinecraftForge.EVENT_BUS.register(this)
        MenuScreens.register(OniMenuTypes.BLUEPRINT_BOOK) { menu, inventory, title ->
            BlueprintBookScreen(menu, inventory, title)
        }
        // Forge Client Events here
    }

    @SubscribeEvent
    fun registerClientCommands(event: RegisterClientCommandsEvent) {
        @Suppress("UNCHECKED_CAST")
        AbstractModBootstrap.registerClientCommands(event.dispatcher as CommandDispatcher<CommandSourceStack>)
    }


    companion object {
        private val LOGGER: Logger = AbstractModBootstrap.LOGGER
    }
}
