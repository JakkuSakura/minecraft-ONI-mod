package mconi.neoforge

import mconi.common.AbstractModBootstrap
import mconi.common.item.OniItemFactory
import mconi.common.item.BlueprintBookItem
import mconi.common.item.BlueprintItem
import mconi.common.item.BottledMatterItem
import mconi.common.item.ElementItem
import mconi.common.item.OniBottledItems
import mconi.common.item.OniCreativeTabs
import mconi.common.sim.model.SystemLens
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object NeoforgeItems {
    private val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(AbstractModBootstrap.MOD_ID)
    private val TABS: DeferredRegister<net.minecraft.world.item.CreativeModeTab> =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AbstractModBootstrap.MOD_ID)
    private var oxygenLensSupplier: Supplier<Item>? = null

    init {
        for (lens in SystemLens.values()) {
            val path = OniItemFactory.pathForLens(lens)
                ?: throw IllegalArgumentException("Missing item id path for lens: $lens")
            val id = Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:$path")
                ?: throw IllegalArgumentException("Invalid item id path: $path")
            val key = ResourceKey.create(Registries.ITEM, id)
            val holder = ITEMS.register(path, Supplier {
                OniItemFactory.createGlassesItem(lens, Item.Properties().setId(key).stacksTo(1))
            })
            OniItemFactory.registerItem(path) { holder.get() }
            if (lens == SystemLens.GAS) {
                oxygenLensSupplier = Supplier { holder.get() }
            }
        }

        for (spec in OniBottledItems.SPECS) {
            val id = Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:${spec.id}")
                ?: throw IllegalArgumentException("Invalid item id path: ${spec.id}")
            val key = ResourceKey.create(Registries.ITEM, id)
            val holder = ITEMS.register(spec.id, Supplier {
                BottledMatterItem(
                    Item.Properties().setId(key),
                    spec.phase,
                    spec.massKg,
                    spec.temperatureK
                )
            })
            OniItemFactory.registerItem(spec.id) { holder.get() }
        }

        run {
            val id = Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:${OniItemFactory.BLUEPRINT_BOOK}")
                ?: throw IllegalArgumentException("Invalid item id path: ${OniItemFactory.BLUEPRINT_BOOK}")
            val key = ResourceKey.create(Registries.ITEM, id)
            val holder = ITEMS.register(OniItemFactory.BLUEPRINT_BOOK, Supplier {
                BlueprintBookItem(Item.Properties().setId(key).stacksTo(1))
            })
            OniItemFactory.registerItem(OniItemFactory.BLUEPRINT_BOOK) { holder.get() }
        }

        run {
            val id = Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:${OniItemFactory.BLUEPRINT}")
                ?: throw IllegalArgumentException("Invalid item id path: ${OniItemFactory.BLUEPRINT}")
            val key = ResourceKey.create(Registries.ITEM, id)
            val holder = ITEMS.register(OniItemFactory.BLUEPRINT, Supplier {
                BlueprintItem(Item.Properties().setId(key))
            })
            OniItemFactory.registerItem(OniItemFactory.BLUEPRINT) { holder.get() }
        }

        for (elementId in OniItemFactory.ELEMENTS) {
            val id = Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:$elementId")
                ?: throw IllegalArgumentException("Invalid item id path: $elementId")
            val key = ResourceKey.create(Registries.ITEM, id)
            val holder = ITEMS.register(elementId, Supplier {
                ElementItem(Item.Properties().setId(key))
            })
            OniItemFactory.registerItem(elementId) { holder.get() }
        }

        run {
            val id = Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:${OniItemFactory.POWER_STATION_TOOLS}")
                ?: throw IllegalArgumentException("Invalid item id path: ${OniItemFactory.POWER_STATION_TOOLS}")
            val key = ResourceKey.create(Registries.ITEM, id)
            val holder = ITEMS.register(OniItemFactory.POWER_STATION_TOOLS, Supplier {
                Item(Item.Properties().setId(key).stacksTo(1))
            })
            OniItemFactory.registerItem(OniItemFactory.POWER_STATION_TOOLS) { holder.get() }
        }

        TABS.register(OniCreativeTabs.TAB_PATH, Supplier {
            OniCreativeTabs.createTab(Supplier {
                oxygenLensSupplier?.get()?.let { ItemStack(it) } ?: ItemStack.EMPTY
            })
        })

    }

    fun register(eventBus: IEventBus) {
        ITEMS.register(eventBus)
        TABS.register(eventBus)
    }
}
