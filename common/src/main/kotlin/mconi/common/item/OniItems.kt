package mconi.common.item

import mconi.common.sim.model.SystemLens
import net.minecraft.world.item.Item
import java.util.Collections
import java.util.EnumMap

/**
 * Shared item definitions used by all loaders.
 */
object OniItems {
    private val GLASSES_ITEM_PATHS: EnumMap<SystemLens, String> = EnumMap(SystemLens::class.java)

    init {
        GLASSES_ITEM_PATHS[SystemLens.ATMOSPHERE] = "atmosphere_glasses"
        GLASSES_ITEM_PATHS[SystemLens.FLUID] = "fluid_glasses"
        GLASSES_ITEM_PATHS[SystemLens.THERMAL] = "thermal_glasses"
        GLASSES_ITEM_PATHS[SystemLens.OXYGEN] = "oxygen_glasses"
        GLASSES_ITEM_PATHS[SystemLens.POWER] = "power_glasses"
        GLASSES_ITEM_PATHS[SystemLens.STRESS] = "stress_glasses"
        GLASSES_ITEM_PATHS[SystemLens.RESEARCH] = "research_glasses"
        GLASSES_ITEM_PATHS[SystemLens.CONSTRUCTION] = "construction_glasses"
    }

    @JvmStatic
    fun glassesItemPaths(): Map<SystemLens, String> {
        return Collections.unmodifiableMap(GLASSES_ITEM_PATHS)
    }

    @JvmStatic
    fun pathForLens(lens: SystemLens): String? {
        return GLASSES_ITEM_PATHS[lens]
    }

    @JvmStatic
    fun createGlassesItem(lens: SystemLens): Item {
        return createGlassesItem(lens, Item.Properties().stacksTo(1))
    }

    @JvmStatic
    fun createGlassesItem(lens: SystemLens, properties: Item.Properties): Item {
        return SystemGlassesItem(properties, lens)
    }
}
