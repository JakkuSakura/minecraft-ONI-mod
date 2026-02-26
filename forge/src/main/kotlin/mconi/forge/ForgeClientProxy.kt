package mconi.forge

import com.mojang.brigadier.CommandDispatcher
import mconi.common.AbstractModInitializer
import net.minecraft.commands.CommandSourceStack
import net.minecraftforge.client.event.RegisterClientCommandsEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import org.apache.logging.log4j.Logger

/**
 * This handles all events sent to the client
 */
class ForgeClientProxy : AbstractModInitializer.IEventProxy {
    override fun registerEvents() {
        LOGGER.info("Registering Forge Client Events")
        MinecraftForge.EVENT_BUS.register(this)
        // Forge Client Events here
    }

    @SubscribeEvent
    fun registerClientCommands(event: RegisterClientCommandsEvent) {
        @Suppress("UNCHECKED_CAST")
        AbstractModInitializer.registerClientCommands(event.dispatcher as CommandDispatcher<CommandSourceStack>)
    }

    companion object {
        private val LOGGER: Logger = AbstractModInitializer.LOGGER
    }
}
