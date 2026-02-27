package mconi.fabric

import mconi.common.AbstractModInitializer
import mconi.common.world.OniChunkGenerator
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier

object FabricWorldgen {
    fun register() {
        val id = Identifier.tryParse("${AbstractModInitializer.MOD_ID}:oni")
            ?: throw IllegalArgumentException("Invalid chunk generator id")
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, id, OniChunkGenerator.CODEC)
    }
}
