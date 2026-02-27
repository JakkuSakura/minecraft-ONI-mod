package mconi.common.block

import mconi.common.AbstractModBootstrap
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

    data class BlockEntry(
        val id: String,
        val block: Block
    )

    private fun blockKey(id: String): ResourceKey<Block> {
        val identifier = Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:$id")
            ?: throw IllegalArgumentException("Invalid block id: $id")
        return ResourceKey.create(Registries.BLOCK, identifier)
    }

    private fun propsFor(id: String): BlockBehaviour.Properties {
        return BlockBehaviour.Properties.of().setId(blockKey(id))
    }

    private fun entry(id: String, block: Block): BlockEntry = BlockEntry(id, block)

    private val SOLID_ENTRIES: List<BlockEntry> = listOf(
        entry(
            REGOLITH,
            OniSolidBlock(
                REGOLITH,
                dropElementId = "mconi:element_regolith",
                propsFor(REGOLITH).mapColor(MapColor.COLOR_BROWN).strength(0.6f).sound(SoundType.GRAVEL)
            )
        ),
        entry(
            SEDIMENTARY_ROCK,
            OniSolidBlock(
                SEDIMENTARY_ROCK,
                dropElementId = "mconi:element_sedimentary_rock",
                propsFor(SEDIMENTARY_ROCK).mapColor(MapColor.COLOR_YELLOW).strength(1.2f, 3.0f).sound(SoundType.STONE)
            )
        ),
        entry(
            IGNEOUS_ROCK,
            OniSolidBlock(
                IGNEOUS_ROCK,
                dropElementId = "mconi:element_igneous_rock",
                propsFor(IGNEOUS_ROCK).mapColor(MapColor.COLOR_GRAY).strength(1.5f, 4.0f).sound(SoundType.STONE)
            )
        ),
        entry(
            GRANITE,
            OniSolidBlock(
                GRANITE,
                dropElementId = "mconi:element_granite",
                propsFor(GRANITE).mapColor(MapColor.COLOR_ORANGE).strength(1.6f, 4.5f).sound(SoundType.STONE)
            )
        ),
        entry(
            ABYSSALITE,
            OniSolidBlock(
                ABYSSALITE,
                dropElementId = "mconi:element_abyssalite",
                propsFor(ABYSSALITE).mapColor(MapColor.COLOR_BLACK).strength(50.0f, 1200.0f).sound(SoundType.STONE)
            )
        ),
        entry(
            ALGAE,
            OniSolidBlock(
                ALGAE,
                dropElementId = "mconi:element_algae",
                propsFor(ALGAE).mapColor(MapColor.COLOR_GREEN).strength(0.4f).sound(SoundType.GRASS)
            )
        ),
        entry(
            POLLUTED_DIRT,
            OniSolidBlock(
                POLLUTED_DIRT,
                dropElementId = "mconi:element_polluted_dirt",
                propsFor(POLLUTED_DIRT).mapColor(MapColor.COLOR_BROWN).strength(0.6f).sound(SoundType.GRAVEL)
            )
        ),
        entry(
            PRINTING_POD,
            PrintingPodBlock(
                PRINTING_POD,
                dropElementId = "mconi:element_refined_metal",
                propsFor(PRINTING_POD).mapColor(MapColor.COLOR_LIGHT_GRAY).strength(3.0f, 6.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            OXYGEN_DIFFUSER,
            OniSolidBlock(
                OXYGEN_DIFFUSER,
                properties = propsFor(OXYGEN_DIFFUSER).mapColor(MapColor.COLOR_LIGHT_BLUE).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            ALGAE_DEOXIDIZER,
            OniSolidBlock(
                ALGAE_DEOXIDIZER,
                properties = propsFor(ALGAE_DEOXIDIZER).mapColor(MapColor.COLOR_GREEN).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            CO2_SCRUBBER,
            OniSolidBlock(
                CO2_SCRUBBER,
                properties = propsFor(CO2_SCRUBBER).mapColor(MapColor.COLOR_GRAY).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            LIQUID_PUMP,
            OniSolidBlock(
                LIQUID_PUMP,
                properties = propsFor(LIQUID_PUMP).mapColor(MapColor.COLOR_CYAN).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            GAS_PUMP,
            OniSolidBlock(
                GAS_PUMP,
                properties = propsFor(GAS_PUMP).mapColor(MapColor.COLOR_LIGHT_GRAY).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            MANUAL_GENERATOR,
            OniSolidBlock(
                MANUAL_GENERATOR,
                properties = propsFor(MANUAL_GENERATOR).mapColor(MapColor.COLOR_ORANGE).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            BATTERY,
            OniSolidBlock(
                BATTERY,
                properties = propsFor(BATTERY).mapColor(MapColor.COLOR_YELLOW).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            POWER_WIRE,
            OniSolidBlock(
                POWER_WIRE,
                properties = propsFor(POWER_WIRE).mapColor(MapColor.COLOR_BROWN).noOcclusion().strength(0.6f).sound(SoundType.METAL)
            )
        ),
        entry(
            WIRE,
            OniSolidBlock(
                WIRE,
                properties = propsFor(WIRE).mapColor(MapColor.COLOR_RED).noOcclusion().strength(0.4f).sound(SoundType.METAL)
            )
        ),
        entry(
            WIRE_BRIDGE,
            OniSolidBlock(
                WIRE_BRIDGE,
                properties = propsFor(WIRE_BRIDGE).mapColor(MapColor.COLOR_RED).strength(0.6f).sound(SoundType.METAL)
            )
        ),
        entry(
            CONDUCTIVE_WIRE,
            OniSolidBlock(
                CONDUCTIVE_WIRE,
                properties = propsFor(CONDUCTIVE_WIRE).mapColor(MapColor.COLOR_YELLOW).noOcclusion().strength(0.4f).sound(SoundType.METAL)
            )
        ),
        entry(
            CONDUCTIVE_WIRE_BRIDGE,
            OniSolidBlock(
                CONDUCTIVE_WIRE_BRIDGE,
                properties = propsFor(CONDUCTIVE_WIRE_BRIDGE).mapColor(MapColor.COLOR_YELLOW).strength(0.6f).sound(SoundType.METAL)
            )
        ),
        entry(
            HEAVI_WATT_WIRE,
            OniSolidBlock(
                HEAVI_WATT_WIRE,
                properties = propsFor(HEAVI_WATT_WIRE).mapColor(MapColor.COLOR_ORANGE).noOcclusion().strength(0.8f).sound(SoundType.METAL)
            )
        ),
        entry(
            HEAVI_WATT_JOINT_PLATE,
            OniSolidBlock(
                HEAVI_WATT_JOINT_PLATE,
                properties = propsFor(HEAVI_WATT_JOINT_PLATE).mapColor(MapColor.COLOR_ORANGE).strength(1.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            HEAVI_WATT_CONDUCTIVE_WIRE,
            OniSolidBlock(
                HEAVI_WATT_CONDUCTIVE_WIRE,
                properties = propsFor(HEAVI_WATT_CONDUCTIVE_WIRE).mapColor(MapColor.COLOR_YELLOW).noOcclusion().strength(0.8f).sound(SoundType.METAL)
            )
        ),
        entry(
            HEAVI_WATT_CONDUCTIVE_JOINT_PLATE,
            OniSolidBlock(
                HEAVI_WATT_CONDUCTIVE_JOINT_PLATE,
                properties = propsFor(HEAVI_WATT_CONDUCTIVE_JOINT_PLATE).mapColor(MapColor.COLOR_YELLOW).strength(1.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            POWER_TRANSFORMER,
            OniSolidBlock(
                POWER_TRANSFORMER,
                properties = propsFor(POWER_TRANSFORMER).mapColor(MapColor.COLOR_GRAY).strength(3.0f, 6.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            POWER_TRANSFORMER_SMALL,
            OniSolidBlock(
                POWER_TRANSFORMER_SMALL,
                properties = propsFor(POWER_TRANSFORMER_SMALL).mapColor(MapColor.COLOR_GRAY).strength(2.5f, 5.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            SMART_BATTERY,
            OniSolidBlock(
                SMART_BATTERY,
                properties = propsFor(SMART_BATTERY).mapColor(MapColor.COLOR_YELLOW).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            JUMBO_BATTERY,
            OniSolidBlock(
                JUMBO_BATTERY,
                properties = propsFor(JUMBO_BATTERY).mapColor(MapColor.COLOR_YELLOW).strength(2.5f, 5.5f).sound(SoundType.METAL)
            )
        ),
        entry(
            COAL_GENERATOR,
            OniSolidBlock(
                COAL_GENERATOR,
                properties = propsFor(COAL_GENERATOR).mapColor(MapColor.COLOR_BLACK).strength(4.0f, 8.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            HYDROGEN_GENERATOR,
            OniSolidBlock(
                HYDROGEN_GENERATOR,
                properties = propsFor(HYDROGEN_GENERATOR).mapColor(MapColor.COLOR_LIGHT_BLUE).strength(4.0f, 8.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            NATURAL_GAS_GENERATOR,
            OniSolidBlock(
                NATURAL_GAS_GENERATOR,
                properties = propsFor(NATURAL_GAS_GENERATOR).mapColor(MapColor.COLOR_GREEN).strength(4.0f, 8.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            PETROLEUM_GENERATOR,
            OniSolidBlock(
                PETROLEUM_GENERATOR,
                properties = propsFor(PETROLEUM_GENERATOR).mapColor(MapColor.COLOR_BROWN).strength(4.0f, 8.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            POWER_CONTROL_STATION,
            OniSolidBlock(
                POWER_CONTROL_STATION,
                properties = propsFor(POWER_CONTROL_STATION).mapColor(MapColor.COLOR_CYAN).strength(2.5f, 5.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            POWER_SWITCH,
            OniSolidBlock(
                POWER_SWITCH,
                properties = propsFor(POWER_SWITCH).mapColor(MapColor.COLOR_RED).strength(1.5f, 3.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            POWER_SHUTOFF,
            OniSolidBlock(
                POWER_SHUTOFF,
                properties = propsFor(POWER_SHUTOFF).mapColor(MapColor.COLOR_RED).strength(1.5f, 3.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            POWER_GENERATOR,
            OniSolidBlock(
                POWER_GENERATOR,
                properties = propsFor(POWER_GENERATOR).mapColor(MapColor.COLOR_ORANGE).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            RESEARCH_DESK,
            OniSolidBlock(
                RESEARCH_DESK,
                properties = propsFor(RESEARCH_DESK).mapColor(MapColor.COLOR_LIGHT_GRAY).strength(2.0f, 5.0f).sound(SoundType.METAL)
            )
        ),
        entry(
            CONSTRUCTION_SITE,
            ConstructionSiteBlock(
                propsFor(CONSTRUCTION_SITE).mapColor(MapColor.COLOR_LIGHT_GRAY).noOcclusion().strength(1.0f, 2.0f)
                    .sound(SoundType.METAL)
            )
        ),
    )

    private val LIQUID_ENTRIES: List<BlockEntry> = listOf(
        entry(
            WATER,
            OniLiquidBlock(
                massKg = 1000,
                properties = propsFor(WATER).mapColor(MapColor.WATER).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.WET_GRASS)
            )
        ),
        entry(
            POLLUTED_WATER,
            OniLiquidBlock(
                massKg = 1000,
                properties = propsFor(POLLUTED_WATER).mapColor(MapColor.COLOR_GREEN).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.WET_GRASS)
            )
        ),
        entry(
            CRUDE_OIL,
            OniLiquidBlock(
                massKg = 870,
                properties = propsFor(CRUDE_OIL).mapColor(MapColor.COLOR_BROWN).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.SLIME_BLOCK)
            )
        ),
        entry(
            LAVA,
            OniLiquidBlock(
                massKg = 1840,
                properties = propsFor(LAVA).mapColor(MapColor.COLOR_ORANGE).noCollision().noOcclusion().strength(0.0f)
                    .sound(SoundType.SLIME_BLOCK)
            )
        )
    )

    private val GAS_ENTRIES: List<BlockEntry> = listOf(
        entry(
            OXYGEN_GAS,
            OniGasBlock(
                propsFor(OXYGEN_GAS).noCollision().noOcclusion().strength(0.0f).sound(SoundType.WOOL)
            )
        ),
        entry(
            CARBON_DIOXIDE_GAS,
            OniGasBlock(
                propsFor(CARBON_DIOXIDE_GAS).noCollision().noOcclusion().strength(0.0f).sound(SoundType.WOOL)
            )
        ),
        entry(
            HYDROGEN_GAS,
            OniGasBlock(
                propsFor(HYDROGEN_GAS).noCollision().noOcclusion().strength(0.0f).sound(SoundType.WOOL)
            )
        )
    )

    private val ENTRIES: List<BlockEntry> = SOLID_ENTRIES + LIQUID_ENTRIES + GAS_ENTRIES
    private val BLOCKS_BY_ID: Map<String, Block> = ENTRIES.associate { it.id to it.block }
    private val IDS_BY_BLOCK: Map<Block, String> = ENTRIES.associate { it.block to it.id }

    @JvmField
    val SOLIDS: List<Block> = SOLID_ENTRIES.map { it.block }

    @JvmField
    val GASES: List<Block> = GAS_ENTRIES.map { it.block }

    @JvmField
    val LIQUIDS: List<Block> = LIQUID_ENTRIES.map { it.block }

    @JvmField
    val ALL: List<Block> = ENTRIES.map { it.block }

    fun entries(): List<BlockEntry> = ENTRIES

    fun idOf(block: Block): String? = IDS_BY_BLOCK[block]

    private val BLOCK_MASS_KG: Map<String, Int> = mapOf(
        REGOLITH to 1000,
        SEDIMENTARY_ROCK to 1840,
        IGNEOUS_ROCK to 1840,
        GRANITE to 1840,
        ABYSSALITE to 500,
        ALGAE to 200,
        POLLUTED_DIRT to 1000
    )

    fun blockMassKg(id: String): Int {
        return BLOCK_MASS_KG[id] ?: 1
    }

    fun blockDigYieldKg(id: String): Int = blockMassKg(id).coerceAtLeast(1)

    fun liquidMassKg(liquidId: String): Int {
        val block = OniBlockLookup.block(liquidId)
        return (block as? OniLiquidBlock)?.massKg ?: 0
    }

    fun createBlock(id: String): Block {
        return BLOCKS_BY_ID[id] ?: throw IllegalArgumentException("Unknown block id: $id")
    }
}
