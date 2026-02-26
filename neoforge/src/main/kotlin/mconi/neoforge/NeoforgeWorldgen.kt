package mconi.neoforge

import com.mojang.serialization.MapCodec
import mconi.common.AbstractModInitializer
import mconi.common.world.OniChunkGenerator
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.chunk.ChunkGenerator
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object NeoforgeWorldgen {
    private val CHUNK_GENERATORS: DeferredRegister<MapCodec<out ChunkGenerator>> =
        DeferredRegister.create(Registries.CHUNK_GENERATOR, AbstractModInitializer.MOD_ID)

    init {
        CHUNK_GENERATORS.register("oni", Supplier { OniChunkGenerator.CODEC })
    }

    fun register(eventBus: IEventBus) {
        CHUNK_GENERATORS.register(eventBus)
    }
}
