package conservecraft.forge

import com.mojang.brigadier.CommandDispatcher
import conservecraft.common.AbstractModBootstrap
import conservecraft.common.client.OniClientScreens
import conservecraft.common.client.screen.AdvancedCraftingTableScreen
import conservecraft.common.client.screen.RecyclingTableScreen
import conservecraft.common.menu.OniMenuTypes
import conservecraft.common.client.overlay.OniLensOverlayRenderer
import net.minecraft.commands.CommandSourceStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.resources.Identifier
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
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
            event.addPass(Identifier.fromNamespaceAndPath("conservecraft", "lens_overlay"), object : FramePassManager.PassDefinition {
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
        MenuScreens.register(OniMenuTypes.advancedCraftingTable(), object : MenuScreens.ScreenConstructor<conservecraft.common.menu.AdvancedCraftingTableMenu, AdvancedCraftingTableScreen> {
            override fun create(menu: conservecraft.common.menu.AdvancedCraftingTableMenu, inventory: Inventory, title: Component): AdvancedCraftingTableScreen {
                return AdvancedCraftingTableScreen(menu, inventory, title)
            }
        })
        MenuScreens.register(OniMenuTypes.recyclingTable(), object : MenuScreens.ScreenConstructor<conservecraft.common.menu.RecyclingTableMenu, RecyclingTableScreen> {
            override fun create(menu: conservecraft.common.menu.RecyclingTableMenu, inventory: Inventory, title: Component): RecyclingTableScreen {
                return RecyclingTableScreen(menu, inventory, title)
            }
        })
        OniClientScreens.registerWorldgenConfigScreen { parent ->
            ForgeWorldgenConfigScreen.create(parent)
        }
        OniClientScreens.registerWorldgenPresetEditor { createWorld, _ ->
            ForgeWorldgenConfigScreen.create(createWorld)
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
