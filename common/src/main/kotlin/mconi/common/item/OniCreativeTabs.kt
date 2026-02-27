package mconi.common.item

import mconi.common.AbstractModInitializer
import mconi.common.block.OniBlockFactory
import mconi.common.item.OniItemFactory
import mconi.common.sim.model.SystemLens
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import java.util.function.Supplier

/**
 * Shared creative tab definition for ONI items.
 */
object OniCreativeTabs {
    const val TAB_PATH: String = "oni"

    @JvmField
    val TAB_KEY: ResourceKey<CreativeModeTab> = ResourceKey.create(
        Registries.CREATIVE_MODE_TAB,
        tabId()
    )

    private fun tabId(): Identifier {
        return Identifier.tryParse("${AbstractModInitializer.MOD_ID}:$TAB_PATH")
            ?: throw IllegalStateException("Invalid creative tab id: $TAB_PATH")
    }

    @JvmStatic
    fun createTab(iconItem: ItemLike): CreativeModeTab {
        return createTab(Supplier { ItemStack(iconItem) })
    }

    @JvmStatic
    fun createTab(iconSupplier: Supplier<ItemStack>): CreativeModeTab {
        return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.${AbstractModInitializer.MOD_ID}.$TAB_PATH"))
            .icon(iconSupplier)
            .displayItems { params, output ->
                for (lens in SystemLens.values()) {
                    val path = OniItemFactory.pathForLens(lens) ?: continue
                    val id = Identifier.tryParse("${AbstractModInitializer.MOD_ID}:$path") ?: continue
                    val itemKey = ResourceKey.create(Registries.ITEM, id)
                    output.accept(ItemStack(params.holders().lookupOrThrow(Registries.ITEM).getOrThrow(itemKey)))
                }
                for (path in OniItemFactory.ALL) {
                    val id = Identifier.tryParse("${AbstractModInitializer.MOD_ID}:$path") ?: continue
                    val itemKey = ResourceKey.create(Registries.ITEM, id)
                    output.accept(ItemStack(params.holders().lookupOrThrow(Registries.ITEM).getOrThrow(itemKey)))
                }
                for (block in OniBlockFactory.SOLIDS) {
                    val path = OniBlockFactory.idOf(block) ?: continue
                    val id = Identifier.tryParse("${AbstractModInitializer.MOD_ID}:$path") ?: continue
                    val itemKey = ResourceKey.create(Registries.ITEM, id)
                    output.accept(ItemStack(params.holders().lookupOrThrow(Registries.ITEM).getOrThrow(itemKey)))
                }
            }
            .build()
    }
}
