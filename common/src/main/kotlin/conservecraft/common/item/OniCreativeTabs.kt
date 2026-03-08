package conservecraft.common.item

import conservecraft.common.AbstractModBootstrap
import conservecraft.common.block.OniBlockFactory
import conservecraft.common.sim.model.SystemLens
import net.minecraft.core.HolderGetter
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import java.util.function.Supplier

object OniCreativeTabs {
    const val TAB_PATH: String = "oni"

    @JvmField
    val TAB_KEY: ResourceKey<CreativeModeTab> = ResourceKey.create(
        Registries.CREATIVE_MODE_TAB,
        tabId()
    )

    private fun tabId(): Identifier {
        return Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:$TAB_PATH")
            ?: throw IllegalStateException("Invalid creative tab id: $TAB_PATH")
    }

    @JvmStatic
    fun createTab(iconItem: ItemLike): CreativeModeTab {
        return createTab(Supplier { ItemStack(iconItem) })
    }

    @JvmStatic
    fun createTab(iconSupplier: Supplier<ItemStack>): CreativeModeTab {
        return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.${AbstractModBootstrap.MOD_ID}.$TAB_PATH"))
            .icon(iconSupplier)
            .displayItems { params, output ->
                val itemLookup = params.holders().lookupOrThrow(Registries.ITEM)
                for (lens in SystemLens.values()) {
                    val path = OniItemFactory.pathForLens(lens) ?: continue
                    acceptIfPresent(output, itemLookup, path)
                }
                for (path in OniItemFactory.ALL) {
                    acceptIfPresent(output, itemLookup, path)
                }
                for (path in OniBlockFactory.SOLID_IDS) {
                    acceptIfPresent(output, itemLookup, path)
                }
            }
            .build()
    }

    private fun acceptIfPresent(
        output: CreativeModeTab.Output,
        itemLookup: HolderGetter<Item>,
        path: String,
    ) {
        val id = Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:$path") ?: return
        val itemKey = ResourceKey.create(Registries.ITEM, id)
        val itemHolder = itemLookup.get(itemKey).orElse(null) ?: return
        output.accept(ItemStack(itemHolder))
    }
}
