package mconi.neoforge

import com.mojang.brigadier.CommandDispatcher
import mconi.common.AbstractModBootstrap
import mconi.common.client.OniClientScreens
import net.minecraft.commands.CommandSourceStack
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent
import net.neoforged.neoforge.common.NeoForge
import org.apache.logging.log4j.Logger

/**
 * This handles all events sent to the client
 */
class NeoforgeClientProxy : AbstractModBootstrap.IEventProxy {
    override fun registerEvents() {
        LOGGER.info("Registering NeoForge Client Events")
        NeoForge.EVENT_BUS.register(this)
        OniClientScreens.registerWorldgenConfigScreen { parent ->
            NeoforgeWorldgenConfigScreen.create(parent)
        }
        // NeoForge Client Events here
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
