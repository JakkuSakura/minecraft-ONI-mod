package mconi.fabric

import com.mojang.brigadier.CommandDispatcher
import mconi.common.AbstractModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.commands.CommandSourceStack
import org.apache.logging.log4j.Logger

/**
 * This handles all events sent to the client
 */
@Environment(EnvType.CLIENT)
class FabricClientProxy : AbstractModInitializer.IEventProxy {
    override fun registerEvents() {
        LOGGER.info("Registering Fabric Client Events")

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            @Suppress("UNCHECKED_CAST")
            AbstractModInitializer.registerClientCommands(
                dispatcher as CommandDispatcher<CommandSourceStack>
            )
        }

        // register Fabric Client Events here
    }

    companion object {
        private val LOGGER: Logger = AbstractModInitializer.LOGGER
    }
}
