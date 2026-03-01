package mconi.fabric

import com.mojang.brigadier.CommandDispatcher
import mconi.common.AbstractModBootstrap
import mconi.common.client.OniClientScreens
import mconi.common.client.screen.BlueprintBookScreen
import mconi.common.menu.OniMenuTypes
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.commands.CommandSourceStack
import org.apache.logging.log4j.Logger

/**
 * This handles all events sent to the client
 */
@Environment(EnvType.CLIENT)
class FabricClientProxy : AbstractModBootstrap.IEventProxy {
    override fun registerEvents() {
        LOGGER.info("Registering Fabric Client Events")

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            @Suppress("UNCHECKED_CAST")
            AbstractModBootstrap.registerClientCommands(
                dispatcher as CommandDispatcher<CommandSourceStack>
            )
        }

        MenuScreens.register(OniMenuTypes.BLUEPRINT_BOOK) { menu, inventory, title ->
            BlueprintBookScreen(menu, inventory, title)
        }

        OniClientScreens.registerWorldgenConfigScreen { parent ->
            FabricWorldgenConfigScreen.create(parent)
        }

        // register Fabric Client Events here
    }

    companion object {
        private val LOGGER: Logger = AbstractModBootstrap.LOGGER
    }
}
