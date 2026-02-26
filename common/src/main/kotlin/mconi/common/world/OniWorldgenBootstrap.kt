package mconi.common.world

import mconi.common.AbstractModInitializer
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries

object OniWorldgenBootstrap {
    private var registered = false

    @JvmStatic
    fun register() {
        if (registered) {
            return
        }
        registered = true
        val id = "${AbstractModInitializer.MOD_ID}:oni"
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, id, OniChunkGenerator.CODEC)
    }
}
