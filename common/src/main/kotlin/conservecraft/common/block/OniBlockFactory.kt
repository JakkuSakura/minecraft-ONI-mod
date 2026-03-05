package conservecraft.common.block

import conservecraft.common.AbstractModBootstrap
import conservecraft.common.element.ElementContents
import conservecraft.common.element.OniElements
import conservecraft.common.item.OniBlueprintRegistry
import conservecraft.common.refining.RefiningMachineBlock
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.block.state.BlockBehaviour

object OniBlockFactory {
    const val REGOLITH = "regolith"
    const val SEDIMENTARY_ROCK = "sedimentary_rock"
    const val IGNEOUS_ROCK = "igneous_rock"
    const val GRANITE = "granite"
    const val ABYSSALITE = "abyssalite"
    const val ALGAE = "algae"
    const val POLLUTED_DIRT = "polluted_dirt"
    const val PRINTING_POD = "printing_pod"
    const val OXYGEN_DIFFUSER = "oxygen_diffuser"
    const val ALGAE_DEOXIDIZER = "algae_deoxidizer"
    const val CO2_SCRUBBER = "co2_scrubber"
    const val LIQUID_PUMP = "liquid_pump"
    const val GAS_PUMP = "gas_pump"
    const val MANUAL_GENERATOR = "manual_generator"
    const val BATTERY = "battery"
    const val POWER_WIRE = "power_wire"
    const val POWER_SWITCH = "power_switch"
    const val POWER_SHUTOFF = "power_shutoff"
    const val WIRE = "wire"
    const val WIRE_BRIDGE = "wire_bridge"
    const val CONDUCTIVE_WIRE = "conductive_wire"
    const val CONDUCTIVE_WIRE_BRIDGE = "conductive_wire_bridge"
    const val HEAVI_WATT_WIRE = "heavi_watt_wire"
    const val HEAVI_WATT_JOINT_PLATE = "heavi_watt_joint_plate"
    const val HEAVI_WATT_CONDUCTIVE_WIRE = "heavi_watt_conductive_wire"
    const val HEAVI_WATT_CONDUCTIVE_JOINT_PLATE = "heavi_watt_conductive_joint_plate"
    const val POWER_TRANSFORMER = "power_transformer"
    const val POWER_TRANSFORMER_SMALL = "power_transformer_small"
    const val SMART_BATTERY = "smart_battery"
    const val JUMBO_BATTERY = "jumbo_battery"
    const val COAL_GENERATOR = "coal_generator"
    const val HYDROGEN_GENERATOR = "hydrogen_generator"
    const val NATURAL_GAS_GENERATOR = "natural_gas_generator"
    const val PETROLEUM_GENERATOR = "petroleum_generator"
    const val POWER_CONTROL_STATION = "power_control_station"
    const val POWER_GENERATOR = "power_generator"
    const val RESEARCH_DESK = "research_desk"
    const val CONSTRUCTION_SITE = "construction_site"
    const val OXYGEN_GAS = "oxygen_gas"
    const val CARBON_DIOXIDE_GAS = "carbon_dioxide_gas"
    const val HYDROGEN_GAS = "hydrogen_gas"
    const val WATER = "water"
    const val POLLUTED_WATER = "polluted_water"
    const val CRUDE_OIL = "crude_oil"
    const val LAVA = "lava"
    const val LIQUID_CONDUIT = "liquid_conduit"
    const val GAS_CONDUIT = "gas_conduit"
    const val LIQUID_VENT = "liquid_vent"
    const val GAS_VENT = "gas_vent"
    const val FABRICATED_WOOD_MAKER = "fabricated_wood_maker"
    const val COMPOST = "compost"
    const val WATER_PURIFIER = "water_purifier"
    const val DESALINATOR = "desalinator"
    const val FERTILIZER_MAKER = "fertilizer_maker"
    const val ALGAE_DISTILLERY = "algae_distillery"
    const val ETHANOL_DISTILLERY = "ethanol_distillery"
    const val ROCK_CRUSHER = "rock_crusher"
    const val KILN = "kiln"
    const val SLUDGE_PRESS = "sludge_press"
    const val METAL_REFINERY = "metal_refinery"
    const val GLASS_FORGE = "glass_forge"
    const val OIL_REFINERY = "oil_refinery"
    const val POLYMERIZER = "polymerizer"
    const val OXYLITE_REFINERY = "oxylite_refinery"
    const val CHLORINATOR = "chlorinator"
    const val CHEMICAL_REFINERY = "chemical_refinery"
    const val SUPERMATERIAL_REFINERY = "supermaterial_refinery"
    const val DIAMOND_PRESS = "diamond_press"
    const val MILK_FAT_SEPARATOR = "milk_fat_separator"
    const val MILK_PRESS = "milk_press"
    const val SALT_WATER = "salt_water"
    const val BRINE = "brine"
    const val ETHANOL = "ethanol"
    const val PETROLEUM = "petroleum"
    const val MILK = "milk"
    const val NATURAL_RESIN = "natural_resin"
    const val PHYTO_OIL = "phyto_oil"
    const val MOLTEN_GLASS = "molten_glass"
    const val SUPER_COOLANT = "super_coolant"
    const val VISCO_GEL = "visco_gel"
    const val METHANE_GAS = "methane_gas"
    const val STEAM_GAS = "steam_gas"
    const val CHLORINE_GAS = "chlorine_gas"

    enum class BlockKind {
        SOLID,
        LIQUID,
        GAS
    }

    data class BlockEntry(
        val id: String,
        val kind: BlockKind,
        val factory: () -> Block
    )

    data class SolidBlockSpec(
        val conductivityCoefficient: Double = 1.0
    )

    private data class BlockElementsSpec(
        val elements: List<ElementContents>
    )

    private const val DEFAULT_SOLID_TEMP_K: Double = 293.15

    private fun blockKey(id: String): ResourceKey<Block> {
        val identifier = Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:$id")
            ?: throw IllegalArgumentException("Invalid block id: $id")
        return ResourceKey.create(Registries.BLOCK, identifier)
    }

    private fun propsFor(id: String): BlockBehaviour.Properties {
        return BlockBehaviour.Properties.of().setId(blockKey(id))
    }

    private fun entry(id: String, kind: BlockKind, factory: () -> Block): BlockEntry =
        BlockEntry(id, kind, factory)

    private val SOLID_SPECS: MutableMap<String, SolidBlockSpec> = LinkedHashMap()
    private val SOLID_BY_BLOCK: MutableMap<Block, SolidBlockSpec> = LinkedHashMap()
    private val DEFAULT_ELEMENTS_BY_ID: MutableMap<String, BlockElementsSpec> = LinkedHashMap()

    fun solidSpec(id: String): SolidBlockSpec? = SOLID_SPECS[id]

    private fun solidEntry(
        id: String,
        spec: SolidBlockSpec,
        defaultElements: List<ElementContents>,
        factory: (SolidBlockSpec, List<ElementContents>) -> Block
    ): BlockEntry {
        SOLID_SPECS[id] = spec
        DEFAULT_ELEMENTS_BY_ID[id] = BlockElementsSpec(defaultElements)
        return entry(id, BlockKind.SOLID) {
            val block = factory(spec, defaultElements)
            SOLID_BY_BLOCK[block] = spec
            block
        }
    }

    private fun liquidEntry(
        id: String,
        defaultElements: List<ElementContents>,
        factory: (List<ElementContents>) -> Block
    ): BlockEntry {
        DEFAULT_ELEMENTS_BY_ID[id] = BlockElementsSpec(defaultElements)
        return entry(id, BlockKind.LIQUID) { factory(defaultElements) }
    }

    private fun gasEntry(
        id: String,
        defaultElements: List<ElementContents>,
        factory: (List<ElementContents>) -> Block
    ): BlockEntry {
        DEFAULT_ELEMENTS_BY_ID[id] = BlockElementsSpec(defaultElements)
        return entry(id, BlockKind.GAS) { factory(defaultElements) }
    }

    fun blockConductivityCoefficient(state: net.minecraft.world.level.block.state.BlockState): Double {
        return SOLID_BY_BLOCK[state.block]?.conductivityCoefficient ?: 1.0
    }

    fun defaultElements(id: String): List<ElementContents> {
        val spec = DEFAULT_ELEMENTS_BY_ID[id] ?: return emptyList()
        return spec.elements.map { it.copy() }
    }

    fun defaultElements(block: Block): List<ElementContents> {
        val id = idOf(block) ?: return emptyList()
        return defaultElements(id)
    }

    private fun solidElements(elementId: String, mass: Double): List<ElementContents> {
        return listOf(ElementContents(elementId, mass, DEFAULT_SOLID_TEMP_K))
    }

    private fun defaultElementsFromBlueprint(blueprintId: String): List<ElementContents> {
        val blueprint = OniBlueprintRegistry.get(blueprintId) ?: return emptyList()
        return blueprint.materialSlots.mapNotNull { slot ->
            if (slot.amount <= 0) {
                return@mapNotNull null
            }
            val itemId = slot.allowedItems.firstOrNull() ?: return@mapNotNull null
            val elementId = OniElements.elementIdForItemId(itemId) ?: return@mapNotNull null
            ElementContents(elementId, slot.amount.toDouble(), DEFAULT_SOLID_TEMP_K)
        }
    }

    private fun defaultElementsForBuilding(id: String): List<ElementContents> {
        val fromBlueprint = defaultElementsFromBlueprint(id)
        if (fromBlueprint.isNotEmpty()) {
            return fromBlueprint
        }
        return solidElements("refined_metal", 100.0)
    }

    private fun liquidElements(liquidId: String): List<ElementContents> {
        val spec = OniElements.liquidSpec(liquidId) ?: return emptyList()
        return listOf(ElementContents(spec.id, spec.defaultMass, spec.defaultTemperatureK))
    }

    private fun gasElements(spec: OniElements.GasSpec): List<ElementContents> {
        return listOf(ElementContents(spec.id, spec.defaultMass, spec.defaultTemperature))
    }

    private val SOLID_ENTRIES: List<BlockEntry> = listOf(
        solidEntry(REGOLITH, SolidBlockSpec(), solidElements("regolith", 1000.0), { _, defaults ->
            OniSolidBlock(
                REGOLITH,
                defaultElements = defaults,
                properties = propsFor(REGOLITH).mapColor(MapColor.COLOR_BROWN).strength(0.6f).sound(SoundType.GRAVEL)
            )
        }),
        solidEntry(SEDIMENTARY_ROCK, SolidBlockSpec(), solidElements("sedimentary_rock", 1840.0), { _, defaults ->
            OniSolidBlock(
                SEDIMENTARY_ROCK,
                defaultElements = defaults,
                properties = propsFor(SEDIMENTARY_ROCK).mapColor(MapColor.COLOR_YELLOW).strength(1.2f, 3.0f).sound(SoundType.STONE)
            )
        }),
        solidEntry(IGNEOUS_ROCK, SolidBlockSpec(), solidElements("igneous_rock", 1840.0), { _, defaults ->
            OniSolidBlock(
                IGNEOUS_ROCK,
                defaultElements = defaults,
                properties = propsFor(IGNEOUS_ROCK).mapColor(MapColor.COLOR_GRAY).strength(1.5f, 4.0f).sound(SoundType.STONE)
            )
        }),
        solidEntry(GRANITE, SolidBlockSpec(), solidElements("granite", 1840.0), { _, defaults ->
            OniSolidBlock(
                GRANITE,
                defaultElements = defaults,
                properties = propsFor(GRANITE).mapColor(MapColor.COLOR_ORANGE).strength(1.6f, 4.5f).sound(SoundType.STONE)
            )
        }),
        solidEntry(ABYSSALITE, SolidBlockSpec(), solidElements("abyssalite", 500.0), { _, defaults ->
            OniSolidBlock(
                ABYSSALITE,
                defaultElements = defaults,
                properties = propsFor(ABYSSALITE).mapColor(MapColor.COLOR_BLACK).strength(50.0f, 1200.0f).sound(SoundType.STONE)
            )
        }),
        solidEntry(ALGAE, SolidBlockSpec(), solidElements("algae", 200.0), { _, defaults ->
            OniSolidBlock(
                ALGAE,
                defaultElements = defaults,
                properties = propsFor(ALGAE).mapColor(MapColor.COLOR_GREEN).strength(0.4f).sound(SoundType.GRASS)
            )
        }),
        solidEntry(POLLUTED_DIRT, SolidBlockSpec(), solidElements("polluted_dirt", 1000.0), { _, defaults ->
            OniSolidBlock(
                POLLUTED_DIRT,
                defaultElements = defaults,
                properties = propsFor(POLLUTED_DIRT).mapColor(MapColor.COLOR_BROWN).strength(0.6f).sound(SoundType.GRAVEL)
            )
        }),
        solidEntry(PRINTING_POD, SolidBlockSpec(), solidElements("refined_metal", 200.0), { _, defaults ->
            PrintingPodBlock(
                PRINTING_POD,
                defaultElements = defaults,
                properties = propsFor(PRINTING_POD).mapColor(MapColor.COLOR_LIGHT_GRAY).strength(3.0f, 6.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(OXYGEN_DIFFUSER, SolidBlockSpec(), defaultElementsForBuilding(OXYGEN_DIFFUSER), { _, defaults ->
            OniSolidBlock(
                OXYGEN_DIFFUSER,
                defaultElements = defaults,
                properties = propsFor(OXYGEN_DIFFUSER).mapColor(MapColor.COLOR_LIGHT_BLUE).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(ALGAE_DEOXIDIZER, SolidBlockSpec(), defaultElementsForBuilding(ALGAE_DEOXIDIZER), { _, defaults ->
            OniSolidBlock(
                ALGAE_DEOXIDIZER,
                defaultElements = defaults,
                properties = propsFor(ALGAE_DEOXIDIZER).mapColor(MapColor.COLOR_GREEN).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(CO2_SCRUBBER, SolidBlockSpec(), defaultElementsForBuilding(CO2_SCRUBBER), { _, defaults ->
            OniSolidBlock(
                CO2_SCRUBBER,
                defaultElements = defaults,
                properties = propsFor(CO2_SCRUBBER).mapColor(MapColor.COLOR_GRAY).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(LIQUID_PUMP, SolidBlockSpec(), defaultElementsForBuilding(LIQUID_PUMP), { _, defaults ->
            OniSolidBlock(
                LIQUID_PUMP,
                defaultElements = defaults,
                properties = propsFor(LIQUID_PUMP).mapColor(MapColor.COLOR_CYAN).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(GAS_PUMP, SolidBlockSpec(), defaultElementsForBuilding(GAS_PUMP), { _, defaults ->
            OniSolidBlock(
                GAS_PUMP,
                defaultElements = defaults,
                properties = propsFor(GAS_PUMP).mapColor(MapColor.COLOR_LIGHT_GRAY).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(MANUAL_GENERATOR, SolidBlockSpec(), defaultElementsForBuilding(MANUAL_GENERATOR), { _, defaults ->
            OniSolidBlock(
                MANUAL_GENERATOR,
                defaultElements = defaults,
                properties = propsFor(MANUAL_GENERATOR).mapColor(MapColor.COLOR_ORANGE).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(BATTERY, SolidBlockSpec(), defaultElementsForBuilding(BATTERY), { _, defaults ->
            OniSolidBlock(
                BATTERY,
                defaultElements = defaults,
                properties = propsFor(BATTERY).mapColor(MapColor.COLOR_YELLOW).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(POWER_WIRE, SolidBlockSpec(), defaultElementsForBuilding(POWER_WIRE), { _, defaults ->
            OniSolidBlock(
                POWER_WIRE,
                defaultElements = defaults,
                properties = propsFor(POWER_WIRE).mapColor(MapColor.COLOR_BROWN).noOcclusion().strength(0.6f).sound(SoundType.METAL)
            )
        }),
        solidEntry(WIRE, SolidBlockSpec(), defaultElementsForBuilding(WIRE), { _, defaults ->
            OniSolidBlock(
                WIRE,
                defaultElements = defaults,
                properties = propsFor(WIRE).mapColor(MapColor.COLOR_RED).noOcclusion().strength(0.4f).sound(SoundType.METAL)
            )
        }),
        solidEntry(WIRE_BRIDGE, SolidBlockSpec(), defaultElementsForBuilding(WIRE_BRIDGE), { _, defaults ->
            OniSolidBlock(
                WIRE_BRIDGE,
                defaultElements = defaults,
                properties = propsFor(WIRE_BRIDGE).mapColor(MapColor.COLOR_RED).strength(0.6f).sound(SoundType.METAL)
            )
        }),
        solidEntry(CONDUCTIVE_WIRE, SolidBlockSpec(), defaultElementsForBuilding(CONDUCTIVE_WIRE), { _, defaults ->
            OniSolidBlock(
                CONDUCTIVE_WIRE,
                defaultElements = defaults,
                properties = propsFor(CONDUCTIVE_WIRE).mapColor(MapColor.COLOR_YELLOW).noOcclusion().strength(0.4f).sound(SoundType.METAL)
            )
        }),
        solidEntry(CONDUCTIVE_WIRE_BRIDGE, SolidBlockSpec(), defaultElementsForBuilding(CONDUCTIVE_WIRE_BRIDGE), { _, defaults ->
            OniSolidBlock(
                CONDUCTIVE_WIRE_BRIDGE,
                defaultElements = defaults,
                properties = propsFor(CONDUCTIVE_WIRE_BRIDGE).mapColor(MapColor.COLOR_YELLOW).strength(0.6f).sound(SoundType.METAL)
            )
        }),
        solidEntry(HEAVI_WATT_WIRE, SolidBlockSpec(), defaultElementsForBuilding(HEAVI_WATT_WIRE), { _, defaults ->
            OniSolidBlock(
                HEAVI_WATT_WIRE,
                defaultElements = defaults,
                properties = propsFor(HEAVI_WATT_WIRE).mapColor(MapColor.COLOR_ORANGE).noOcclusion().strength(0.8f).sound(SoundType.METAL)
            )
        }),
        solidEntry(HEAVI_WATT_JOINT_PLATE, SolidBlockSpec(), defaultElementsForBuilding(HEAVI_WATT_JOINT_PLATE), { _, defaults ->
            OniSolidBlock(
                HEAVI_WATT_JOINT_PLATE,
                defaultElements = defaults,
                properties = propsFor(HEAVI_WATT_JOINT_PLATE).mapColor(MapColor.COLOR_ORANGE).strength(1.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(HEAVI_WATT_CONDUCTIVE_WIRE, SolidBlockSpec(), defaultElementsForBuilding(HEAVI_WATT_CONDUCTIVE_WIRE), { _, defaults ->
            OniSolidBlock(
                HEAVI_WATT_CONDUCTIVE_WIRE,
                defaultElements = defaults,
                properties = propsFor(HEAVI_WATT_CONDUCTIVE_WIRE).mapColor(MapColor.COLOR_YELLOW).noOcclusion().strength(0.8f).sound(SoundType.METAL)
            )
        }),
        solidEntry(HEAVI_WATT_CONDUCTIVE_JOINT_PLATE, SolidBlockSpec(), defaultElementsForBuilding(HEAVI_WATT_CONDUCTIVE_JOINT_PLATE), { _, defaults ->
            OniSolidBlock(
                HEAVI_WATT_CONDUCTIVE_JOINT_PLATE,
                defaultElements = defaults,
                properties = propsFor(HEAVI_WATT_CONDUCTIVE_JOINT_PLATE).mapColor(MapColor.COLOR_YELLOW).strength(1.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(POWER_TRANSFORMER, SolidBlockSpec(), defaultElementsForBuilding(POWER_TRANSFORMER), { _, defaults ->
            OniSolidBlock(
                POWER_TRANSFORMER,
                defaultElements = defaults,
                properties = propsFor(POWER_TRANSFORMER).mapColor(MapColor.COLOR_GRAY).strength(3.0f, 6.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(POWER_TRANSFORMER_SMALL, SolidBlockSpec(), defaultElementsForBuilding(POWER_TRANSFORMER_SMALL), { _, defaults ->
            OniSolidBlock(
                POWER_TRANSFORMER_SMALL,
                defaultElements = defaults,
                properties = propsFor(POWER_TRANSFORMER_SMALL).mapColor(MapColor.COLOR_GRAY).strength(2.5f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(SMART_BATTERY, SolidBlockSpec(), defaultElementsForBuilding(SMART_BATTERY), { _, defaults ->
            OniSolidBlock(
                SMART_BATTERY,
                defaultElements = defaults,
                properties = propsFor(SMART_BATTERY).mapColor(MapColor.COLOR_YELLOW).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(JUMBO_BATTERY, SolidBlockSpec(), defaultElementsForBuilding(JUMBO_BATTERY), { _, defaults ->
            OniSolidBlock(
                JUMBO_BATTERY,
                defaultElements = defaults,
                properties = propsFor(JUMBO_BATTERY).mapColor(MapColor.COLOR_YELLOW).strength(2.5f, 5.5f).sound(SoundType.METAL)
            )
        }),
        solidEntry(COAL_GENERATOR, SolidBlockSpec(), defaultElementsForBuilding(COAL_GENERATOR), { _, defaults ->
            OniSolidBlock(
                COAL_GENERATOR,
                defaultElements = defaults,
                properties = propsFor(COAL_GENERATOR).mapColor(MapColor.COLOR_BLACK).strength(4.0f, 8.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(HYDROGEN_GENERATOR, SolidBlockSpec(), defaultElementsForBuilding(HYDROGEN_GENERATOR), { _, defaults ->
            OniSolidBlock(
                HYDROGEN_GENERATOR,
                defaultElements = defaults,
                properties = propsFor(HYDROGEN_GENERATOR).mapColor(MapColor.COLOR_LIGHT_BLUE).strength(4.0f, 8.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(NATURAL_GAS_GENERATOR, SolidBlockSpec(), defaultElementsForBuilding(NATURAL_GAS_GENERATOR), { _, defaults ->
            OniSolidBlock(
                NATURAL_GAS_GENERATOR,
                defaultElements = defaults,
                properties = propsFor(NATURAL_GAS_GENERATOR).mapColor(MapColor.COLOR_GREEN).strength(4.0f, 8.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(PETROLEUM_GENERATOR, SolidBlockSpec(), defaultElementsForBuilding(PETROLEUM_GENERATOR), { _, defaults ->
            OniSolidBlock(
                PETROLEUM_GENERATOR,
                defaultElements = defaults,
                properties = propsFor(PETROLEUM_GENERATOR).mapColor(MapColor.COLOR_BROWN).strength(4.0f, 8.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(POWER_CONTROL_STATION, SolidBlockSpec(), defaultElementsForBuilding(POWER_CONTROL_STATION), { _, defaults ->
            OniSolidBlock(
                POWER_CONTROL_STATION,
                defaultElements = defaults,
                properties = propsFor(POWER_CONTROL_STATION).mapColor(MapColor.COLOR_CYAN).strength(2.5f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(POWER_SWITCH, SolidBlockSpec(), defaultElementsForBuilding(POWER_SWITCH), { _, defaults ->
            OniSolidBlock(
                POWER_SWITCH,
                defaultElements = defaults,
                properties = propsFor(POWER_SWITCH).mapColor(MapColor.COLOR_RED).strength(1.5f, 3.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(POWER_SHUTOFF, SolidBlockSpec(), defaultElementsForBuilding(POWER_SHUTOFF), { _, defaults ->
            OniSolidBlock(
                POWER_SHUTOFF,
                defaultElements = defaults,
                properties = propsFor(POWER_SHUTOFF).mapColor(MapColor.COLOR_RED).strength(1.5f, 3.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(POWER_GENERATOR, SolidBlockSpec(), defaultElementsForBuilding(POWER_GENERATOR), { _, defaults ->
            OniSolidBlock(
                POWER_GENERATOR,
                defaultElements = defaults,
                properties = propsFor(POWER_GENERATOR).mapColor(MapColor.COLOR_ORANGE).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(RESEARCH_DESK, SolidBlockSpec(), defaultElementsForBuilding(RESEARCH_DESK), { _, defaults ->
            OniSolidBlock(
                RESEARCH_DESK,
                defaultElements = defaults,
                properties = propsFor(RESEARCH_DESK).mapColor(MapColor.COLOR_LIGHT_GRAY).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(CONSTRUCTION_SITE, SolidBlockSpec(), defaultElementsForBuilding(CONSTRUCTION_SITE), { _, _ ->
            ConstructionSiteBlock(
                propsFor(CONSTRUCTION_SITE).mapColor(MapColor.COLOR_LIGHT_GRAY).noOcclusion().strength(1.0f, 2.0f)
                    .sound(SoundType.METAL)
            )
        }),
        solidEntry(LIQUID_CONDUIT, SolidBlockSpec(), defaultElementsForBuilding(LIQUID_CONDUIT), { _, _ ->
            ConduitBlock(
                propsFor(LIQUID_CONDUIT).mapColor(MapColor.COLOR_CYAN).noOcclusion().strength(0.4f).sound(SoundType.METAL)
            )
        }),
        solidEntry(GAS_CONDUIT, SolidBlockSpec(), defaultElementsForBuilding(GAS_CONDUIT), { _, _ ->
            ConduitBlock(
                propsFor(GAS_CONDUIT).mapColor(MapColor.COLOR_LIGHT_BLUE).noOcclusion().strength(0.4f).sound(SoundType.METAL)
            )
        }),
        solidEntry(LIQUID_VENT, SolidBlockSpec(), defaultElementsForBuilding(LIQUID_VENT), { _, defaults ->
            OniSolidBlock(
                LIQUID_VENT,
                defaultElements = defaults,
                properties = propsFor(LIQUID_VENT).mapColor(MapColor.COLOR_CYAN).noOcclusion().strength(0.6f).sound(SoundType.METAL)
            )
        }),
        solidEntry(GAS_VENT, SolidBlockSpec(), defaultElementsForBuilding(GAS_VENT), { _, defaults ->
            OniSolidBlock(
                GAS_VENT,
                defaultElements = defaults,
                properties = propsFor(GAS_VENT).mapColor(MapColor.COLOR_LIGHT_BLUE).noOcclusion().strength(0.6f).sound(SoundType.METAL)
            )
        }),
        // Placeholder textures reused from power_wire assets until custom art lands.
        solidEntry(FABRICATED_WOOD_MAKER, SolidBlockSpec(), defaultElementsForBuilding(FABRICATED_WOOD_MAKER), { _, _ ->
            RefiningMachineBlock(
                propsFor(FABRICATED_WOOD_MAKER).mapColor(MapColor.COLOR_BROWN).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(COMPOST, SolidBlockSpec(), defaultElementsForBuilding(COMPOST), { _, _ ->
            RefiningMachineBlock(
                propsFor(COMPOST).mapColor(MapColor.COLOR_BROWN).strength(1.5f, 4.0f).sound(SoundType.GRAVEL)
            )
        }),
        solidEntry(WATER_PURIFIER, SolidBlockSpec(), defaultElementsForBuilding(WATER_PURIFIER), { _, _ ->
            RefiningMachineBlock(
                propsFor(WATER_PURIFIER).mapColor(MapColor.COLOR_BLUE).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(DESALINATOR, SolidBlockSpec(), defaultElementsForBuilding(DESALINATOR), { _, _ ->
            RefiningMachineBlock(
                propsFor(DESALINATOR).mapColor(MapColor.COLOR_CYAN).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(FERTILIZER_MAKER, SolidBlockSpec(), defaultElementsForBuilding(FERTILIZER_MAKER), { _, _ ->
            RefiningMachineBlock(
                propsFor(FERTILIZER_MAKER).mapColor(MapColor.COLOR_GREEN).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(ALGAE_DISTILLERY, SolidBlockSpec(), defaultElementsForBuilding(ALGAE_DISTILLERY), { _, _ ->
            RefiningMachineBlock(
                propsFor(ALGAE_DISTILLERY).mapColor(MapColor.COLOR_GREEN).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(ETHANOL_DISTILLERY, SolidBlockSpec(), defaultElementsForBuilding(ETHANOL_DISTILLERY), { _, _ ->
            RefiningMachineBlock(
                propsFor(ETHANOL_DISTILLERY).mapColor(MapColor.COLOR_ORANGE).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(ROCK_CRUSHER, SolidBlockSpec(), defaultElementsForBuilding(ROCK_CRUSHER), { _, _ ->
            RefiningMachineBlock(
                propsFor(ROCK_CRUSHER).mapColor(MapColor.COLOR_LIGHT_GRAY).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(KILN, SolidBlockSpec(), defaultElementsForBuilding(KILN), { _, _ ->
            RefiningMachineBlock(
                propsFor(KILN).mapColor(MapColor.COLOR_ORANGE).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(SLUDGE_PRESS, SolidBlockSpec(), defaultElementsForBuilding(SLUDGE_PRESS), { _, _ ->
            RefiningMachineBlock(
                propsFor(SLUDGE_PRESS).mapColor(MapColor.COLOR_GREEN).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(METAL_REFINERY, SolidBlockSpec(), defaultElementsForBuilding(METAL_REFINERY), { _, _ ->
            RefiningMachineBlock(
                propsFor(METAL_REFINERY).mapColor(MapColor.COLOR_GRAY).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(GLASS_FORGE, SolidBlockSpec(), defaultElementsForBuilding(GLASS_FORGE), { _, _ ->
            RefiningMachineBlock(
                propsFor(GLASS_FORGE).mapColor(MapColor.COLOR_LIGHT_BLUE).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(OIL_REFINERY, SolidBlockSpec(), defaultElementsForBuilding(OIL_REFINERY), { _, _ ->
            RefiningMachineBlock(
                propsFor(OIL_REFINERY).mapColor(MapColor.COLOR_BROWN).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(POLYMERIZER, SolidBlockSpec(), defaultElementsForBuilding(POLYMERIZER), { _, _ ->
            RefiningMachineBlock(
                propsFor(POLYMERIZER).mapColor(MapColor.COLOR_PINK).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(OXYLITE_REFINERY, SolidBlockSpec(), defaultElementsForBuilding(OXYLITE_REFINERY), { _, _ ->
            RefiningMachineBlock(
                propsFor(OXYLITE_REFINERY).mapColor(MapColor.COLOR_LIGHT_BLUE).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(CHLORINATOR, SolidBlockSpec(), defaultElementsForBuilding(CHLORINATOR), { _, _ ->
            RefiningMachineBlock(
                propsFor(CHLORINATOR).mapColor(MapColor.COLOR_YELLOW).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(CHEMICAL_REFINERY, SolidBlockSpec(), defaultElementsForBuilding(CHEMICAL_REFINERY), { _, _ ->
            RefiningMachineBlock(
                propsFor(CHEMICAL_REFINERY).mapColor(MapColor.COLOR_PURPLE).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(SUPERMATERIAL_REFINERY, SolidBlockSpec(), defaultElementsForBuilding(SUPERMATERIAL_REFINERY), { _, _ ->
            RefiningMachineBlock(
                propsFor(SUPERMATERIAL_REFINERY).mapColor(MapColor.COLOR_LIGHT_GRAY).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(DIAMOND_PRESS, SolidBlockSpec(), defaultElementsForBuilding(DIAMOND_PRESS), { _, _ ->
            RefiningMachineBlock(
                propsFor(DIAMOND_PRESS).mapColor(MapColor.COLOR_BLUE).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(MILK_FAT_SEPARATOR, SolidBlockSpec(), defaultElementsForBuilding(MILK_FAT_SEPARATOR), { _, _ ->
            RefiningMachineBlock(
                propsFor(MILK_FAT_SEPARATOR).mapColor(MapColor.COLOR_LIGHT_GRAY).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(MILK_PRESS, SolidBlockSpec(), defaultElementsForBuilding(MILK_PRESS), { _, _ ->
            RefiningMachineBlock(
                propsFor(MILK_PRESS).mapColor(MapColor.COLOR_LIGHT_GRAY).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        })
    )

    private val LIQUID_ENTRIES: List<BlockEntry> = listOf(
        liquidEntry(WATER, liquidElements(OniElements.LIQUID_WATER)) { defaults ->
            OniLiquidBlock(
                defaultElements = defaults,
                properties = propsFor(WATER).mapColor(MapColor.WATER).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.WET_GRASS)
            )
        },
        liquidEntry(POLLUTED_WATER, liquidElements(OniElements.LIQUID_POLLUTED_WATER)) { defaults ->
            OniLiquidBlock(
                defaultElements = defaults,
                properties = propsFor(POLLUTED_WATER).mapColor(MapColor.COLOR_GREEN).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.WET_GRASS)
            )
        },
        liquidEntry(CRUDE_OIL, liquidElements(OniElements.LIQUID_CRUDE_OIL)) { defaults ->
            OniLiquidBlock(
                defaultElements = defaults,
                properties = propsFor(CRUDE_OIL).mapColor(MapColor.COLOR_BROWN).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.SLIME_BLOCK)
            )
        },
        liquidEntry(LAVA, liquidElements(OniElements.LIQUID_LAVA)) { defaults ->
            OniLiquidBlock(
                defaultElements = defaults,
                properties = propsFor(LAVA).mapColor(MapColor.COLOR_ORANGE).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.SLIME_BLOCK)
            )
        },
        liquidEntry(SALT_WATER, liquidElements(OniElements.LIQUID_SALT_WATER)) { defaults ->
            OniLiquidBlock(
                defaultElements = defaults,
                properties = propsFor(SALT_WATER).mapColor(MapColor.COLOR_BLUE).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.WET_GRASS)
            )
        },
        liquidEntry(BRINE, liquidElements(OniElements.LIQUID_BRINE)) { defaults ->
            OniLiquidBlock(
                defaultElements = defaults,
                properties = propsFor(BRINE).mapColor(MapColor.COLOR_LIGHT_BLUE).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.WET_GRASS)
            )
        },
        liquidEntry(ETHANOL, liquidElements(OniElements.LIQUID_ETHANOL)) { defaults ->
            OniLiquidBlock(
                defaultElements = defaults,
                properties = propsFor(ETHANOL).mapColor(MapColor.COLOR_ORANGE).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.WET_GRASS)
            )
        },
        liquidEntry(PETROLEUM, liquidElements(OniElements.LIQUID_PETROLEUM)) { defaults ->
            OniLiquidBlock(
                defaultElements = defaults,
                properties = propsFor(PETROLEUM).mapColor(MapColor.COLOR_BROWN).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.SLIME_BLOCK)
            )
        },
        liquidEntry(MILK, liquidElements(OniElements.LIQUID_MILK)) { defaults ->
            OniLiquidBlock(
                defaultElements = defaults,
                properties = propsFor(MILK).mapColor(MapColor.COLOR_LIGHT_GRAY).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.WET_GRASS)
            )
        },
        liquidEntry(NATURAL_RESIN, liquidElements(OniElements.LIQUID_NATURAL_RESIN)) { defaults ->
            OniLiquidBlock(
                defaultElements = defaults,
                properties = propsFor(NATURAL_RESIN).mapColor(MapColor.COLOR_YELLOW).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.WET_GRASS)
            )
        },
        liquidEntry(PHYTO_OIL, liquidElements(OniElements.LIQUID_PHYTO_OIL)) { defaults ->
            OniLiquidBlock(
                defaultElements = defaults,
                properties = propsFor(PHYTO_OIL).mapColor(MapColor.COLOR_GREEN).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.WET_GRASS)
            )
        },
        liquidEntry(MOLTEN_GLASS, liquidElements(OniElements.LIQUID_MOLTEN_GLASS)) { defaults ->
            OniLiquidBlock(
                defaultElements = defaults,
                properties = propsFor(MOLTEN_GLASS).mapColor(MapColor.COLOR_LIGHT_BLUE).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.SLIME_BLOCK)
            )
        },
        liquidEntry(SUPER_COOLANT, liquidElements(OniElements.LIQUID_SUPER_COOLANT)) { defaults ->
            OniLiquidBlock(
                defaultElements = defaults,
                properties = propsFor(SUPER_COOLANT).mapColor(MapColor.COLOR_LIGHT_BLUE).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.WET_GRASS)
            )
        },
        liquidEntry(VISCO_GEL, liquidElements(OniElements.LIQUID_VISCO_GEL)) { defaults ->
            OniLiquidBlock(
                defaultElements = defaults,
                properties = propsFor(VISCO_GEL).mapColor(MapColor.COLOR_CYAN).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.SLIME_BLOCK)
            )
        }
    )

    private val GAS_ENTRIES: List<BlockEntry> = listOf(
        gasEntry(OXYGEN_GAS, gasElements(OniElements.GAS_OXYGEN)) { defaults ->
            OniGasBlock(
                defaultElements = defaults,
                properties = propsFor(OXYGEN_GAS).noCollision().noOcclusion().strength(0.0f).sound(SoundType.WOOL)
            )
        },
        gasEntry(CARBON_DIOXIDE_GAS, gasElements(OniElements.GAS_CARBON_DIOXIDE)) { defaults ->
            OniGasBlock(
                defaultElements = defaults,
                properties = propsFor(CARBON_DIOXIDE_GAS).noCollision().noOcclusion().strength(0.0f).sound(SoundType.WOOL)
            )
        },
        gasEntry(HYDROGEN_GAS, gasElements(OniElements.GAS_HYDROGEN)) { defaults ->
            OniGasBlock(
                defaultElements = defaults,
                properties = propsFor(HYDROGEN_GAS).noCollision().noOcclusion().strength(0.0f).sound(SoundType.WOOL)
            )
        },
        gasEntry(METHANE_GAS, gasElements(OniElements.GAS_METHANE)) { defaults ->
            OniGasBlock(
                defaultElements = defaults,
                properties = propsFor(METHANE_GAS).noCollision().noOcclusion().strength(0.0f).sound(SoundType.WOOL)
            )
        },
        gasEntry(STEAM_GAS, gasElements(OniElements.GAS_STEAM)) { defaults ->
            OniGasBlock(
                defaultElements = defaults,
                properties = propsFor(STEAM_GAS).noCollision().noOcclusion().strength(0.0f).sound(SoundType.WOOL)
            )
        },
        gasEntry(CHLORINE_GAS, gasElements(OniElements.GAS_CHLORINE)) { defaults ->
            OniGasBlock(
                defaultElements = defaults,
                properties = propsFor(CHLORINE_GAS).noCollision().noOcclusion().strength(0.0f).sound(SoundType.WOOL)
            )
        }
    )

    private val ENTRIES: List<BlockEntry> = SOLID_ENTRIES + LIQUID_ENTRIES + GAS_ENTRIES
    private val ENTRIES_BY_ID: Map<String, BlockEntry> = ENTRIES.associateBy { it.id }

    @JvmField
    val SOLID_IDS: List<String> = SOLID_ENTRIES.map { it.id }

    @JvmField
    val GAS_IDS: List<String> = GAS_ENTRIES.map { it.id }

    @JvmField
    val LIQUID_IDS: List<String> = LIQUID_ENTRIES.map { it.id }

    @JvmField
    val REFINING_IDS: List<String> = listOf(
        FABRICATED_WOOD_MAKER,
        COMPOST,
        WATER_PURIFIER,
        DESALINATOR,
        FERTILIZER_MAKER,
        ALGAE_DISTILLERY,
        ETHANOL_DISTILLERY,
        ROCK_CRUSHER,
        KILN,
        SLUDGE_PRESS,
        METAL_REFINERY,
        GLASS_FORGE,
        OIL_REFINERY,
        POLYMERIZER,
        OXYLITE_REFINERY,
        CHLORINATOR,
        CHEMICAL_REFINERY,
        SUPERMATERIAL_REFINERY,
        DIAMOND_PRESS,
        MILK_FAT_SEPARATOR,
        MILK_PRESS
    )

    fun entries(): List<BlockEntry> = ENTRIES

    fun idOf(block: Block): String? {
        val key = BuiltInRegistries.BLOCK.getKey(block)
        if (key.namespace != AbstractModBootstrap.MOD_ID) {
            return null
        }
        return key.path
    }

    fun blockDigYield(entity: conservecraft.common.block.entity.OniElementBlockEntity): Int {
        val total = entity.totalMass()
        return total.coerceAtLeast(0.0).toInt()
    }

    fun blockDigYield(level: net.minecraft.server.level.ServerLevel, pos: net.minecraft.core.BlockPos): Int {
        val entity = level.getBlockEntity(pos) as? conservecraft.common.block.entity.OniElementBlockEntity
        if (entity != null) {
            return blockDigYield(entity)
        }
        val total = conservecraft.common.world.OniElementAccess.totalMass(level, pos)
        return total.coerceAtLeast(0.0).toInt()
    }

    fun createBlock(id: String): Block {
        val entry = ENTRIES_BY_ID[id] ?: throw IllegalArgumentException("Unknown block id: $id")
        return entry.factory()
    }
}
