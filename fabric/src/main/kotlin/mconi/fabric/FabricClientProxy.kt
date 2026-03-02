package mconi.fabric

import com.mojang.brigadier.CommandDispatcher
import mconi.common.AbstractModBootstrap
import mconi.common.client.OniClientScreens
import mconi.common.client.overlay.OniLensOverlayRenderer
import mconi.common.client.worldgen.OniWorldgenPresetEditors
import mconi.common.client.screen.BlueprintBookScreen
import mconi.common.menu.OniMenuTypes
import mconi.mixins.fabric.client.PresetEditorAccessor
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.minecraft.client.Minecraft
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
        OniClientScreens.registerWorldgenPresetEditor { createWorld, _ ->
            FabricWorldgenConfigScreen.create(createWorld)
        }
        OniWorldgenPresetEditors.registerEditors { key, editor ->
            PresetEditorAccessor.`mconi$getEditors`()[key] = editor
        }

        WorldRenderEvents.END_MAIN.register { context ->
            val client = Minecraft.getInstance()
            val level = client.level ?: return@register
            val player = client.player ?: return@register
            OniLensOverlayRenderer.render(
                context.matrices(),
                context.consumers(),
                level,
                player
            )
        }
    }

    companion object {
        private val LOGGER: Logger = AbstractModBootstrap.LOGGER
    }
}
