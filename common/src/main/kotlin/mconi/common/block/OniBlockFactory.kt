package mconi.common.block

import mconi.common.AbstractModBootstrap
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

    fun solidSpec(id: String): SolidBlockSpec? = SOLID_SPECS[id]

    private fun solidEntry(id: String, spec: SolidBlockSpec, factory: (SolidBlockSpec) -> Block): BlockEntry {
        SOLID_SPECS[id] = spec
        return entry(id, BlockKind.SOLID) {
            val block = factory(spec)
            SOLID_BY_BLOCK[block] = spec
            block
        }
    }

    fun blockConductivityCoefficient(state: net.minecraft.world.level.block.state.BlockState): Double {
        return SOLID_BY_BLOCK[state.block]?.conductivityCoefficient ?: 1.0
    }

    private val SOLID_ENTRIES: List<BlockEntry> = listOf(
        solidEntry(REGOLITH, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                REGOLITH,
                elements = listOf(
                    mconi.common.element.ElementStack("mconi:element_regolith", blockDigYield(REGOLITH).coerceAtLeast(1))
                ),
                properties = propsFor(REGOLITH).mapColor(MapColor.COLOR_BROWN).strength(0.6f).sound(SoundType.GRAVEL)
            )
        }),
        solidEntry(SEDIMENTARY_ROCK, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                SEDIMENTARY_ROCK,
                elements = listOf(
                    mconi.common.element.ElementStack("mconi:element_sedimentary_rock", blockDigYield(SEDIMENTARY_ROCK).coerceAtLeast(1))
                ),
                properties = propsFor(SEDIMENTARY_ROCK).mapColor(MapColor.COLOR_YELLOW).strength(1.2f, 3.0f).sound(SoundType.STONE)
            )
        }),
        solidEntry(IGNEOUS_ROCK, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                IGNEOUS_ROCK,
                elements = listOf(
                    mconi.common.element.ElementStack("mconi:element_igneous_rock", blockDigYield(IGNEOUS_ROCK).coerceAtLeast(1))
                ),
                properties = propsFor(IGNEOUS_ROCK).mapColor(MapColor.COLOR_GRAY).strength(1.5f, 4.0f).sound(SoundType.STONE)
            )
        }),
        solidEntry(GRANITE, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                GRANITE,
                elements = listOf(
                    mconi.common.element.ElementStack("mconi:element_granite", blockDigYield(GRANITE).coerceAtLeast(1))
                ),
                properties = propsFor(GRANITE).mapColor(MapColor.COLOR_ORANGE).strength(1.6f, 4.5f).sound(SoundType.STONE)
            )
        }),
        solidEntry(ABYSSALITE, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                ABYSSALITE,
                elements = listOf(
                    mconi.common.element.ElementStack("mconi:element_abyssalite", blockDigYield(ABYSSALITE).coerceAtLeast(1))
                ),
                properties = propsFor(ABYSSALITE).mapColor(MapColor.COLOR_BLACK).strength(50.0f, 1200.0f).sound(SoundType.STONE)
            )
        }),
        solidEntry(ALGAE, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                ALGAE,
                elements = listOf(
                    mconi.common.element.ElementStack("mconi:element_algae", blockDigYield(ALGAE).coerceAtLeast(1))
                ),
                properties = propsFor(ALGAE).mapColor(MapColor.COLOR_GREEN).strength(0.4f).sound(SoundType.GRASS)
            )
        }),
        solidEntry(POLLUTED_DIRT, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                POLLUTED_DIRT,
                elements = listOf(
                    mconi.common.element.ElementStack("mconi:element_polluted_dirt", blockDigYield(POLLUTED_DIRT).coerceAtLeast(1))
                ),
                properties = propsFor(POLLUTED_DIRT).mapColor(MapColor.COLOR_BROWN).strength(0.6f).sound(SoundType.GRAVEL)
            )
        }),
        solidEntry(PRINTING_POD, SolidBlockSpec(), { _ ->
            PrintingPodBlock(
                PRINTING_POD,
                elements = listOf(
                    mconi.common.element.ElementStack("mconi:element_refined_metal", blockDigYield(PRINTING_POD).coerceAtLeast(1))
                ),
                properties = propsFor(PRINTING_POD).mapColor(MapColor.COLOR_LIGHT_GRAY).strength(3.0f, 6.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(OXYGEN_DIFFUSER, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                OXYGEN_DIFFUSER,
                properties = propsFor(OXYGEN_DIFFUSER).mapColor(MapColor.COLOR_LIGHT_BLUE).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(ALGAE_DEOXIDIZER, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                ALGAE_DEOXIDIZER,
                properties = propsFor(ALGAE_DEOXIDIZER).mapColor(MapColor.COLOR_GREEN).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(CO2_SCRUBBER, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                CO2_SCRUBBER,
                properties = propsFor(CO2_SCRUBBER).mapColor(MapColor.COLOR_GRAY).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(LIQUID_PUMP, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                LIQUID_PUMP,
                properties = propsFor(LIQUID_PUMP).mapColor(MapColor.COLOR_CYAN).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(GAS_PUMP, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                GAS_PUMP,
                properties = propsFor(GAS_PUMP).mapColor(MapColor.COLOR_LIGHT_GRAY).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(MANUAL_GENERATOR, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                MANUAL_GENERATOR,
                properties = propsFor(MANUAL_GENERATOR).mapColor(MapColor.COLOR_ORANGE).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(BATTERY, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                BATTERY,
                properties = propsFor(BATTERY).mapColor(MapColor.COLOR_YELLOW).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(POWER_WIRE, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                POWER_WIRE,
                properties = propsFor(POWER_WIRE).mapColor(MapColor.COLOR_BROWN).noOcclusion().strength(0.6f).sound(SoundType.METAL)
            )
        }),
        solidEntry(WIRE, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                WIRE,
                properties = propsFor(WIRE).mapColor(MapColor.COLOR_RED).noOcclusion().strength(0.4f).sound(SoundType.METAL)
            )
        }),
        solidEntry(WIRE_BRIDGE, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                WIRE_BRIDGE,
                properties = propsFor(WIRE_BRIDGE).mapColor(MapColor.COLOR_RED).strength(0.6f).sound(SoundType.METAL)
            )
        }),
        solidEntry(CONDUCTIVE_WIRE, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                CONDUCTIVE_WIRE,
                properties = propsFor(CONDUCTIVE_WIRE).mapColor(MapColor.COLOR_YELLOW).noOcclusion().strength(0.4f).sound(SoundType.METAL)
            )
        }),
        solidEntry(CONDUCTIVE_WIRE_BRIDGE, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                CONDUCTIVE_WIRE_BRIDGE,
                properties = propsFor(CONDUCTIVE_WIRE_BRIDGE).mapColor(MapColor.COLOR_YELLOW).strength(0.6f).sound(SoundType.METAL)
            )
        }),
        solidEntry(HEAVI_WATT_WIRE, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                HEAVI_WATT_WIRE,
                properties = propsFor(HEAVI_WATT_WIRE).mapColor(MapColor.COLOR_ORANGE).noOcclusion().strength(0.8f).sound(SoundType.METAL)
            )
        }),
        solidEntry(HEAVI_WATT_JOINT_PLATE, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                HEAVI_WATT_JOINT_PLATE,
                properties = propsFor(HEAVI_WATT_JOINT_PLATE).mapColor(MapColor.COLOR_ORANGE).strength(1.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(HEAVI_WATT_CONDUCTIVE_WIRE, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                HEAVI_WATT_CONDUCTIVE_WIRE,
                properties = propsFor(HEAVI_WATT_CONDUCTIVE_WIRE).mapColor(MapColor.COLOR_YELLOW).noOcclusion().strength(0.8f).sound(SoundType.METAL)
            )
        }),
        solidEntry(HEAVI_WATT_CONDUCTIVE_JOINT_PLATE, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                HEAVI_WATT_CONDUCTIVE_JOINT_PLATE,
                properties = propsFor(HEAVI_WATT_CONDUCTIVE_JOINT_PLATE).mapColor(MapColor.COLOR_YELLOW).strength(1.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(POWER_TRANSFORMER, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                POWER_TRANSFORMER,
                properties = propsFor(POWER_TRANSFORMER).mapColor(MapColor.COLOR_GRAY).strength(3.0f, 6.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(POWER_TRANSFORMER_SMALL, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                POWER_TRANSFORMER_SMALL,
                properties = propsFor(POWER_TRANSFORMER_SMALL).mapColor(MapColor.COLOR_GRAY).strength(2.5f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(SMART_BATTERY, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                SMART_BATTERY,
                properties = propsFor(SMART_BATTERY).mapColor(MapColor.COLOR_YELLOW).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(JUMBO_BATTERY, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                JUMBO_BATTERY,
                properties = propsFor(JUMBO_BATTERY).mapColor(MapColor.COLOR_YELLOW).strength(2.5f, 5.5f).sound(SoundType.METAL)
            )
        }),
        solidEntry(COAL_GENERATOR, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                COAL_GENERATOR,
                properties = propsFor(COAL_GENERATOR).mapColor(MapColor.COLOR_BLACK).strength(4.0f, 8.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(HYDROGEN_GENERATOR, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                HYDROGEN_GENERATOR,
                properties = propsFor(HYDROGEN_GENERATOR).mapColor(MapColor.COLOR_LIGHT_BLUE).strength(4.0f, 8.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(NATURAL_GAS_GENERATOR, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                NATURAL_GAS_GENERATOR,
                properties = propsFor(NATURAL_GAS_GENERATOR).mapColor(MapColor.COLOR_GREEN).strength(4.0f, 8.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(PETROLEUM_GENERATOR, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                PETROLEUM_GENERATOR,
                properties = propsFor(PETROLEUM_GENERATOR).mapColor(MapColor.COLOR_BROWN).strength(4.0f, 8.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(POWER_CONTROL_STATION, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                POWER_CONTROL_STATION,
                properties = propsFor(POWER_CONTROL_STATION).mapColor(MapColor.COLOR_CYAN).strength(2.5f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(POWER_SWITCH, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                POWER_SWITCH,
                properties = propsFor(POWER_SWITCH).mapColor(MapColor.COLOR_RED).strength(1.5f, 3.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(POWER_SHUTOFF, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                POWER_SHUTOFF,
                properties = propsFor(POWER_SHUTOFF).mapColor(MapColor.COLOR_RED).strength(1.5f, 3.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(POWER_GENERATOR, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                POWER_GENERATOR,
                properties = propsFor(POWER_GENERATOR).mapColor(MapColor.COLOR_ORANGE).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(RESEARCH_DESK, SolidBlockSpec(), { _ ->
            OniSolidBlock(
                RESEARCH_DESK,
                properties = propsFor(RESEARCH_DESK).mapColor(MapColor.COLOR_LIGHT_GRAY).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        }),
        solidEntry(CONSTRUCTION_SITE, SolidBlockSpec(), { _ ->
            ConstructionSiteBlock(
                propsFor(CONSTRUCTION_SITE).mapColor(MapColor.COLOR_LIGHT_GRAY).noOcclusion().strength(1.0f, 2.0f)
                    .sound(SoundType.METAL)
            )
        })
    )

    private val LIQUID_ENTRIES: List<BlockEntry> = listOf(
        entry(WATER, BlockKind.LIQUID, {
            OniLiquidBlock(
                mass = 1000,
                properties = propsFor(WATER).mapColor(MapColor.WATER).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.WET_GRASS)
            )
        }),
        entry(POLLUTED_WATER, BlockKind.LIQUID, {
            OniLiquidBlock(
                mass = 1000,
                properties = propsFor(POLLUTED_WATER).mapColor(MapColor.COLOR_GREEN).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.WET_GRASS)
            )
        }),
        entry(CRUDE_OIL, BlockKind.LIQUID, {
            OniLiquidBlock(
                mass = 870,
                properties = propsFor(CRUDE_OIL).mapColor(MapColor.COLOR_BROWN).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.SLIME_BLOCK)
            )
        }),
        entry(LAVA, BlockKind.LIQUID, {
            OniLiquidBlock(
                mass = 1840,
                properties = propsFor(LAVA).mapColor(MapColor.COLOR_ORANGE).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.SLIME_BLOCK)
            )
        })
    )

    private val GAS_ENTRIES: List<BlockEntry> = listOf(
        entry(OXYGEN_GAS, BlockKind.GAS, {
            OniGasBlock(
                propsFor(OXYGEN_GAS).noCollision().noOcclusion().strength(0.0f).sound(SoundType.WOOL)
            )
        }),
        entry(CARBON_DIOXIDE_GAS, BlockKind.GAS, {
            OniGasBlock(
                propsFor(CARBON_DIOXIDE_GAS).noCollision().noOcclusion().strength(0.0f).sound(SoundType.WOOL)
            )
        }),
        entry(HYDROGEN_GAS, BlockKind.GAS, {
            OniGasBlock(
                propsFor(HYDROGEN_GAS).noCollision().noOcclusion().strength(0.0f).sound(SoundType.WOOL)
            )
        })
    )

    private val ENTRIES: List<BlockEntry> = SOLID_ENTRIES + LIQUID_ENTRIES + GAS_ENTRIES
    private val ENTRIES_BY_ID: Map<String, BlockEntry> = ENTRIES.associateBy { it.id }

    @JvmField
    val SOLID_IDS: List<String> = SOLID_ENTRIES.map { it.id }

    @JvmField
    val GAS_IDS: List<String> = GAS_ENTRIES.map { it.id }

    @JvmField
    val LIQUID_IDS: List<String> = LIQUID_ENTRIES.map { it.id }

    fun entries(): List<BlockEntry> = ENTRIES

    fun idOf(block: Block): String? {
        val key = BuiltInRegistries.BLOCK.getKey(block)
        if (key.namespace != AbstractModBootstrap.MOD_ID) {
            return null
        }
        return key.path
    }

    private val BLOCK_MASS: Map<String, Int> = mapOf(
        REGOLITH to 1000,
        SEDIMENTARY_ROCK to 1840,
        IGNEOUS_ROCK to 1840,
        GRANITE to 1840,
        ABYSSALITE to 500,
        ALGAE to 200,
        POLLUTED_DIRT to 1000
    )

    fun blockMass(id: String): Int {
        return BLOCK_MASS[id] ?: 1
    }

    fun blockDigYield(id: String): Int = blockMass(id).coerceAtLeast(1)

    fun createBlock(id: String): Block {
        val entry = ENTRIES_BY_ID[id] ?: throw IllegalArgumentException("Unknown block id: $id")
        return entry.factory()
    }
}
