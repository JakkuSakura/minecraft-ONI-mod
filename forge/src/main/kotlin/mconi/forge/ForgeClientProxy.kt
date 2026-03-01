package mconi.forge

import com.mojang.brigadier.CommandDispatcher
import mconi.common.AbstractModBootstrap
import mconi.common.client.OniClientScreens
import mconi.common.client.overlay.OniLensOverlayRenderer
import mconi.common.client.screen.BlueprintBookScreen
import mconi.common.menu.OniMenuTypes
import net.minecraft.commands.CommandSourceStack
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier
import net.minecraftforge.client.FramePassManager
import net.minecraftforge.client.event.AddFramePassEvent
import net.minecraftforge.client.event.RegisterClientCommandsEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.listener.SubscribeEvent
import org.apache.logging.log4j.Logger
import com.mojang.blaze3d.vertex.PoseStack

/**
 * This handles all events sent to the client
 */
class ForgeClientProxy : AbstractModBootstrap.IEventProxy {
    override fun registerEvents() {
        LOGGER.info("Registering Forge Client Events")
        MinecraftForge.EVENT_BUS.register(this)
        AddFramePassEvent.BUS.addListener { event ->
            event.addPass(Identifier.fromNamespaceAndPath("mconi", "lens_overlay"), object : FramePassManager.PassDefinition {
                override fun extracts(bundle: net.minecraft.client.renderer.LevelTargetBundle, pass: com.mojang.blaze3d.framegraph.FramePass) {
                    pass.readsAndWrites(bundle.main)
                }

                override fun executes(state: net.minecraft.client.renderer.state.LevelRenderState) {
                    val client = Minecraft.getInstance()
                    val level = client.level ?: return
                    val player = client.player ?: return
                    OniLensOverlayRenderer.render(
                        PoseStack(),
                        client.renderBuffers().bufferSource(),
                        level,
                        player
                    )
                }
            })
        }
        MenuScreens.register(OniMenuTypes.BLUEPRINT_BOOK) { menu, inventory, title ->
            BlueprintBookScreen(menu, inventory, title)
        }
        OniClientScreens.registerWorldgenConfigScreen { parent ->
            ForgeWorldgenConfigScreen.create(parent)
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
