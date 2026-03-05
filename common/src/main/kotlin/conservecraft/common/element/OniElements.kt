package conservecraft.common.element

import conservecraft.common.AbstractModBootstrap
import conservecraft.common.item.OniItemFactory
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
    const val LIQUID_SALT_WATER: String = "salt_water"
    const val LIQUID_BRINE: String = "brine"
    const val LIQUID_ETHANOL: String = "ethanol"
    const val LIQUID_PETROLEUM: String = "petroleum"
    const val LIQUID_MILK: String = "milk"
    const val LIQUID_NATURAL_RESIN: String = "natural_resin"
    const val LIQUID_PHYTO_OIL: String = "phyto_oil"
    const val LIQUID_MOLTEN_GLASS: String = "molten_glass"
    const val LIQUID_SUPER_COOLANT: String = "super_coolant"
    const val LIQUID_VISCO_GEL: String = "visco_gel"

    data class LiquidSpec(
        val id: String,
        val elementId: String,
        val bottledItemId: String,
        val defaultMass: Double,
        val defaultTemperatureK: Double,
        val specificHeatCapacity: Double,
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

    private val gasSpecificHeatCapacityByOniId: Map<String, Double> = mapOf(
        "Oxygen" to 1.005,
        "CarbonDioxide" to 0.846,
        "Hydrogen" to 2.4,
        "Methane" to 2.191,
        "Steam" to 4.179,
        "ChlorineGas" to 0.48
    )

    private fun gasSpecificHeatCapacity(oniId: String): Double {
        return gasSpecificHeatCapacityByOniId[oniId]
            ?: error("Missing specific heat capacity for gas element id '$oniId'.")
    }

    @JvmField
    val GAS_CARBON_DIOXIDE: GasSpec = GasSpec(
        id = "carbon_dioxide",
        elementId = "CarbonDioxide",
        symbol = "CO2",
        specificHeatCapacity = gasSpecificHeatCapacity("CarbonDioxide"),
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
        specificHeatCapacity = gasSpecificHeatCapacity("Hydrogen"),
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
        specificHeatCapacity = gasSpecificHeatCapacity("Oxygen"),
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
    val GAS_METHANE: GasSpec = GasSpec(
        id = "methane",
        elementId = "Methane",
        symbol = "CH4",
        specificHeatCapacity = gasSpecificHeatCapacity("Methane"),
        thermalConductivity = 0.035,
        solidSurfaceAreaMultiplier = 25.0,
        liquidSurfaceAreaMultiplier = 1.0,
        gasSurfaceAreaMultiplier = 1.0,
        flow = 0.1,
        lowTemp = 111.65,
        lowTempTransitionTarget = "LiquidMethane",
        defaultTemperature = 300.0,
        defaultMass = 7.0,
        molarMass = 16.044,
        toxicity = 0.0,
        lightAbsorptionFactor = 0.25,
        radiationAbsorptionFactor = 0.07,
        radiationPer1000Mass = 0.0,
        materialCategory = "Unbreathable",
        tags = emptyList(),
        isDisabled = false,
        state = ElementPhase.GAS,
        localizationId = "STRINGS.ELEMENTS.METHANE.NAME",
        dlcId = ""
    )

    @JvmField
    val GAS_STEAM: GasSpec = GasSpec(
        id = "steam",
        elementId = "Steam",
        symbol = "H2O",
        specificHeatCapacity = gasSpecificHeatCapacity("Steam"),
        thermalConductivity = 0.184,
        solidSurfaceAreaMultiplier = 25.0,
        liquidSurfaceAreaMultiplier = 1.0,
        gasSurfaceAreaMultiplier = 1.0,
        flow = 0.1,
        lowTemp = 372.5,
        lowTempTransitionTarget = "Water",
        defaultTemperature = 400.0,
        defaultMass = 57.0,
        molarMass = 18.01528,
        toxicity = 0.0,
        lightAbsorptionFactor = 0.1,
        radiationAbsorptionFactor = 0.08,
        radiationPer1000Mass = 0.0,
        materialCategory = "Unbreathable",
        tags = emptyList(),
        isDisabled = false,
        state = ElementPhase.GAS,
        localizationId = "STRINGS.ELEMENTS.STEAM.NAME",
        dlcId = ""
    )

    @JvmField
    val GAS_CHLORINE: GasSpec = GasSpec(
        id = "chlorine",
        elementId = "ChlorineGas",
        symbol = "Cl2",
        specificHeatCapacity = gasSpecificHeatCapacity("ChlorineGas"),
        thermalConductivity = 0.0081,
        solidSurfaceAreaMultiplier = 25.0,
        liquidSurfaceAreaMultiplier = 1.0,
        gasSurfaceAreaMultiplier = 1.0,
        flow = 0.1,
        lowTemp = 238.55,
        lowTempTransitionTarget = "Chlorine",
        defaultTemperature = 300.0,
        defaultMass = 228.0,
        molarMass = 34.453,
        toxicity = 0.0,
        lightAbsorptionFactor = 0.2,
        radiationAbsorptionFactor = 0.07,
        radiationPer1000Mass = 0.0,
        materialCategory = "Unbreathable",
        tags = emptyList(),
        isDisabled = false,
        state = ElementPhase.GAS,
        localizationId = "STRINGS.ELEMENTS.CHLORINE.NAME",
        dlcId = ""
    )

    @JvmField
    val GASES: List<GasSpec> = listOf(
        GAS_OXYGEN,
        GAS_CARBON_DIOXIDE,
        GAS_HYDROGEN,
        GAS_METHANE,
        GAS_STEAM,
        GAS_CHLORINE
    )

    private val gasById: Map<String, GasSpec> = GASES.associateBy { it.id }
    private val gasBySymbol: Map<String, GasSpec> = GASES.associateBy { it.symbol.uppercase() }
    private val gasByElementId: Map<String, GasSpec> = GASES.associateBy { it.elementId.lowercase() }
    private val elementIdByItemId: Map<String, String> by lazy {
        REGISTRY.all().values.associateBy({ it.itemId.toString() }, { it.id })
    }

    private val liquidSpecificHeatCapacityByOniId: Map<String, Double> = mapOf(
        "Water" to 4.179,
        "DirtyWater" to 4.179,
        "CrudeOil" to 1.69,
        "Magma" to 1.0,
        "SaltWater" to 4.1,
        "Brine" to 3.4,
        "Ethanol" to 2.46,
        "Petroleum" to 1.76,
        "Milk" to 4.1,
        "NaturalResin" to 1.11,
        "PhytoOil" to 0.9,
        "MoltenGlass" to 0.2,
        "SuperCoolant" to 8.44,
        "ViscoGel" to 1.55
    )

    private fun liquidSpecificHeatCapacity(oniId: String): Double {
        return liquidSpecificHeatCapacityByOniId[oniId]
            ?: error("Missing specific heat capacity for liquid element id '$oniId'.")
    }

    @JvmField
    val LIQUID_SPECS: List<LiquidSpec> = listOf(
        LiquidSpec(
            id = LIQUID_WATER,
            elementId = "Water",
            bottledItemId = OniItemFactory.BOTTLED_WATER,
            defaultMass = 1000.0,
            defaultTemperatureK = 300.0,
            specificHeatCapacity = liquidSpecificHeatCapacity("Water"),
            thermalConductivityWmK = 0.609
        ),
        LiquidSpec(
            id = LIQUID_POLLUTED_WATER,
            elementId = "DirtyWater",
            bottledItemId = OniItemFactory.BOTTLED_POLLUTED_WATER,
            defaultMass = 1000.0,
            defaultTemperatureK = 300.0,
            specificHeatCapacity = liquidSpecificHeatCapacity("DirtyWater"),
            thermalConductivityWmK = 0.58
        ),
        LiquidSpec(
            id = LIQUID_CRUDE_OIL,
            elementId = "CrudeOil",
            bottledItemId = OniItemFactory.BOTTLED_CRUDE_OIL,
            defaultMass = 870.0,
            defaultTemperatureK = 350.0,
            specificHeatCapacity = liquidSpecificHeatCapacity("CrudeOil"),
            thermalConductivityWmK = 2.0
        ),
        LiquidSpec(
            id = LIQUID_LAVA,
            elementId = "Magma",
            bottledItemId = OniItemFactory.BOTTLED_LAVA,
            defaultMass = 1840.0,
            defaultTemperatureK = 2000.0,
            specificHeatCapacity = liquidSpecificHeatCapacity("Magma"),
            thermalConductivityWmK = 1.0
        ),
        LiquidSpec(
            id = LIQUID_SALT_WATER,
            elementId = "SaltWater",
            bottledItemId = OniItemFactory.BOTTLED_SALT_WATER,
            defaultMass = 1100.0,
            defaultTemperatureK = 300.0,
            specificHeatCapacity = liquidSpecificHeatCapacity("SaltWater"),
            thermalConductivityWmK = 0.609
        ),
        LiquidSpec(
            id = LIQUID_BRINE,
            elementId = "Brine",
            bottledItemId = OniItemFactory.BOTTLED_BRINE,
            defaultMass = 1200.0,
            defaultTemperatureK = 282.15,
            specificHeatCapacity = liquidSpecificHeatCapacity("Brine"),
            thermalConductivityWmK = 0.609
        ),
        LiquidSpec(
            id = LIQUID_ETHANOL,
            elementId = "Ethanol",
            bottledItemId = OniItemFactory.BOTTLED_ETHANOL,
            defaultMass = 1000.0,
            defaultTemperatureK = 300.0,
            specificHeatCapacity = liquidSpecificHeatCapacity("Ethanol"),
            thermalConductivityWmK = 0.171
        ),
        LiquidSpec(
            id = LIQUID_PETROLEUM,
            elementId = "Petroleum",
            bottledItemId = OniItemFactory.BOTTLED_PETROLEUM,
            defaultMass = 740.0,
            defaultTemperatureK = 300.0,
            specificHeatCapacity = liquidSpecificHeatCapacity("Petroleum"),
            thermalConductivityWmK = 2.0
        ),
        LiquidSpec(
            id = LIQUID_MILK,
            elementId = "Milk",
            bottledItemId = OniItemFactory.BOTTLED_MILK,
            defaultMass = 1100.0,
            defaultTemperatureK = 310.0,
            specificHeatCapacity = liquidSpecificHeatCapacity("Milk"),
            thermalConductivityWmK = 0.609
        ),
        LiquidSpec(
            id = LIQUID_NATURAL_RESIN,
            elementId = "NaturalResin",
            bottledItemId = OniItemFactory.BOTTLED_NATURAL_RESIN,
            defaultMass = 920.0,
            defaultTemperatureK = 300.0,
            specificHeatCapacity = liquidSpecificHeatCapacity("NaturalResin"),
            thermalConductivityWmK = 0.15
        ),
        LiquidSpec(
            id = LIQUID_PHYTO_OIL,
            elementId = "PhytoOil",
            bottledItemId = OniItemFactory.BOTTLED_PHYTO_OIL,
            defaultMass = 800.0,
            defaultTemperatureK = 293.0,
            specificHeatCapacity = liquidSpecificHeatCapacity("PhytoOil"),
            thermalConductivityWmK = 2.0
        ),
        LiquidSpec(
            id = LIQUID_MOLTEN_GLASS,
            elementId = "MoltenGlass",
            bottledItemId = OniItemFactory.BOTTLED_MOLTEN_GLASS,
            defaultMass = 200.0,
            defaultTemperatureK = 2215.0,
            specificHeatCapacity = liquidSpecificHeatCapacity("MoltenGlass"),
            thermalConductivityWmK = 1.0
        ),
        LiquidSpec(
            id = LIQUID_SUPER_COOLANT,
            elementId = "SuperCoolant",
            bottledItemId = OniItemFactory.BOTTLED_SUPER_COOLANT,
            defaultMass = 800.0,
            defaultTemperatureK = 238.0,
            specificHeatCapacity = liquidSpecificHeatCapacity("SuperCoolant"),
            thermalConductivityWmK = 9.46
        ),
        LiquidSpec(
            id = LIQUID_VISCO_GEL,
            elementId = "ViscoGel",
            bottledItemId = OniItemFactory.BOTTLED_VISCO_GEL,
            defaultMass = 100.0,
            defaultTemperatureK = 238.0,
            specificHeatCapacity = liquidSpecificHeatCapacity("ViscoGel"),
            thermalConductivityWmK = 0.45
        )
    )

    // Values sourced from ONI solid.yaml; abyssalite uses the Katairite conductivity.
    private val solidThermalConductivityById: Map<String, Double> = mapOf(
        "regolith" to 1.0,
        "sedimentary_rock" to 2.0,
        "igneous_rock" to 2.0,
        "granite" to 3.39,
        "abyssalite" to 1.0e-05,
        "algae" to 2.0,
        "dirt" to 2.0,
        "sand" to 2.0,
        "toxic_sand" to 2.0,
        "salt" to 0.444,
        "slime_mold" to 2.0,
        "wood_log" to 0.22,
        "phosphorite" to 2.0,
        "fertilizer" to 2.0,
        "bleach_stone" to 4.0,
        "ceramic" to 0.62,
        "carbon" to 1.25,
        "peat" to 0.6,
        "refined_carbon" to 3.1,
        "lime" to 2.0,
        "fossil" to 2.0,
        "clay" to 2.0,
        "gold_amalgam" to 2.0,
        "gold" to 60.0,
        "iron" to 55.0,
        "iron_ore" to 4.0,
        "copper" to 60.0,
        "aluminum" to 205.0,
        "aluminum_ore" to 20.5,
        "cobaltite" to 4.0,
        "cobalt" to 100.0,
        "tungsten" to 60.0,
        "steel" to 54.0,
        "diamond" to 80.0,
        "sulfur" to 0.2,
        "fullerene" to 50.0,
        "polypropylene" to 0.15,
        "hard_polypropylene" to 0.25,
        "temp_conductor_solid" to 220.0,
        "milk_fat" to 0.15,
        "isoresin" to 0.17,
        "super_insulator" to 1.0e-05,
        "niobium" to 54.0,
        "enriched_uranium" to 20.0,
        "amber" to 0.17,
        "oxyrock" to 4.0,
        "graphite" to 8.0,
        "katairite" to 1.0e-05
    )

    // Specific heat capacity values sourced from ONI element data (kJ/kg/K).
    private val solidSpecificHeatCapacityByOniId: Map<String, Double> = mapOf(
        "Algae" to 0.2,
        "Aluminum" to 0.91,
        "AluminumOre" to 0.91,
        "Amber" to 1.3,
        "BleachStone" to 0.5,
        "Carbon" to 0.71,
        "Ceramic" to 0.84,
        "Clay" to 0.92,
        "Cobalt" to 0.42,
        "Cobaltite" to 0.42,
        "Copper" to 0.385,
        "Cuprite" to 0.386,
        "Diamond" to 0.516,
        "Dirt" to 1.48,
        "EnrichedUranium" to 1.0,
        "FabricatedWood" to 2.3,
        "Fertilizer" to 0.83,
        "Fossil" to 0.91,
        "Fullerene" to 0.95,
        "Gold" to 0.129,
        "GoldAmalgam" to 0.15,
        "Granite" to 0.79,
        "Graphite" to 0.71,
        "HardPolypropylene" to 1.5,
        "IgneousRock" to 1.0,
        "Iron" to 0.449,
        "IronOre" to 0.449,
        "Isoresin" to 1.3,
        "Katairite" to 4.0,
        "Lime" to 0.834,
        "MilkFat" to 1.92,
        "Niobium" to 0.265,
        "OxyRock" to 1.0,
        "Peat" to 0.71,
        "Phosphorite" to 0.15,
        "Polypropylene" to 1.92,
        "RefinedCarbon" to 1.74,
        "Regolith" to 0.2,
        "Salt" to 0.7,
        "Sand" to 0.83,
        "SedimentaryRock" to 0.2,
        "SlimeMold" to 0.2,
        "Steel" to 0.49,
        "Sulfur" to 0.7,
        "SuperInsulator" to 5.57,
        "TempConductorSolid" to 0.622,
        "ToxicSand" to 0.83,
        "Tungsten" to 0.134,
        "Wolframite" to 0.134,
        "WoodLog" to 2.3
    )

    // Proxy mapping for placeholder or non-ONI ids to real ONI element ids.
    private val solidSpecificHeatCapacityProxyById: Map<String, String> = mapOf(
        "abyssalite" to "Katairite",
        "bean_plant_seed" to "Dirt",
        "building_wood" to "FabricatedWood",
        "cold_wheat_seed" to "Dirt",
        "crab_shell" to "Lime",
        "crab_wood_shell" to "Lime",
        "dew_drip" to "Dirt",
        "egg_shell" to "Lime",
        "fabricated_wood" to "FabricatedWood",
        "garbage_electrobank" to "Iron",
        "gold_belly_crown" to "Gold",
        "ice_belly_poop" to "Dirt",
        "kelp" to "Algae",
        "metal_ore" to "IronOre",
        "oxyrock" to "OxyRock",
        "plant_fiber" to "FabricatedWood",
        "polluted_dirt" to "Dirt",
        "refined_lipid" to "MilkFat",
        "refined_metal" to "Iron",
        "self_charging_electrobank" to "Iron",
        "spice_nut" to "Dirt",
        "table_salt" to "Salt",
        "tungsten_ore" to "Wolframite",
        "copper_ore" to "Cuprite"
    )

    @JvmField
    val LIQUIDS: List<String> = LIQUID_SPECS.map { it.id }

    private val liquidById: Map<String, LiquidSpec> = LIQUID_SPECS.associateBy { it.id }

    private fun toOniSolidId(id: String): String {
        val proxied = solidSpecificHeatCapacityProxyById[id]
        if (proxied != null) {
            return proxied
        }
        return id.split('_').joinToString(separator = "") { part ->
            part.replaceFirstChar { it.uppercase() }
        }
    }

    private fun solidSpecificHeatCapacity(id: String): Double {
        val oniId = toOniSolidId(id)
        return solidSpecificHeatCapacityByOniId[oniId]
            ?: error("Missing specific heat capacity for solid element id '$id' (ONI id '$oniId').")
    }

    private fun solidElement(id: String): ElementSpec {
        return ElementSpec(
            id = id,
            itemId = Identifier.parse("${AbstractModBootstrap.MOD_ID}:element_$id"),
            phase = ElementPhase.SOLID,
            specificHeatCapacity = solidSpecificHeatCapacity(id),
            thermalConductivityWmK = solidThermalConductivityById[id],
            massPerItem = null,
            clotterThresholdItems = null,
            transforms = emptyList()
        )
    }

    init {
        val coreElements: List<ElementSpec> = listOf(
            "regolith",
            "sedimentary_rock",
            "igneous_rock",
            "granite",
            "abyssalite",
            "algae",
            "polluted_dirt",
            "metal_ore",
            "refined_metal"
        ).map { solidElement(it) }

        // Placeholder element specs until ONI element data is integrated.
        val extraSolidElements = listOf(
            "dirt",
            "sand",
            "toxic_sand",
            "salt",
            "table_salt",
            "slime_mold",
            "plant_fiber",
            "fabricated_wood",
            "wood_log",
            "building_wood",
            "phosphorite",
            "fertilizer",
            "bleach_stone",
            "ceramic",
            "carbon",
            "peat",
            "refined_carbon",
            "lime",
            "fossil",
            "clay",
            "gold_amalgam",
            "gold",
            "iron",
            "iron_ore",
            "copper",
            "copper_ore",
            "aluminum",
            "aluminum_ore",
            "cobaltite",
            "cobalt",
            "tungsten",
            "tungsten_ore",
            "steel",
            "diamond",
            "refined_lipid",
            "sulfur",
            "fullerene",
            "polypropylene",
            "hard_polypropylene",
            "temp_conductor_solid",
            "milk_fat",
            "isoresin",
            "super_insulator",
            "niobium",
            "enriched_uranium",
            "amber",
            "oxyrock",
            "graphite",
            "katairite",
            "ice_belly_poop",
            "egg_shell",
            "crab_shell",
            "crab_wood_shell",
            "gold_belly_crown",
            "garbage_electrobank",
            "self_charging_electrobank",
            "cold_wheat_seed",
            "spice_nut",
            "bean_plant_seed",
            "dew_drip",
            "kelp"
        ).map { solidElement(it) }

        val liquidElements = LIQUID_SPECS.map { spec ->
            ElementSpec(
                id = spec.id,
                itemId = Identifier.parse("${AbstractModBootstrap.MOD_ID}:${spec.bottledItemId}"),
                phase = ElementPhase.LIQUID,
                specificHeatCapacity = spec.specificHeatCapacity,
                thermalConductivityWmK = spec.thermalConductivityWmK,
                massPerItem = spec.itemMass(),
                clotterThresholdItems = spec.clotterThresholdItems,
                transforms = spec.transforms
            )
        }

        for (spec in coreElements + extraSolidElements + liquidElements) {
            REGISTRY.register(spec)
        }
    }

    fun elementItems(): Set<Item> = Collections.unmodifiableSet(elementItems)

    fun isLiquid(id: String): Boolean {
        return LIQUIDS.contains(id)
    }

    fun elementSpec(id: String): ElementSpec? = REGISTRY.byId(id)

    fun liquidSpec(id: String): LiquidSpec? = liquidById[id]

    fun specificHeatCapacityForElementId(id: String): Double? {
        return gasById[id]?.specificHeatCapacity ?: REGISTRY.byId(id)?.specificHeatCapacity
    }

    fun parseLiquidId(input: String): String? {
        val normalized = input.trim().lowercase()
        return when (normalized) {
            "none", "vacuum", "empty" -> LIQUID_NONE
            "water" -> LIQUID_WATER
            "polluted_water", "pollutedwater" -> LIQUID_POLLUTED_WATER
            "crude_oil", "crudeoil" -> LIQUID_CRUDE_OIL
            "lava", "magma" -> LIQUID_LAVA
            "salt_water", "saltwater" -> LIQUID_SALT_WATER
            "brine" -> LIQUID_BRINE
            "ethanol" -> LIQUID_ETHANOL
            "petroleum" -> LIQUID_PETROLEUM
            "milk" -> LIQUID_MILK
            "natural_resin", "naturalresin", "resin" -> LIQUID_NATURAL_RESIN
            "phyto_oil", "phytooil" -> LIQUID_PHYTO_OIL
            "molten_glass", "moltenglass" -> LIQUID_MOLTEN_GLASS
            "super_coolant", "supercoolant" -> LIQUID_SUPER_COOLANT
            "visco_gel", "viscogel" -> LIQUID_VISCO_GEL
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

    fun elementIdForItemId(itemId: String): String? = elementIdByItemId[itemId]

}
