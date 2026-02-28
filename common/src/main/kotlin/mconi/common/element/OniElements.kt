package mconi.common.element

import mconi.common.AbstractModBootstrap
import mconi.common.item.OniItemFactory
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import java.util.Collections

object OniElements {
    val REGISTRY: ElementRegistry = ElementRegistry()
    private val elementItems: MutableSet<Item> = LinkedHashSet()

    const val LIQUID_NONE: String = "none"
    const val LIQUID_WATER: String = "water"
    const val LIQUID_POLLUTED_WATER: String = "polluted_water"
    const val LIQUID_CRUDE_OIL: String = "crude_oil"
    const val LIQUID_LAVA: String = "lava"

    data class LiquidSpec(
        val id: String,
        val elementId: String,
        val bottledItemId: String,
        val defaultMass: Double,
        val defaultTemperatureK: Double,
        val thermalConductivityWmK: Double? = null,
        val massPerItem: Double? = null,
        val clotterThresholdItems: Int? = null,
        val transforms: List<ElementTransform> = emptyList()
    ) {
        fun bottledMass(): Double = defaultMass

        fun bottledTemperatureK(): Double = defaultTemperatureK

        fun itemMass(): Double = massPerItem ?: defaultMass
    }

    data class GasSpec(
        val id: String,
        val elementId: String,
        val symbol: String,
        val specificHeatCapacity: Double,
        val thermalConductivity: Double,
        val solidSurfaceAreaMultiplier: Double,
        val liquidSurfaceAreaMultiplier: Double,
        val gasSurfaceAreaMultiplier: Double,
        val flow: Double,
        val lowTemp: Double,
        val lowTempTransitionTarget: String,
        val defaultTemperature: Double,
        val defaultMass: Double,
        val molarMass: Double,
        val toxicity: Double,
        val lightAbsorptionFactor: Double,
        val radiationAbsorptionFactor: Double,
        val radiationPer1000Mass: Double,
        val materialCategory: String,
        val tags: List<String>,
        val isDisabled: Boolean,
        val state: ElementPhase,
        val localizationId: String,
        val dlcId: String,
    )

    @JvmField
    val GAS_CARBON_DIOXIDE: GasSpec = GasSpec(
        id = "carbon_dioxide",
        elementId = "CarbonDioxide",
        symbol = "CO2",
        specificHeatCapacity = 0.846,
        thermalConductivity = 0.0146,
        solidSurfaceAreaMultiplier = 25.0,
        liquidSurfaceAreaMultiplier = 1.0,
        gasSurfaceAreaMultiplier = 1.0,
        flow = 0.1,
        lowTemp = 225.0,
        lowTempTransitionTarget = "LiquidCarbonDioxide",
        defaultTemperature = 300.0,
        defaultMass = 139.0,
        molarMass = 44.01,
        toxicity = 0.0001,
        lightAbsorptionFactor = 0.1,
        radiationAbsorptionFactor = 0.08,
        radiationPer1000Mass = 0.0,
        materialCategory = "Unbreathable",
        tags = emptyList(),
        isDisabled = false,
        state = ElementPhase.GAS,
        localizationId = "STRINGS.ELEMENTS.CARBONDIOXIDE.NAME",
        dlcId = ""
    )

    @JvmField
    val GAS_HYDROGEN: GasSpec = GasSpec(
        id = "hydrogen",
        elementId = "Hydrogen",
        symbol = "H2",
        specificHeatCapacity = 2.4,
        thermalConductivity = 0.168,
        solidSurfaceAreaMultiplier = 25.0,
        liquidSurfaceAreaMultiplier = 1.0,
        gasSurfaceAreaMultiplier = 1.0,
        flow = 0.1,
        lowTemp = 21.0,
        lowTempTransitionTarget = "LiquidHydrogen",
        defaultTemperature = 300.0,
        defaultMass = 7.0,
        molarMass = 1.00794,
        toxicity = 0.0,
        lightAbsorptionFactor = 0.1,
        radiationAbsorptionFactor = 0.09,
        radiationPer1000Mass = 0.0,
        materialCategory = "Unbreathable",
        tags = emptyList(),
        isDisabled = false,
        state = ElementPhase.GAS,
        localizationId = "STRINGS.ELEMENTS.HYDROGEN.NAME",
        dlcId = ""
    )

    @JvmField
    val GAS_OXYGEN: GasSpec = GasSpec(
        id = "oxygen",
        elementId = "Oxygen",
        symbol = "O2",
        specificHeatCapacity = 1.005,
        thermalConductivity = 0.024,
        solidSurfaceAreaMultiplier = 25.0,
        liquidSurfaceAreaMultiplier = 1.0,
        gasSurfaceAreaMultiplier = 1.0,
        flow = 0.12,
        lowTemp = 90.19,
        lowTempTransitionTarget = "LiquidOxygen",
        defaultTemperature = 300.0,
        defaultMass = 101.3,
        molarMass = 15.9994,
        toxicity = 0.0,
        lightAbsorptionFactor = 0.0,
        radiationAbsorptionFactor = 0.08,
        radiationPer1000Mass = 0.0,
        materialCategory = "Breathable",
        tags = emptyList(),
        isDisabled = false,
        state = ElementPhase.GAS,
        localizationId = "STRINGS.ELEMENTS.OXYGEN.NAME",
        dlcId = ""
    )

    @JvmField
    val GASES: List<GasSpec> = listOf(
        GAS_OXYGEN,
        GAS_CARBON_DIOXIDE,
        GAS_HYDROGEN
    )

    private val gasById: Map<String, GasSpec> = GASES.associateBy { it.id }
    private val gasBySymbol: Map<String, GasSpec> = GASES.associateBy { it.symbol.uppercase() }
    private val gasByElementId: Map<String, GasSpec> = GASES.associateBy { it.elementId.lowercase() }

    @JvmField
    val LIQUID_SPECS: List<LiquidSpec> = listOf(
        LiquidSpec(
            id = LIQUID_WATER,
            elementId = "Water",
            bottledItemId = OniItemFactory.BOTTLED_WATER,
            defaultMass = 1.0,
            defaultTemperatureK = 295.0
        ),
        LiquidSpec(
            id = LIQUID_POLLUTED_WATER,
            elementId = "DirtyWater",
            bottledItemId = OniItemFactory.BOTTLED_POLLUTED_WATER,
            defaultMass = 1.0,
            defaultTemperatureK = 295.0
        ),
        LiquidSpec(
            id = LIQUID_CRUDE_OIL,
            elementId = "CrudeOil",
            bottledItemId = OniItemFactory.BOTTLED_CRUDE_OIL,
            defaultMass = 1.0,
            defaultTemperatureK = 295.0
        ),
        LiquidSpec(
            id = LIQUID_LAVA,
            elementId = "Magma",
            bottledItemId = OniItemFactory.BOTTLED_LAVA,
            defaultMass = 1.0,
            defaultTemperatureK = 1300.0
        )
    )

    @JvmField
    val LIQUIDS: List<String> = LIQUID_SPECS.map { it.id }

    private val liquidById: Map<String, LiquidSpec> = LIQUID_SPECS.associateBy { it.id }
    init {
        val coreElements: List<ElementSpec> = listOf(
            ElementSpec(
                id = "regolith",
                itemId = Identifier.parse("${AbstractModBootstrap.MOD_ID}:element_regolith"),
                phase = ElementPhase.SOLID,
                thermalConductivityWmK = null,
                massPerItem = null,
                clotterThresholdItems = null,
                transforms = emptyList()
            ),
            ElementSpec(
                id = "sedimentary_rock",
                itemId = Identifier.parse("${AbstractModBootstrap.MOD_ID}:element_sedimentary_rock"),
                phase = ElementPhase.SOLID,
                thermalConductivityWmK = null,
                massPerItem = null,
                clotterThresholdItems = null,
                transforms = emptyList()
            ),
            ElementSpec(
                id = "igneous_rock",
                itemId = Identifier.parse("${AbstractModBootstrap.MOD_ID}:element_igneous_rock"),
                phase = ElementPhase.SOLID,
                thermalConductivityWmK = null,
                massPerItem = null,
                clotterThresholdItems = null,
                transforms = emptyList()
            ),
            ElementSpec(
                id = "granite",
                itemId = Identifier.parse("${AbstractModBootstrap.MOD_ID}:element_granite"),
                phase = ElementPhase.SOLID,
                thermalConductivityWmK = null,
                massPerItem = null,
                clotterThresholdItems = null,
                transforms = emptyList()
            ),
            ElementSpec(
                id = "abyssalite",
                itemId = Identifier.parse("${AbstractModBootstrap.MOD_ID}:element_abyssalite"),
                phase = ElementPhase.SOLID,
                thermalConductivityWmK = null,
                massPerItem = null,
                clotterThresholdItems = null,
                transforms = emptyList()
            ),
            ElementSpec(
                id = "algae",
                itemId = Identifier.parse("${AbstractModBootstrap.MOD_ID}:element_algae"),
                phase = ElementPhase.SOLID,
                thermalConductivityWmK = null,
                massPerItem = null,
                clotterThresholdItems = null,
                transforms = emptyList()
            ),
            ElementSpec(
                id = "polluted_dirt",
                itemId = Identifier.parse("${AbstractModBootstrap.MOD_ID}:element_polluted_dirt"),
                phase = ElementPhase.SOLID,
                thermalConductivityWmK = null,
                massPerItem = null,
                clotterThresholdItems = null,
                transforms = emptyList()
            ),
            ElementSpec(
                id = "metal_ore",
                itemId = Identifier.parse("${AbstractModBootstrap.MOD_ID}:element_metal_ore"),
                phase = ElementPhase.SOLID,
                thermalConductivityWmK = null,
                massPerItem = null,
                clotterThresholdItems = null,
                transforms = emptyList()
            ),
            ElementSpec(
                id = "refined_metal",
                itemId = Identifier.parse("${AbstractModBootstrap.MOD_ID}:element_refined_metal"),
                phase = ElementPhase.SOLID,
                thermalConductivityWmK = null,
                massPerItem = null,
                clotterThresholdItems = null,
                transforms = emptyList()
            )
        )

        val liquidElements = LIQUID_SPECS.map { spec ->
            ElementSpec(
                id = spec.id,
                itemId = Identifier.parse("${AbstractModBootstrap.MOD_ID}:${spec.bottledItemId}"),
                phase = ElementPhase.LIQUID,
                thermalConductivityWmK = spec.thermalConductivityWmK,
                massPerItem = spec.itemMass(),
                clotterThresholdItems = spec.clotterThresholdItems,
                transforms = spec.transforms
            )
        }

        for (spec in coreElements + liquidElements) {
            REGISTRY.register(spec)
        }
    }

    fun elementItems(): Set<Item> = Collections.unmodifiableSet(elementItems)

    fun isLiquid(id: String): Boolean {
        return LIQUIDS.contains(id)
    }

    fun liquidSpec(id: String): LiquidSpec? = liquidById[id]

    fun parseLiquidId(input: String): String? {
        val normalized = input.trim().lowercase()
        return when (normalized) {
            "none", "vacuum", "empty" -> LIQUID_NONE
            "water" -> LIQUID_WATER
            "polluted_water", "pollutedwater" -> LIQUID_POLLUTED_WATER
            "crude_oil", "crudeoil" -> LIQUID_CRUDE_OIL
            "lava", "magma" -> LIQUID_LAVA
            else -> null
        }
    }

    fun parseGas(input: String): GasSpec? {
        val trimmed = input.trim()
        if (trimmed.isBlank()) {
            return null
        }
        val normalized = trimmed.lowercase()
        return gasById[normalized]
            ?: gasByElementId[normalized]
            ?: gasBySymbol[trimmed.uppercase()]
    }

    fun refreshElementItems() {
        elementItems.clear()
        for (spec in REGISTRY.all().values) {
            val item = OniItemFactory.itemById(spec.itemId.toString()) ?: continue
            elementItems.add(item)
        }
    }

}
