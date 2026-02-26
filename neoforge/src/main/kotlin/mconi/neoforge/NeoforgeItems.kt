package mconi.neoforge

import mconi.common.AbstractModInitializer
import mconi.common.item.BottledMatterItem
import mconi.common.item.OniBottledItems
import mconi.common.item.OniCreativeTabs
import mconi.common.item.OniItems
import mconi.common.sim.model.SystemLens
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object NeoforgeItems {
    private val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(AbstractModInitializer.MOD_ID)
    private val TABS: DeferredRegister<net.minecraft.world.item.CreativeModeTab> =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AbstractModInitializer.MOD_ID)

    init {
        for (lens in SystemLens.values()) {
            val path = OniItems.pathForLens(lens)
                ?: throw IllegalArgumentException("Missing item id path for lens: $lens")
            val id = Identifier.tryParse("${AbstractModInitializer.MOD_ID}:$path")
                ?: throw IllegalArgumentException("Invalid item id path: $path")
            val key = ResourceKey.create(Registries.ITEM, id)
            ITEMS.register(path, Supplier { OniItems.createGlassesItem(lens, Item.Properties().setId(key).stacksTo(1)) })
        }

        for (spec in OniBottledItems.SPECS) {
            val id = Identifier.tryParse("${AbstractModInitializer.MOD_ID}:${spec.id}")
                ?: throw IllegalArgumentException("Invalid item id path: ${spec.id}")
            val key = ResourceKey.create(Registries.ITEM, id)
            ITEMS.register(spec.id, Supplier {
                BottledMatterItem(
                    Item.Properties().setId(key),
                    spec.phase,
                    spec.massKg,
                    spec.temperatureK
                )
            })
        }

        TABS.register(OniCreativeTabs.TAB_PATH, Supplier {
            OniCreativeTabs.createTab(Supplier {
                val id = Identifier.tryParse("${AbstractModInitializer.MOD_ID}:oxygen_glasses")
                    ?: return@Supplier ItemStack.EMPTY
                BuiltInRegistries.ITEM.getOptional(id)
                    .map { ItemStack(it) }
                    .orElse(ItemStack.EMPTY)
            })
        })
    }

    fun register(eventBus: IEventBus) {
        ITEMS.register(eventBus)
        TABS.register(eventBus)
    }
}
