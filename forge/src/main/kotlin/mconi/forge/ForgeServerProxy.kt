package mconi.forge

import com.mojang.brigadier.CommandDispatcher
import mconi.common.AbstractModInitializer
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import org.apache.logging.log4j.Logger

/**
 * This handles all events sent to the server
 */
class ForgeServerProxy(private val isDedicated: Boolean) : AbstractModInitializer.IEventProxy {
    override fun registerEvents() {
        LOGGER.info("Registering Forge Server Events")
        MinecraftForge.EVENT_BUS.register(this)
        // Forge Server Events here
    }

    @SubscribeEvent
    fun registerCommands(event: RegisterCommandsEvent) {
        @Suppress("UNCHECKED_CAST")
        AbstractModInitializer.registerServerCommands(
            event.dispatcher as CommandDispatcher<CommandSourceStack>,
            event.commandSelection == Commands.CommandSelection.ALL
                    || event.commandSelection == Commands.CommandSelection.DEDICATED
        )
    }

    companion object {
        private val LOGGER: Logger = AbstractModInitializer.LOGGER
    }
}
