package mconi.neoforge;

import com.mojang.serialization.MapCodec;
import mconi.common.AbstractModInitializer;
import mconi.common.world.OniChunkGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class NeoforgeWorldgen {
    private static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS =
        DeferredRegister.create(Registries.CHUNK_GENERATOR, AbstractModInitializer.MOD_ID);

    static {
        CHUNK_GENERATORS.register("oni", () -> OniChunkGenerator.CODEC);
    }

    private NeoforgeWorldgen() {
    }

    public static void register(IEventBus eventBus) {
        CHUNK_GENERATORS.register(eventBus);
    }
}
