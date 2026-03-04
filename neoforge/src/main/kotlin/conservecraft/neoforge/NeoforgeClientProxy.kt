package conservecraft.neoforge

import com.mojang.brigadier.CommandDispatcher
import conservecraft.common.AbstractModBootstrap
import conservecraft.common.client.OniClientScreens
import conservecraft.common.client.overlay.OniLensOverlayRenderer
import net.minecraft.commands.CommandSourceStack
import net.minecraft.client.Minecraft
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
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
        OniClientScreens.registerWorldgenPresetEditor { createWorld, _ ->
            NeoforgeWorldgenConfigScreen.create(createWorld)
        }
        // NeoForge Client Events here
    }

    @SubscribeEvent
    fun registerClientCommands(event: RegisterClientCommandsEvent) {
        @Suppress("UNCHECKED_CAST")
        AbstractModBootstrap.registerClientCommands(event.dispatcher as CommandDispatcher<CommandSourceStack>)
    }

    @SubscribeEvent
    fun renderLensOverlay(event: RenderLevelStageEvent.AfterTranslucentBlocks) {
        val client = Minecraft.getInstance()
        val level = client.level ?: return
        val player = client.player ?: return
        OniLensOverlayRenderer.render(
            event.poseStack,
            client.renderBuffers().bufferSource(),
            level,
            player
        )
    }



    companion object {
        private val LOGGER: Logger = AbstractModBootstrap.LOGGER
    }
}
