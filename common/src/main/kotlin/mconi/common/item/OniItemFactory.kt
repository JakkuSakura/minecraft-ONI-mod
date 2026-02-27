package mconi.common.item

import mconi.common.AbstractModInitializer
import mconi.common.element.OniElements
import mconi.common.sim.OniDecompiledCatalog
import mconi.common.sim.OniDecompiledSource
import mconi.common.sim.model.SystemLens
import net.minecraft.world.item.Item
import java.util.Collections
import java.util.EnumMap

object OniItemFactory {
    const val BOTTLED_OXYGEN = "bottled_oxygen"
    const val BOTTLED_CO2 = "bottled_co2"
    const val BOTTLED_HYDROGEN = "bottled_hydrogen"
    const val BOTTLED_WATER = "bottled_water"
    const val BOTTLED_POLLUTED_WATER = "bottled_polluted_water"
    const val BOTTLED_CRUDE_OIL = "bottled_crude_oil"
    const val BOTTLED_LAVA = "bottled_lava"
    const val BLUEPRINT_BOOK = "blueprint_book"
    const val BLUEPRINT = "blueprint"
    const val ELEMENT_REGOLITH = "element_regolith"
    const val ELEMENT_SEDIMENTARY_ROCK = "element_sedimentary_rock"
    const val ELEMENT_IGNEOUS_ROCK = "element_igneous_rock"
    const val ELEMENT_GRANITE = "element_granite"
    const val ELEMENT_ABYSSALITE = "element_abyssalite"
    const val ELEMENT_ALGAE = "element_algae"
    const val ELEMENT_POLLUTED_DIRT = "element_polluted_dirt"
    const val ELEMENT_METAL_ORE = "element_metal_ore"
    const val ELEMENT_REFINED_METAL = "element_refined_metal"
    const val POWER_STATION_TOOLS = "power_station_tools"

    @JvmField
    val ELEMENTS = listOf(
        ELEMENT_REGOLITH,
        ELEMENT_SEDIMENTARY_ROCK,
        ELEMENT_IGNEOUS_ROCK,
        ELEMENT_GRANITE,
        ELEMENT_ABYSSALITE,
        ELEMENT_ALGAE,
        ELEMENT_POLLUTED_DIRT,
        ELEMENT_METAL_ORE,
        ELEMENT_REFINED_METAL
    )

    @JvmField
    val ALL = listOf(
        BOTTLED_OXYGEN,
        BOTTLED_CO2,
        BOTTLED_HYDROGEN,
        BOTTLED_WATER,
        BOTTLED_POLLUTED_WATER,
        BOTTLED_CRUDE_OIL,
        BOTTLED_LAVA,
        BLUEPRINT_BOOK,
        BLUEPRINT,
        ELEMENT_REGOLITH,
        ELEMENT_SEDIMENTARY_ROCK,
        ELEMENT_IGNEOUS_ROCK,
        ELEMENT_GRANITE,
        ELEMENT_ABYSSALITE,
        ELEMENT_ALGAE,
        ELEMENT_POLLUTED_DIRT,
        ELEMENT_METAL_ORE,
        ELEMENT_REFINED_METAL,
        POWER_STATION_TOOLS
    )

    private val itemSuppliers: MutableMap<String, () -> Item> = LinkedHashMap()
    private val glassesItemPaths: EnumMap<SystemLens, String> = EnumMap(SystemLens::class.java)
    private val coreSpecs: List<OniItemSpec>
    private val decompiledSpecs: List<OniItemSpec>

    init {
        glassesItemPaths[SystemLens.ATMOSPHERE] = "atmosphere_glasses"
        glassesItemPaths[SystemLens.FLUID] = "fluid_glasses"
        glassesItemPaths[SystemLens.THERMAL] = "thermal_glasses"
        glassesItemPaths[SystemLens.OXYGEN] = "oxygen_glasses"
        glassesItemPaths[SystemLens.POWER] = "power_glasses"
        glassesItemPaths[SystemLens.STRESS] = "stress_glasses"
        glassesItemPaths[SystemLens.RESEARCH] = "research_glasses"
        glassesItemPaths[SystemLens.CONSTRUCTION] = "construction_glasses"

        coreSpecs = buildSpecs {
            source(OniItemSource.CORE)
            item(BOTTLED_OXYGEN) {
                registryId(modId(BOTTLED_OXYGEN))
                massKg(1.0)
                temperatureK(295.0)
            }
            item(BOTTLED_CO2) {
                registryId(modId(BOTTLED_CO2))
                massKg(1.0)
                temperatureK(295.0)
            }
            item(BOTTLED_HYDROGEN) {
                registryId(modId(BOTTLED_HYDROGEN))
                massKg(1.0)
                temperatureK(295.0)
            }
            for (spec in OniElements.LIQUID_SPECS) {
                item(spec.bottledItemId) {
                    registryId(modId(spec.bottledItemId))
                    massKg(spec.defaultMassKg)
                    temperatureK(spec.defaultTemperatureK)
                }
            }
            item(BLUEPRINT_BOOK) {
                registryId(modId(BLUEPRINT_BOOK))
            }
            item(BLUEPRINT) {
                registryId(modId(BLUEPRINT))
            }
            item(ELEMENT_REGOLITH) {
                registryId(modId(ELEMENT_REGOLITH))
            }
            item(ELEMENT_SEDIMENTARY_ROCK) {
                registryId(modId(ELEMENT_SEDIMENTARY_ROCK))
            }
            item(ELEMENT_IGNEOUS_ROCK) {
                registryId(modId(ELEMENT_IGNEOUS_ROCK))
            }
            item(ELEMENT_GRANITE) {
                registryId(modId(ELEMENT_GRANITE))
            }
            item(ELEMENT_ABYSSALITE) {
                registryId(modId(ELEMENT_ABYSSALITE))
            }
            item(ELEMENT_ALGAE) {
                registryId(modId(ELEMENT_ALGAE))
            }
            item(ELEMENT_POLLUTED_DIRT) {
                registryId(modId(ELEMENT_POLLUTED_DIRT))
            }
            item(ELEMENT_METAL_ORE) {
                registryId(modId(ELEMENT_METAL_ORE))
            }
            item(ELEMENT_REFINED_METAL) {
                registryId(modId(ELEMENT_REFINED_METAL))
            }
            item(POWER_STATION_TOOLS) {
                registryId(modId(POWER_STATION_TOOLS))
            }
        }

        decompiledSpecs = OniDecompiledCatalog.entries.map { entry ->
            OniItemSpec(
                key = entry.key,
                registryId = null,
                source = when (entry.source) {
                    OniDecompiledSource.STRINGS_ITEMS -> OniItemSource.DECOMPILED_STRINGS_ITEMS
                    OniDecompiledSource.PREFAB_ID -> OniItemSource.DECOMPILED_PREFAB
                },
                properties = OniItemProperties()
            )
        }
    }

    fun registerItem(idPath: String, supplier: () -> Item) {
        val fullId = normalizeId(idPath)
        itemSuppliers[fullId] = supplier
    }

    fun glassesItemPaths(): Map<SystemLens, String> {
        return Collections.unmodifiableMap(glassesItemPaths)
    }

    fun pathForLens(lens: SystemLens): String? {
        return glassesItemPaths[lens]
    }

    fun createGlassesItem(lens: SystemLens): Item {
        return createGlassesItem(lens, Item.Properties().stacksTo(1))
    }

    fun createGlassesItem(lens: SystemLens, properties: Item.Properties): Item {
        return SystemGlassesItem(properties, lens)
    }

    fun itemById(id: String): Item? {
        val fullId = normalizeId(id)
        return itemSuppliers[fullId]?.invoke()
    }

    fun specs(): List<OniItemSpec> = coreSpecs + decompiledSpecs

    private fun normalizeId(id: String): String {
        return if (id.contains(":")) {
            id
        } else {
            "${AbstractModInitializer.MOD_ID}:$id"
        }
    }

    private fun modId(path: String): String {
        return "${AbstractModInitializer.MOD_ID}:$path"
    }
}

enum class OniItemSource {
    CORE,
    DECOMPILED_STRINGS_ITEMS,
    DECOMPILED_PREFAB
}

data class OniItemProperties(
    val massKg: Double? = null,
    val temperatureK: Double? = null,
    val overheatTempK: Double? = null,
    val meltingPointK: Double? = null,
    val buildMaterialTags: List<String> = emptyList(),
    val elementRequirement: String? = null,
    val powerW: Double? = null,
    val notes: String? = null,
    val extras: Map<String, String> = emptyMap()
)

data class OniItemSpec(
    val key: String,
    val registryId: String?,
    val source: OniItemSource,
    val properties: OniItemProperties
)

private class OniItemSpecDsl(private val key: String, private val source: OniItemSource) {
    private var registryId: String? = null
    private var massKg: Double? = null
    private var temperatureK: Double? = null
    private var overheatTempK: Double? = null
    private var meltingPointK: Double? = null
    private var buildMaterialTags: List<String> = emptyList()
    private var elementRequirement: String? = null
    private var powerW: Double? = null
    private var notes: String? = null
    private val extras: MutableMap<String, String> = LinkedHashMap()

    fun registryId(id: String) {
        registryId = id
    }

    fun massKg(value: Double) {
        massKg = value
    }

    fun temperatureK(value: Double) {
        temperatureK = value
    }

    fun overheatTempK(value: Double) {
        overheatTempK = value
    }

    fun meltingPointK(value: Double) {
        meltingPointK = value
    }

    fun buildMaterials(vararg tags: String) {
        buildMaterialTags = tags.toList()
    }

    fun elementRequirement(value: String) {
        elementRequirement = value
    }

    fun powerW(value: Double) {
        powerW = value
    }

    fun note(value: String) {
        notes = value
    }

    fun extra(key: String, value: String) {
        extras[key] = value
    }

    fun build(): OniItemSpec {
        return OniItemSpec(
            key = key,
            registryId = registryId,
            source = source,
            properties = OniItemProperties(
                massKg = massKg,
                temperatureK = temperatureK,
                overheatTempK = overheatTempK,
                meltingPointK = meltingPointK,
                buildMaterialTags = buildMaterialTags,
                elementRequirement = elementRequirement,
                powerW = powerW,
                notes = notes,
                extras = extras.toMap()
            )
        )
    }
}

private class OniItemSpecsBuilder {
    private val entries: MutableList<OniItemSpec> = ArrayList()
    private var source: OniItemSource = OniItemSource.CORE

    fun source(value: OniItemSource) {
        source = value
    }

    fun item(key: String, block: OniItemSpecDsl.() -> Unit = {}) {
        val dsl = OniItemSpecDsl(key, source)
        dsl.block()
        entries.add(dsl.build())
    }

    fun build(): List<OniItemSpec> = entries.toList()
}

private fun buildSpecs(block: OniItemSpecsBuilder.() -> Unit): List<OniItemSpec> {
    val builder = OniItemSpecsBuilder()
    builder.block()
    return builder.build()
}
