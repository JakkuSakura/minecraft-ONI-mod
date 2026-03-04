package conservecraft.common.client.worldgen

import conservecraft.common.client.OniClientScreens
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen
import net.minecraft.client.gui.screens.worldselection.PresetEditor
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.presets.WorldPreset
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext

object OniWorldgenPresetEditors {
    fun registerEditors(register: (ResourceKey<WorldPreset>, PresetEditor) -> Unit) {
        val editor = PresetEditor { createWorld: CreateWorldScreen, context: WorldCreationContext ->
            OniClientScreens.createPresetEditorScreen(createWorld, context) ?: createWorld
        }
        for (preset in OniWorldgenPresets.ALL) {
            register(preset, editor)
        }
    }
}
