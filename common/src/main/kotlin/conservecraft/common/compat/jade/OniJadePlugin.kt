package conservecraft.common.compat.jade

import conservecraft.common.AbstractModBootstrap
import conservecraft.common.element.ElementContents
import conservecraft.common.world.OniElementAccess
import conservecraft.common.world.OniMatterAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
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
            Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:oni_systems")
        ) { "Invalid Jade UID" }
    }
}

object OniJadeDataProvider : StreamServerDataProvider<BlockAccessor, String> {
    override fun streamData(accessor: BlockAccessor): String? {
        val lines = ArrayList<String>()
        val level = accessor.level as? ServerLevel ?: return null
        val pos = accessor.position
        val elements: List<ElementContents> = OniElementAccess.elements(level, pos)
        val totalMass = elements.sumOf { it.mass }

        lines.add("Elements:")
        if (elements.isEmpty()) {
            lines.add("- <none>")
        } else {
            for (element in elements) {
                lines.add("element|${element.elementId}|${element.mass}")
            }
        }

        lines.add("Element mass: $totalMass")

        if (elements.isNotEmpty()) {
            val tempK = OniElementAccess.averageTemperatureK(level, pos)
            val tempC = tempK - 273.15
            lines.add("Temperature: %.2f K (%.2f C)".format(tempK, tempC))
            val entity = OniMatterAccess.matterEntity(level, pos)
            if (entity != null) {
                lines.add("Matter mass: %.3f".format(entity.mass()))
            }
        } else {
            lines.add("Temperature: <unknown>")
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
                if (line.startsWith("element|")) {
                    val parts = line.split('|')
                    if (parts.size == 3) {
                        val itemId = parts[1]
                        val amount = parts[2]
                        val identifier = Identifier.tryParse(itemId)
                        val item = if (identifier != null) {
                            BuiltInRegistries.ITEM.getOptional(identifier).orElse(null)
                        } else {
                            null
                        }
                        val name = if (item != null) ItemStack(item).hoverName.string else itemId
                        tooltip.add(Component.literal("- $name $amount"))
                        continue
                    }
                }
                tooltip.add(Component.literal(line))
            }
        }
    }

    override fun getUid(): Identifier = OniJadePlugin.UID
}
