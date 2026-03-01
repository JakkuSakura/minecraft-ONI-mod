package mconi.common.client

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen

object OniClientScreens {
    @Volatile private var worldgenConfigFactory: ((Screen?) -> Screen)? = null

    @JvmStatic
    fun registerWorldgenConfigScreen(factory: (Screen?) -> Screen) {
        worldgenConfigFactory = factory
    }

    @JvmStatic
    fun openWorldgenConfigScreen() {
        val factory = worldgenConfigFactory ?: return
        val client = Minecraft.getInstance()
        val parent = client.screen
        client.setScreen(factory(parent))
    }
}
