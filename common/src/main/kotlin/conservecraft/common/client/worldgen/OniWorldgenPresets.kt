package conservecraft.common.client.worldgen

import conservecraft.common.AbstractModBootstrap
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.levelgen.presets.WorldPreset

object OniWorldgenPresets {
    val PRESET_2D: ResourceKey<WorldPreset> = key("oni_2d")
    val PRESET_3D: ResourceKey<WorldPreset> = key("oni_3d")
    val ALL: List<ResourceKey<WorldPreset>> = listOf(PRESET_2D, PRESET_3D)

    private fun key(path: String): ResourceKey<WorldPreset> {
        val id = Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:$path")
            ?: throw IllegalArgumentException("Invalid world preset id: $path")
        return ResourceKey.create(Registries.WORLD_PRESET, id)
    }
}
