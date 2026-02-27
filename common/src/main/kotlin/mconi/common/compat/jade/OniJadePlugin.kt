package mconi.common.compat.jade

import mconi.common.AbstractModBootstrap
import mconi.common.block.ConstructionSiteBlock
import mconi.common.block.entity.ConstructionSiteBlockEntity
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
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
        registration.registerBlockDataProvider(OniJadeDataProvider, ConstructionSiteBlock::class.java)
    }

    override fun registerClient(registration: IWailaClientRegistration) {
        registration.registerBlockComponent(OniJadeComponentProvider, ConstructionSiteBlock::class.java)
    }

    companion object {
        val UID: Identifier = requireNotNull(
            Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:oni_systems")
        ) { "Invalid Jade UID" }
    }
}

object OniJadeDataProvider : StreamServerDataProvider<BlockAccessor, String> {
    override fun streamData(accessor: BlockAccessor): String? {
        val lines = ArrayList<String>()
        val constructionSite = accessor.blockEntity as? ConstructionSiteBlockEntity
            ?: return null
        constructionSite.appendJadeLines(lines)
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
