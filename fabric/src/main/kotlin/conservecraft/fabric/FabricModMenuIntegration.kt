package conservecraft.fabric

import com.terraformersmc.modmenu.api.ModMenuApi
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import net.minecraft.client.gui.screens.Screen

class FabricModMenuIntegration : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<Screen> =
        ConfigScreenFactory { parent -> FabricWorldgenConfigScreen.create(parent) }
}
