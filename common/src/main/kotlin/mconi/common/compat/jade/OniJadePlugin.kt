package mconi.common.compat.jade

import mconi.common.AbstractModInitializer
import mconi.common.sim.OniServices
import mconi.common.sim.OniSystemInspector
import mconi.common.sim.model.SystemLens
import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.Block
import snownee.jade.api.BlockAccessor
import snownee.jade.api.IBlockComponentProvider
import snownee.jade.api.IWailaClientRegistration
import snownee.jade.api.IWailaCommonRegistration
import snownee.jade.api.IWailaPlugin
import snownee.jade.api.StreamServerDataProvider
import snownee.jade.api.WailaPlugin
import snownee.jade.api.config.IPluginConfig
import java.util.Optional

@WailaPlugin
class OniJadePlugin : IWailaPlugin {
    override fun register(registration: IWailaCommonRegistration) {
        registration.registerBlockDataProvider(OniJadeDataProvider, Block::class.java)
    }

    override fun registerClient(registration: IWailaClientRegistration) {
        registration.registerBlockComponent(OniJadeComponentProvider, Block::class.java)
    }

    companion object {
        val UID: Identifier = requireNotNull(
            Identifier.tryParse("${AbstractModInitializer.MOD_ID}:oni_systems")
        ) { "Invalid Jade UID" }
    }
}

object OniJadeDataProvider : StreamServerDataProvider<BlockAccessor, String> {
    override fun streamData(accessor: BlockAccessor): String? {
        val runtime = OniServices.simulationRuntime()
        val pos: BlockPos = accessor.position
        val cell = runtime.grid().getOrCreateCellAtBlock(
            pos.x,
            pos.y,
            pos.z,
            runtime.config().cellSize()
        )

        val lines = ArrayList<String>()
        lines.add("ONI Systems @ ${pos.x},${pos.y},${pos.z}")
        for (lens in SystemLens.values()) {
            lines.add("Lens: ${lens.name}")
            for (property in OniSystemInspector.inspect(runtime, lens, cell)) {
                lines.add("[${property.layer()}] ${property.key()}=${property.value()}")
            }
        }

        return lines.joinToString("\n")
    }

    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, String> {
        return ByteBufCodecs.STRING_UTF8.cast()
    }

    override fun getUid(): Identifier = OniJadePlugin.UID
}

object OniJadeComponentProvider : IBlockComponentProvider {
    override fun appendTooltip(
        tooltip: snownee.jade.api.ITooltip,
        accessor: BlockAccessor,
        config: IPluginConfig
    ) {
        val payload: Optional<String> = OniJadeDataProvider.decodeFromData(accessor)
        if (payload.isEmpty) {
            return
        }
        val text = payload.get()
        if (text.isBlank()) {
            return
        }
        for (line in text.split('\n')) {
            if (line.isNotBlank()) {
                tooltip.add(Component.literal(line))
            }
        }
    }

    override fun getUid(): Identifier = OniJadePlugin.UID
}
