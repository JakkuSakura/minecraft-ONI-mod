package mconi.common.client

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext

object OniClientScreens {
    @Volatile private var worldgenConfigFactory: ((Screen?) -> Screen)? = null
    @Volatile private var worldgenPresetEditorFactory: ((CreateWorldScreen, WorldCreationContext) -> Screen)? = null

    @JvmStatic
    fun registerWorldgenConfigScreen(factory: (Screen?) -> Screen) {
        worldgenConfigFactory = factory
    }

    @JvmStatic
    fun registerWorldgenPresetEditor(factory: (CreateWorldScreen, WorldCreationContext) -> Screen) {
        worldgenPresetEditorFactory = factory
    }

    @JvmStatic
    fun createPresetEditorScreen(createWorld: CreateWorldScreen, context: WorldCreationContext): Screen? {
        val factory = worldgenPresetEditorFactory ?: return null
        return factory(createWorld, context)
    }

    @JvmStatic
    fun openWorldgenConfigScreen() {
        val factory = worldgenConfigFactory ?: return
        val client = Minecraft.getInstance()
        val parent = client.screen
        client.setScreen(factory(parent))
    }
}
