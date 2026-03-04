package conservecraft.common.item

import conservecraft.common.AbstractModBootstrap
import conservecraft.common.element.OniElements
import conservecraft.common.sim.model.SystemLens
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
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
    const val BOTTLED_METHANE = "bottled_methane"
    const val BOTTLED_STEAM = "bottled_steam"
    const val BOTTLED_CHLORINE = "bottled_chlorine"
    const val BOTTLED_SALT_WATER = "bottled_salt_water"
    const val BOTTLED_BRINE = "bottled_brine"
    const val BOTTLED_ETHANOL = "bottled_ethanol"
    const val BOTTLED_PETROLEUM = "bottled_petroleum"
    const val BOTTLED_MILK = "bottled_milk"
    const val BOTTLED_NATURAL_RESIN = "bottled_natural_resin"
    const val BOTTLED_PHYTO_OIL = "bottled_phyto_oil"
    const val BOTTLED_MOLTEN_GLASS = "bottled_molten_glass"
    const val BOTTLED_SUPER_COOLANT = "bottled_super_coolant"
    const val BOTTLED_VISCO_GEL = "bottled_visco_gel"
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
    const val ELEMENT_DIRT = "element_dirt"
    const val ELEMENT_SAND = "element_sand"
    const val ELEMENT_TOXIC_SAND = "element_toxic_sand"
    const val ELEMENT_SALT = "element_salt"
    const val ELEMENT_TABLE_SALT = "element_table_salt"
    const val ELEMENT_SLIME_MOLD = "element_slime_mold"
    const val ELEMENT_PLANT_FIBER = "element_plant_fiber"
    const val ELEMENT_FABRICATED_WOOD = "element_fabricated_wood"
    const val ELEMENT_WOOD_LOG = "element_wood_log"
    const val ELEMENT_BUILDING_WOOD = "element_building_wood"
    const val ELEMENT_PHOSPHORITE = "element_phosphorite"
    const val ELEMENT_FERTILIZER = "element_fertilizer"
    const val ELEMENT_BLEACH_STONE = "element_bleach_stone"
    const val ELEMENT_CERAMIC = "element_ceramic"
    const val ELEMENT_CARBON = "element_carbon"
    const val ELEMENT_PEAT = "element_peat"
    const val ELEMENT_REFINED_CARBON = "element_refined_carbon"
    const val ELEMENT_LIME = "element_lime"
    const val ELEMENT_FOSSIL = "element_fossil"
    const val ELEMENT_CLAY = "element_clay"
    const val ELEMENT_GOLD_AMALGAM = "element_gold_amalgam"
    const val ELEMENT_GOLD = "element_gold"
    const val ELEMENT_IRON = "element_iron"
    const val ELEMENT_IRON_ORE = "element_iron_ore"
    const val ELEMENT_COPPER = "element_copper"
    const val ELEMENT_COPPER_ORE = "element_copper_ore"
    const val ELEMENT_ALUMINUM = "element_aluminum"
    const val ELEMENT_ALUMINUM_ORE = "element_aluminum_ore"
    const val ELEMENT_COBALTITE = "element_cobaltite"
    const val ELEMENT_COBALT = "element_cobalt"
    const val ELEMENT_TUNGSTEN = "element_tungsten"
    const val ELEMENT_TUNGSTEN_ORE = "element_tungsten_ore"
    const val ELEMENT_STEEL = "element_steel"
    const val ELEMENT_DIAMOND = "element_diamond"
    const val ELEMENT_REFINED_LIPID = "element_refined_lipid"
    const val ELEMENT_SULFUR = "element_sulfur"
    const val ELEMENT_FULLERENE = "element_fullerene"
    const val ELEMENT_POLYPROPYLENE = "element_polypropylene"
    const val ELEMENT_HARD_POLYPROPYLENE = "element_hard_polypropylene"
    const val ELEMENT_TEMP_CONDUCTOR_SOLID = "element_temp_conductor_solid"
    const val ELEMENT_MILK_FAT = "element_milk_fat"
    const val ELEMENT_ISORESIN = "element_isoresin"
    const val ELEMENT_SUPER_INSULATOR = "element_super_insulator"
    const val ELEMENT_NIOBIUM = "element_niobium"
    const val ELEMENT_ENRICHED_URANIUM = "element_enriched_uranium"
    const val ELEMENT_AMBER = "element_amber"
    const val ELEMENT_OXYROCK = "element_oxyrock"
    const val ELEMENT_GRAPHITE = "element_graphite"
    const val ELEMENT_KATAIRITE = "element_katairite"
    const val ELEMENT_ICE_BELLY_POOP = "element_ice_belly_poop"
    const val ELEMENT_EGG_SHELL = "element_egg_shell"
    const val ELEMENT_CRAB_SHELL = "element_crab_shell"
    const val ELEMENT_CRAB_WOOD_SHELL = "element_crab_wood_shell"
    const val ELEMENT_GOLD_BELLY_CROWN = "element_gold_belly_crown"
    const val ELEMENT_GARBAGE_ELECTROBANK = "element_garbage_electrobank"
    const val ELEMENT_SELF_CHARGING_ELECTROBANK = "element_self_charging_electrobank"
    const val ELEMENT_COLD_WHEAT_SEED = "element_cold_wheat_seed"
    const val ELEMENT_SPICE_NUT = "element_spice_nut"
    const val ELEMENT_BEAN_PLANT_SEED = "element_bean_plant_seed"
    const val ELEMENT_DEW_DRIP = "element_dew_drip"
    const val ELEMENT_KELP = "element_kelp"
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
        ELEMENT_REFINED_METAL,
        ELEMENT_DIRT,
        ELEMENT_SAND,
        ELEMENT_TOXIC_SAND,
        ELEMENT_SALT,
        ELEMENT_TABLE_SALT,
        ELEMENT_SLIME_MOLD,
        ELEMENT_PLANT_FIBER,
        ELEMENT_FABRICATED_WOOD,
        ELEMENT_WOOD_LOG,
        ELEMENT_BUILDING_WOOD,
        ELEMENT_PHOSPHORITE,
        ELEMENT_FERTILIZER,
        ELEMENT_BLEACH_STONE,
        ELEMENT_CERAMIC,
        ELEMENT_CARBON,
        ELEMENT_PEAT,
        ELEMENT_REFINED_CARBON,
        ELEMENT_LIME,
        ELEMENT_FOSSIL,
        ELEMENT_CLAY,
        ELEMENT_GOLD_AMALGAM,
        ELEMENT_GOLD,
        ELEMENT_IRON,
        ELEMENT_IRON_ORE,
        ELEMENT_COPPER,
        ELEMENT_COPPER_ORE,
        ELEMENT_ALUMINUM,
        ELEMENT_ALUMINUM_ORE,
        ELEMENT_COBALTITE,
        ELEMENT_COBALT,
        ELEMENT_TUNGSTEN,
        ELEMENT_TUNGSTEN_ORE,
        ELEMENT_STEEL,
        ELEMENT_DIAMOND,
        ELEMENT_REFINED_LIPID,
        ELEMENT_SULFUR,
        ELEMENT_FULLERENE,
        ELEMENT_POLYPROPYLENE,
        ELEMENT_HARD_POLYPROPYLENE,
        ELEMENT_TEMP_CONDUCTOR_SOLID,
        ELEMENT_MILK_FAT,
        ELEMENT_ISORESIN,
        ELEMENT_SUPER_INSULATOR,
        ELEMENT_NIOBIUM,
        ELEMENT_ENRICHED_URANIUM,
        ELEMENT_AMBER,
        ELEMENT_OXYROCK,
        ELEMENT_GRAPHITE,
        ELEMENT_KATAIRITE,
        ELEMENT_ICE_BELLY_POOP,
        ELEMENT_EGG_SHELL,
        ELEMENT_CRAB_SHELL,
        ELEMENT_CRAB_WOOD_SHELL,
        ELEMENT_GOLD_BELLY_CROWN,
        ELEMENT_GARBAGE_ELECTROBANK,
        ELEMENT_SELF_CHARGING_ELECTROBANK,
        ELEMENT_COLD_WHEAT_SEED,
        ELEMENT_SPICE_NUT,
        ELEMENT_BEAN_PLANT_SEED,
        ELEMENT_DEW_DRIP,
        ELEMENT_KELP
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
        BOTTLED_METHANE,
        BOTTLED_STEAM,
        BOTTLED_CHLORINE,
        BOTTLED_SALT_WATER,
        BOTTLED_BRINE,
        BOTTLED_ETHANOL,
        BOTTLED_PETROLEUM,
        BOTTLED_MILK,
        BOTTLED_NATURAL_RESIN,
        BOTTLED_PHYTO_OIL,
        BOTTLED_MOLTEN_GLASS,
        BOTTLED_SUPER_COOLANT,
        BOTTLED_VISCO_GEL,
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
        ELEMENT_DIRT,
        ELEMENT_SAND,
        ELEMENT_TOXIC_SAND,
        ELEMENT_SALT,
        ELEMENT_TABLE_SALT,
        ELEMENT_SLIME_MOLD,
        ELEMENT_PLANT_FIBER,
        ELEMENT_FABRICATED_WOOD,
        ELEMENT_WOOD_LOG,
        ELEMENT_BUILDING_WOOD,
        ELEMENT_PHOSPHORITE,
        ELEMENT_FERTILIZER,
        ELEMENT_BLEACH_STONE,
        ELEMENT_CERAMIC,
        ELEMENT_CARBON,
        ELEMENT_PEAT,
        ELEMENT_REFINED_CARBON,
        ELEMENT_LIME,
        ELEMENT_FOSSIL,
        ELEMENT_CLAY,
        ELEMENT_GOLD_AMALGAM,
        ELEMENT_GOLD,
        ELEMENT_IRON,
        ELEMENT_IRON_ORE,
        ELEMENT_COPPER,
        ELEMENT_COPPER_ORE,
        ELEMENT_ALUMINUM,
        ELEMENT_ALUMINUM_ORE,
        ELEMENT_COBALTITE,
        ELEMENT_COBALT,
        ELEMENT_TUNGSTEN,
        ELEMENT_TUNGSTEN_ORE,
        ELEMENT_STEEL,
        ELEMENT_DIAMOND,
        ELEMENT_REFINED_LIPID,
        ELEMENT_SULFUR,
        ELEMENT_FULLERENE,
        ELEMENT_POLYPROPYLENE,
        ELEMENT_HARD_POLYPROPYLENE,
        ELEMENT_TEMP_CONDUCTOR_SOLID,
        ELEMENT_MILK_FAT,
        ELEMENT_ISORESIN,
        ELEMENT_SUPER_INSULATOR,
        ELEMENT_NIOBIUM,
        ELEMENT_ENRICHED_URANIUM,
        ELEMENT_AMBER,
        ELEMENT_OXYROCK,
        ELEMENT_GRAPHITE,
        ELEMENT_KATAIRITE,
        ELEMENT_ICE_BELLY_POOP,
        ELEMENT_EGG_SHELL,
        ELEMENT_CRAB_SHELL,
        ELEMENT_CRAB_WOOD_SHELL,
        ELEMENT_GOLD_BELLY_CROWN,
        ELEMENT_GARBAGE_ELECTROBANK,
        ELEMENT_SELF_CHARGING_ELECTROBANK,
        ELEMENT_COLD_WHEAT_SEED,
        ELEMENT_SPICE_NUT,
        ELEMENT_BEAN_PLANT_SEED,
        ELEMENT_DEW_DRIP,
        ELEMENT_KELP,
        POWER_STATION_TOOLS
    )

    private val itemSuppliers: MutableMap<String, () -> Item> = LinkedHashMap()
    private val itemSpecsByRegistryId: MutableMap<String, OniItemSpec> = LinkedHashMap()
    private val itemSpecsByItem: MutableMap<Item, OniItemSpec> = LinkedHashMap()
    private val glassesItemPaths: EnumMap<SystemLens, String> = EnumMap(SystemLens::class.java)
    private val unimplementedEntries: List<UnimplementedItem> = listOf(
        UnimplementedItem(1, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "PILLS.PLACEBO"),
        UnimplementedItem(2, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "PILLS.BASICBOOSTER"),
        UnimplementedItem(3, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "PILLS.INTERMEDIATEBOOSTER"),
        UnimplementedItem(4, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "PILLS.ANTIHISTAMINE"),
        UnimplementedItem(5, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "PILLS.BASICCURE"),
        UnimplementedItem(6, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "PILLS.INTERMEDIATECURE"),
        UnimplementedItem(7, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "PILLS.ADVANCEDCURE"),
        UnimplementedItem(8, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "PILLS.BASICRADPILL"),
        UnimplementedItem(9, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "PILLS.INTERMEDIATERADPILL"),
        UnimplementedItem(10, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "LUBRICATIONSTICK"),
        UnimplementedItem(11, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "TALLOWLUBRICATIONSTICK"),
        UnimplementedItem(12, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "BIONIC_BOOSTERS.BOOSTER_DIG1"),
        UnimplementedItem(13, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "BIONIC_BOOSTERS.BOOSTER_DIG2"),
        UnimplementedItem(14, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "BIONIC_BOOSTERS.BOOSTER_CONSTRUCT1"),
        UnimplementedItem(15, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "BIONIC_BOOSTERS.BOOSTER_FARM1"),
        UnimplementedItem(16, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "BIONIC_BOOSTERS.BOOSTER_RANCH1"),
        UnimplementedItem(17, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "BIONIC_BOOSTERS.BOOSTER_COOK1"),
        UnimplementedItem(18, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "BIONIC_BOOSTERS.BOOSTER_ART1"),
        UnimplementedItem(19, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "BIONIC_BOOSTERS.BOOSTER_RESEARCH1"),
        UnimplementedItem(20, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "BIONIC_BOOSTERS.BOOSTER_RESEARCH2"),
        UnimplementedItem(21, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "BIONIC_BOOSTERS.BOOSTER_RESEARCH3"),
        UnimplementedItem(22, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "BIONIC_BOOSTERS.BOOSTER_PILOT1"),
        UnimplementedItem(23, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "BIONIC_BOOSTERS.BOOSTER_PILOTVANILLA1"),
        UnimplementedItem(24, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "BIONIC_BOOSTERS.BOOSTER_SUITS1"),
        UnimplementedItem(25, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "BIONIC_BOOSTERS.BOOSTER_CARRY1"),
        UnimplementedItem(26, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "BIONIC_BOOSTERS.BOOSTER_OP1"),
        UnimplementedItem(27, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "BIONIC_BOOSTERS.BOOSTER_OP2"),
        UnimplementedItem(28, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "BIONIC_BOOSTERS.BOOSTER_MEDICINE1"),
        UnimplementedItem(29, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "BIONIC_BOOSTERS.BOOSTER_TIDY1"),
        UnimplementedItem(30, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.FOODSPLAT"),
        UnimplementedItem(31, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.BURGER.DEHYDRATED"),
        UnimplementedItem(32, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.BURGER"),
        UnimplementedItem(33, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.FIELDRATION"),
        UnimplementedItem(34, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.MUSHBAR"),
        UnimplementedItem(35, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.MUSHROOMWRAP.DEHYDRATED"),
        UnimplementedItem(36, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.MUSHROOMWRAP"),
        UnimplementedItem(37, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.MICROWAVEDLETTUCE"),
        UnimplementedItem(38, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.GAMMAMUSH"),
        UnimplementedItem(39, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.FRUITCAKE"),
        UnimplementedItem(40, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.POPCORN"),
        UnimplementedItem(41, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.SUSHI"),
        UnimplementedItem(42, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.HATCHEGG"),
        UnimplementedItem(43, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.DRECKOEGG"),
        UnimplementedItem(44, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.LIGHTBUGEGG"),
        UnimplementedItem(45, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.LETTUCE"),
        UnimplementedItem(46, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.PASTA"),
        UnimplementedItem(47, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.PANCAKES"),
        UnimplementedItem(48, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.OILFLOATEREGG"),
        UnimplementedItem(49, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.PUFTEGG"),
        UnimplementedItem(50, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.PREHISTORICPACUFILLET"),
        UnimplementedItem(51, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.FISHMEAT"),
        UnimplementedItem(52, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.MEAT"),
        UnimplementedItem(53, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.DINOSAURMEAT"),
        UnimplementedItem(54, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.SMOKEDDINOSAURMEAT"),
        UnimplementedItem(55, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.SMOKEDFISH"),
        UnimplementedItem(56, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.SMOKEDVEGETABLES"),
        UnimplementedItem(57, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.PLANTMEAT"),
        UnimplementedItem(58, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.SHELLFISHMEAT"),
        UnimplementedItem(59, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.MUSHROOM"),
        UnimplementedItem(60, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.COOKEDFISH"),
        UnimplementedItem(61, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.COOKEDMEAT"),
        UnimplementedItem(62, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.FRIESCARROT"),
        UnimplementedItem(63, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.DEEPFRIEDFISH"),
        UnimplementedItem(64, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.DEEPFRIEDSHELLFISH"),
        UnimplementedItem(65, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.DEEPFRIEDMEAT"),
        UnimplementedItem(66, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.DEEPFRIEDNOSH"),
        UnimplementedItem(67, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.PICKLEDMEAL"),
        UnimplementedItem(68, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.FRIEDMUSHBAR"),
        UnimplementedItem(69, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.RAWEGG"),
        UnimplementedItem(70, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.COOKEDEGG"),
        UnimplementedItem(71, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.FRIEDMUSHROOM"),
        UnimplementedItem(72, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.COOKEDPIKEAPPLE"),
        UnimplementedItem(73, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.PRICKLEFRUIT"),
        UnimplementedItem(74, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.GRILLEDPRICKLEFRUIT"),
        UnimplementedItem(75, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.SWAMPFRUIT"),
        UnimplementedItem(76, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.SWAMPDELIGHTS"),
        UnimplementedItem(77, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.WORMBASICFRUIT"),
        UnimplementedItem(78, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.WORMBASICFOOD"),
        UnimplementedItem(79, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.WORMSUPERFRUIT"),
        UnimplementedItem(80, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.WORMSUPERFOOD"),
        UnimplementedItem(81, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.VINEFRUITJAM"),
        UnimplementedItem(82, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.BERRYPIE.DEHYDRATED"),
        UnimplementedItem(83, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.BERRYPIE"),
        UnimplementedItem(84, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.COLDWHEATBREAD"),
        UnimplementedItem(85, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.BEAN"),
        UnimplementedItem(86, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.SPICENUT"),
        UnimplementedItem(87, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.VINEFRUIT"),
        UnimplementedItem(88, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.SPICEBREAD.DEHYDRATED"),
        UnimplementedItem(89, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.SPICEBREAD"),
        UnimplementedItem(90, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.SURFANDTURF.DEHYDRATED"),
        UnimplementedItem(91, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.SURFANDTURF"),
        UnimplementedItem(92, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.TOFU"),
        UnimplementedItem(93, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.SPICYTOFU.DEHYDRATED"),
        UnimplementedItem(94, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.SPICYTOFU"),
        UnimplementedItem(95, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.CURRY.DEHYDRATED"),
        UnimplementedItem(96, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.CURRY"),
        UnimplementedItem(97, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.SALSA.DEHYDRATED"),
        UnimplementedItem(98, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.SALSA"),
        UnimplementedItem(99, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.HARDSKINBERRY"),
        UnimplementedItem(100, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.CARROT"),
        UnimplementedItem(101, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.FERNFOOD"),
        UnimplementedItem(102, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.PEMMICAN"),
        UnimplementedItem(103, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.BASICPLANTFOOD"),
        UnimplementedItem(104, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.BASICPLANTBAR"),
        UnimplementedItem(105, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.BASICFORAGEPLANT"),
        UnimplementedItem(106, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.FORESTFORAGEPLANT"),
        UnimplementedItem(107, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.SWAMPFORAGEPLANT"),
        UnimplementedItem(108, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.ICECAVESFORAGEPLANT"),
        UnimplementedItem(109, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.ROTPILE"),
        UnimplementedItem(110, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.COLDWHEATSEED"),
        UnimplementedItem(111, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.BEANPLANTSEED"),
        UnimplementedItem(112, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.QUICHE.DEHYDRATED"),
        UnimplementedItem(113, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.QUICHE"),
        UnimplementedItem(114, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.GARDENFOODPLANTFOOD"),
        UnimplementedItem(115, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.GARDENFORAGEPLANT"),
        UnimplementedItem(116, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.BUTTERFLYPLANTSEED"),
        UnimplementedItem(117, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "FOOD.BUTTERFLYFOOD"),
        UnimplementedItem(118, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INGREDIENTS.SWAMPLILYFLOWER"),
        UnimplementedItem(119, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INGREDIENTS.GINGER"),
        UnimplementedItem(120, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INGREDIENTS.KELP"),
        UnimplementedItem(121, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.ELECTROBANK_URANIUM_ORE"),
        UnimplementedItem(122, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.ELECTROBANK_METAL_ORE"),
        UnimplementedItem(123, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.ELECTROBANK_SELFCHARGING"),
        UnimplementedItem(124, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.ELECTROBANK"),
        UnimplementedItem(125, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.ELECTROBANK_EMPTY"),
        UnimplementedItem(126, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.ELECTROBANK_GARBAGE"),
        UnimplementedItem(127, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.FUEL_BRICK"),
        UnimplementedItem(128, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.BASIC_FABRIC"),
        UnimplementedItem(129, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.PLANT_FIBER"),
        UnimplementedItem(130, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.FEATHER_FABRIC"),
        UnimplementedItem(131, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.DEWDRIP"),
        UnimplementedItem(132, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.TRAP_PARTS"),
        UnimplementedItem(133, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.POWER_STATION_TOOLS"),
        UnimplementedItem(134, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.FARM_STATION_TOOLS"),
        UnimplementedItem(135, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.MACHINE_PARTS"),
        UnimplementedItem(136, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.RESEARCH_DATABANK"),
        UnimplementedItem(137, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.ORBITAL_RESEARCH_DATABANK"),
        UnimplementedItem(138, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.EGG_SHELL"),
        UnimplementedItem(139, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.GOLD_BELLY_CROWN"),
        UnimplementedItem(140, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.CRAB_SHELL.VARIANT_WOOD"),
        UnimplementedItem(141, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.CRAB_SHELL"),
        UnimplementedItem(142, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.BABY_CRAB_SHELL.VARIANT_WOOD"),
        UnimplementedItem(143, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.BABY_CRAB_SHELL"),
        UnimplementedItem(144, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.WOOD"),
        UnimplementedItem(145, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.GENE_SHUFFLER_RECHARGE"),
        UnimplementedItem(146, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.TABLE_SALT"),
        UnimplementedItem(147, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.REFINED_SUGAR"),
        UnimplementedItem(148, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "INDUSTRIAL_PRODUCTS.ICE_BELLY_POOP"),
        UnimplementedItem(149, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "CARGO_CAPSULE"),
        UnimplementedItem(150, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "RAILGUNPAYLOAD"),
        UnimplementedItem(151, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "MISSILE_BASIC"),
        UnimplementedItem(152, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "MISSILE_LONGRANGE_VANILLADLC4"),
        UnimplementedItem(153, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "MISSILE_LONGRANGE"),
        UnimplementedItem(154, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "DEBRISPAYLOAD"),
        UnimplementedItem(155, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "RADIATION.HIGHENERGYPARITCLE"),
        UnimplementedItem(156, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "DREAMJOURNAL"),
        UnimplementedItem(157, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "DEHYDRATEDFOODPACKAGE"),
        UnimplementedItem(158, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "SPICES.MACHINERY_SPICE"),
        UnimplementedItem(159, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "SPICES.PILOTING_SPICE"),
        UnimplementedItem(160, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "SPICES.PRESERVING_SPICE"),
        UnimplementedItem(161, OniItemSource.UNIMPLEMENTED_STRINGS_ITEMS, "SPICES.STRENGTH_SPICE"),
        UnimplementedItem(162, OniItemSource.UNIMPLEMENTED_PREFAB, "FoodSplat"),
        UnimplementedItem(163, OniItemSource.UNIMPLEMENTED_PREFAB, "HighEnergyParticle"),
        UnimplementedItem(164, OniItemSource.UNIMPLEMENTED_PREFAB, "OrbitalResearchDatabank"),
        UnimplementedItem(165, OniItemSource.UNIMPLEMENTED_PREFAB, "HeatCube"),
        UnimplementedItem(166, OniItemSource.UNIMPLEMENTED_PREFAB, "GardenForagePlant"),
        UnimplementedItem(167, OniItemSource.UNIMPLEMENTED_PREFAB, "SmokedFish"),
        UnimplementedItem(168, OniItemSource.UNIMPLEMENTED_PREFAB, "IceCavesForagePlant"),
        UnimplementedItem(169, OniItemSource.UNIMPLEMENTED_PREFAB, "ResearchDatabank"),
        UnimplementedItem(170, OniItemSource.UNIMPLEMENTED_PREFAB, "FabricatedWood"),
        UnimplementedItem(171, OniItemSource.UNIMPLEMENTED_PREFAB, "ManualGenerator"),
        UnimplementedItem(172, OniItemSource.UNIMPLEMENTED_PREFAB, "WaterCooler"),
        UnimplementedItem(173, OniItemSource.UNIMPLEMENTED_PREFAB, "Bed"),
        UnimplementedItem(174, OniItemSource.UNIMPLEMENTED_PREFAB, "PlantFiber"),
        UnimplementedItem(175, OniItemSource.UNIMPLEMENTED_PREFAB, "PowerStationTools"),
        UnimplementedItem(176, OniItemSource.UNIMPLEMENTED_PREFAB, "BasicPlantFood"),
        UnimplementedItem(177, OniItemSource.UNIMPLEMENTED_PREFAB, "MissileBasic"),
        UnimplementedItem(178, OniItemSource.UNIMPLEMENTED_PREFAB, "DeepFriedNosh"),
        UnimplementedItem(179, OniItemSource.UNIMPLEMENTED_PREFAB, "CrabShell"),
        UnimplementedItem(180, OniItemSource.UNIMPLEMENTED_PREFAB, "keepsake_geothermalplant"),
        UnimplementedItem(181, OniItemSource.UNIMPLEMENTED_PREFAB, "Electrobank"),
        UnimplementedItem(182, OniItemSource.UNIMPLEMENTED_PREFAB, "EmptyElectrobank"),
        UnimplementedItem(183, OniItemSource.UNIMPLEMENTED_PREFAB, "SmokedVegetables"),
        UnimplementedItem(184, OniItemSource.UNIMPLEMENTED_PREFAB, "FriesCarrot"),
        UnimplementedItem(185, OniItemSource.UNIMPLEMENTED_PREFAB, "EggShell"),
        UnimplementedItem(186, OniItemSource.UNIMPLEMENTED_PREFAB, "BalloonStand"),
        UnimplementedItem(187, OniItemSource.UNIMPLEMENTED_PREFAB, "Meter"),
        UnimplementedItem(188, OniItemSource.UNIMPLEMENTED_PREFAB, "FriedMushroom"),
        UnimplementedItem(189, OniItemSource.UNIMPLEMENTED_PREFAB, "DebrisPayload"),
        UnimplementedItem(190, OniItemSource.UNIMPLEMENTED_PREFAB, "Atmo_Suit"),
        UnimplementedItem(191, OniItemSource.UNIMPLEMENTED_PREFAB, "SelfChargingElectrobank"),
        UnimplementedItem(192, OniItemSource.UNIMPLEMENTED_PREFAB, "SweepBot"),
        UnimplementedItem(193, OniItemSource.UNIMPLEMENTED_PREFAB, "BerryPie"),
        UnimplementedItem(194, OniItemSource.UNIMPLEMENTED_PREFAB, "IntermediateRadPill"),
        UnimplementedItem(195, OniItemSource.UNIMPLEMENTED_PREFAB, "PrehistoricPacuFillet"),
        UnimplementedItem(196, OniItemSource.UNIMPLEMENTED_PREFAB, "GasCargoBay"),
        UnimplementedItem(197, OniItemSource.UNIMPLEMENTED_PREFAB, "LiquidCargoBay"),
        UnimplementedItem(198, OniItemSource.UNIMPLEMENTED_PREFAB, "CargoBay"),
        UnimplementedItem(199, OniItemSource.UNIMPLEMENTED_PREFAB, "SpecialCargoBay"),
        UnimplementedItem(200, OniItemSource.UNIMPLEMENTED_PREFAB, "SpecialCargoBayCluster"),
        UnimplementedItem(201, OniItemSource.UNIMPLEMENTED_PREFAB, "BasicRadPill"),
        UnimplementedItem(202, OniItemSource.UNIMPLEMENTED_PREFAB, "SpicyTofu"),
        UnimplementedItem(203, OniItemSource.UNIMPLEMENTED_PREFAB, "GeneShufflerRecharge"),
        UnimplementedItem(204, OniItemSource.UNIMPLEMENTED_PREFAB, "BeeHive"),
        UnimplementedItem(205, OniItemSource.UNIMPLEMENTED_PREFAB, "SmokedDinosaurMeat"),
        UnimplementedItem(206, OniItemSource.UNIMPLEMENTED_PREFAB, "ShellfishMeat"),
        UnimplementedItem(207, OniItemSource.UNIMPLEMENTED_PREFAB, "CrabWoodShell"),
        UnimplementedItem(208, OniItemSource.UNIMPLEMENTED_PREFAB, "CookedFish"),
        UnimplementedItem(209, OniItemSource.UNIMPLEMENTED_PREFAB, "GardenFoodPlantFood"),
        UnimplementedItem(210, OniItemSource.UNIMPLEMENTED_PREFAB, "LargeImpactor"),
        UnimplementedItem(211, OniItemSource.UNIMPLEMENTED_PREFAB, "Antihistamine"),
        UnimplementedItem(212, OniItemSource.UNIMPLEMENTED_PREFAB, "Clustercraft"),
        UnimplementedItem(213, OniItemSource.UNIMPLEMENTED_PREFAB, "MachineParts"),
        UnimplementedItem(214, OniItemSource.UNIMPLEMENTED_PREFAB, "DeepFriedFish"),
        UnimplementedItem(215, OniItemSource.UNIMPLEMENTED_PREFAB, "GoldBellyCrown"),
        UnimplementedItem(216, OniItemSource.UNIMPLEMENTED_PREFAB, "DeepFriedShellfish"),
        UnimplementedItem(217, OniItemSource.UNIMPLEMENTED_PREFAB, "SwampForagePlant"),
        UnimplementedItem(218, OniItemSource.UNIMPLEMENTED_PREFAB, "Asteroid"),
        UnimplementedItem(219, OniItemSource.UNIMPLEMENTED_PREFAB, "Oxygen_Mask"),
        UnimplementedItem(220, OniItemSource.UNIMPLEMENTED_PREFAB, "BasicCure"),
        UnimplementedItem(221, OniItemSource.UNIMPLEMENTED_PREFAB, "IntermediateCure"),
        UnimplementedItem(222, OniItemSource.UNIMPLEMENTED_PREFAB, "BasicPlantBar"),
        UnimplementedItem(223, OniItemSource.UNIMPLEMENTED_PREFAB, "StickerBomb"),
        UnimplementedItem(224, OniItemSource.UNIMPLEMENTED_PREFAB, "EscapePod"),
        UnimplementedItem(225, OniItemSource.UNIMPLEMENTED_PREFAB, "ForestTreeSeed"),
        UnimplementedItem(226, OniItemSource.UNIMPLEMENTED_PREFAB, "BasicForagePlant"),
        UnimplementedItem(227, OniItemSource.UNIMPLEMENTED_PREFAB, "PickledMeal"),
        UnimplementedItem(228, OniItemSource.UNIMPLEMENTED_PREFAB, "AdvancedCure"),
        UnimplementedItem(229, OniItemSource.UNIMPLEMENTED_PREFAB, "ShearingStation"),
        UnimplementedItem(230, OniItemSource.UNIMPLEMENTED_PREFAB, "MilkingStation"),
        UnimplementedItem(231, OniItemSource.UNIMPLEMENTED_PREFAB, "fx_dig_splash"),
        UnimplementedItem(232, OniItemSource.UNIMPLEMENTED_PREFAB, "GrilledPrickleFruit"),
        UnimplementedItem(233, OniItemSource.UNIMPLEMENTED_PREFAB, "MushBar"),
        UnimplementedItem(234, OniItemSource.UNIMPLEMENTED_PREFAB, "Hatch"),
        UnimplementedItem(235, OniItemSource.UNIMPLEMENTED_PREFAB, "HatchEgg"),
        UnimplementedItem(236, OniItemSource.UNIMPLEMENTED_PREFAB, "Funky_Vest"),
        UnimplementedItem(237, OniItemSource.UNIMPLEMENTED_PREFAB, "PrickleFlower"),
        UnimplementedItem(238, OniItemSource.UNIMPLEMENTED_PREFAB, "PrickleFlowerSeed"),
        UnimplementedItem(239, OniItemSource.UNIMPLEMENTED_PREFAB, "FruitCake"),
        UnimplementedItem(240, OniItemSource.UNIMPLEMENTED_PREFAB, "ForestForagePlant"),
        UnimplementedItem(241, OniItemSource.UNIMPLEMENTED_PREFAB, "StarmapHexCellInventory"),
        UnimplementedItem(242, OniItemSource.UNIMPLEMENTED_PREFAB, "HardSkinBerry"),
        UnimplementedItem(243, OniItemSource.UNIMPLEMENTED_PREFAB, "FishFeederBot"),
        UnimplementedItem(244, OniItemSource.UNIMPLEMENTED_PREFAB, "DinosaurMeat"),
        UnimplementedItem(245, OniItemSource.UNIMPLEMENTED_PREFAB, "Quiche"),
        UnimplementedItem(246, OniItemSource.UNIMPLEMENTED_PREFAB, "Curry"),
        UnimplementedItem(247, OniItemSource.UNIMPLEMENTED_PREFAB, "DisposableElectrobank_RawMetal"),
        UnimplementedItem(248, OniItemSource.UNIMPLEMENTED_PREFAB, "LubricationStick"),
        UnimplementedItem(249, OniItemSource.UNIMPLEMENTED_PREFAB, "keepsake_largeimpactor"),
        UnimplementedItem(250, OniItemSource.UNIMPLEMENTED_PREFAB, "MushroomWrap"),
        UnimplementedItem(251, OniItemSource.UNIMPLEMENTED_PREFAB, "WormSuperFood"),
        UnimplementedItem(252, OniItemSource.UNIMPLEMENTED_PREFAB, "Lettuce"),
        UnimplementedItem(253, OniItemSource.UNIMPLEMENTED_PREFAB, "EquippableBalloon"),
        UnimplementedItem(254, OniItemSource.UNIMPLEMENTED_PREFAB, "TallowLubricationStick"),
        UnimplementedItem(255, OniItemSource.UNIMPLEMENTED_PREFAB, "IntermediateBooster"),
        UnimplementedItem(256, OniItemSource.UNIMPLEMENTED_PREFAB, "SwampDelights"),
        UnimplementedItem(257, OniItemSource.UNIMPLEMENTED_PREFAB, "RawEgg"),
        UnimplementedItem(258, OniItemSource.UNIMPLEMENTED_PREFAB, "SpaceTree"),
        UnimplementedItem(259, OniItemSource.UNIMPLEMENTED_PREFAB, "PlantMeat"),
        UnimplementedItem(260, OniItemSource.UNIMPLEMENTED_PREFAB, "Meat"),
        UnimplementedItem(261, OniItemSource.UNIMPLEMENTED_PREFAB, "Pancakes"),
        UnimplementedItem(262, OniItemSource.UNIMPLEMENTED_PREFAB, "ClusterMapLongRangeMissile"),
        UnimplementedItem(263, OniItemSource.UNIMPLEMENTED_PREFAB, "WormBasicFruit"),
        UnimplementedItem(264, OniItemSource.UNIMPLEMENTED_PREFAB, "PinkRockCarved"),
        UnimplementedItem(265, OniItemSource.UNIMPLEMENTED_PREFAB, "GammaMush"),
        UnimplementedItem(266, OniItemSource.UNIMPLEMENTED_PREFAB, "MissileLongRange"),
        UnimplementedItem(267, OniItemSource.UNIMPLEMENTED_PREFAB, "FieldRation"),
        UnimplementedItem(268, OniItemSource.UNIMPLEMENTED_PREFAB, "ButterflyFood"),
        UnimplementedItem(269, OniItemSource.UNIMPLEMENTED_PREFAB, "TelescopeTarget"),
        UnimplementedItem(270, OniItemSource.UNIMPLEMENTED_PREFAB, "CraftingTable"),
        UnimplementedItem(271, OniItemSource.UNIMPLEMENTED_PREFAB, "RailGunPayload"),
        UnimplementedItem(272, OniItemSource.UNIMPLEMENTED_PREFAB, "Burger"),
        UnimplementedItem(273, OniItemSource.UNIMPLEMENTED_PREFAB, "WormBasicFood"),
        UnimplementedItem(274, OniItemSource.UNIMPLEMENTED_PREFAB, "FarmStationTools"),
        UnimplementedItem(275, OniItemSource.UNIMPLEMENTED_PREFAB, "ColdWheatBread"),
        UnimplementedItem(276, OniItemSource.UNIMPLEMENTED_PREFAB, "CookedPikeapple"),
        UnimplementedItem(277, OniItemSource.UNIMPLEMENTED_PREFAB, "WormSuperFruit"),
        UnimplementedItem(278, OniItemSource.UNIMPLEMENTED_PREFAB, "MonumentBottom"),
        UnimplementedItem(279, OniItemSource.UNIMPLEMENTED_PREFAB, "BasicBooster"),
        UnimplementedItem(280, OniItemSource.UNIMPLEMENTED_PREFAB, "Salsa"),
        UnimplementedItem(281, OniItemSource.UNIMPLEMENTED_PREFAB, "SurfAndTurf"),
        UnimplementedItem(282, OniItemSource.UNIMPLEMENTED_PREFAB, "Pemmican"),
        UnimplementedItem(283, OniItemSource.UNIMPLEMENTED_PREFAB, "CookedMeat"),
        UnimplementedItem(284, OniItemSource.UNIMPLEMENTED_PREFAB, "DeepFriedMeat"),
        UnimplementedItem(285, OniItemSource.UNIMPLEMENTED_PREFAB, "SpiceBread"),
        UnimplementedItem(286, OniItemSource.UNIMPLEMENTED_PREFAB, "FriedMushBar"),
        UnimplementedItem(287, OniItemSource.UNIMPLEMENTED_PREFAB, "GarbageElectrobank"),
        UnimplementedItem(288, OniItemSource.UNIMPLEMENTED_PREFAB, "keepsake_"),
        UnimplementedItem(289, OniItemSource.UNIMPLEMENTED_PREFAB, "GasGrassHarvested"),
        UnimplementedItem(290, OniItemSource.UNIMPLEMENTED_PREFAB, "IceBellyPoop"),
        UnimplementedItem(291, OniItemSource.UNIMPLEMENTED_PREFAB, "Tofu"),
        UnimplementedItem(292, OniItemSource.UNIMPLEMENTED_PREFAB, "CookedEgg"),
        UnimplementedItem(293, OniItemSource.UNIMPLEMENTED_PREFAB, "FishMeat"),
        UnimplementedItem(294, OniItemSource.UNIMPLEMENTED_PREFAB, "artifact_"),
    )

    private val coreSpecs: List<OniItemSpec>
    private val unimplementedSpecs: List<OniItemSpec>

    init {
        glassesItemPaths[SystemLens.ATMOSPHERE] = "atmosphere_glasses"
        glassesItemPaths[SystemLens.LIQUID] = "liquid_glasses"
        glassesItemPaths[SystemLens.THERMAL] = "thermal_glasses"
        glassesItemPaths[SystemLens.GAS] = "oxygen_glasses"
        glassesItemPaths[SystemLens.POWER] = "power_glasses"
        glassesItemPaths[SystemLens.STRESS] = "stress_glasses"
        glassesItemPaths[SystemLens.RESEARCH] = "research_glasses"
        glassesItemPaths[SystemLens.CONSTRUCTION] = "construction_glasses"

        coreSpecs = buildSpecs {
            source(OniItemSource.CORE)
            item(BOTTLED_OXYGEN) {
                registryId(modId(BOTTLED_OXYGEN))
                mass(1.0)
                temperatureK(295.0)
            }
            item(BOTTLED_CO2) {
                registryId(modId(BOTTLED_CO2))
                mass(1.0)
                temperatureK(295.0)
            }
            item(BOTTLED_HYDROGEN) {
                registryId(modId(BOTTLED_HYDROGEN))
                mass(1.0)
                temperatureK(295.0)
            }
            item(BOTTLED_METHANE) {
                registryId(modId(BOTTLED_METHANE))
                mass(1.0)
                temperatureK(295.0)
            }
            item(BOTTLED_STEAM) {
                registryId(modId(BOTTLED_STEAM))
                mass(1.0)
                temperatureK(373.15)
            }
            item(BOTTLED_CHLORINE) {
                registryId(modId(BOTTLED_CHLORINE))
                mass(1.0)
                temperatureK(295.0)
            }
            for (spec in OniElements.LIQUID_SPECS) {
                item(spec.bottledItemId) {
                    registryId(modId(spec.bottledItemId))
                    mass(spec.bottledMass())
                    temperatureK(spec.bottledTemperatureK())
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
                mass(1.0)
            }
            item(ELEMENT_SEDIMENTARY_ROCK) {
                registryId(modId(ELEMENT_SEDIMENTARY_ROCK))
                mass(1.0)
            }
            item(ELEMENT_IGNEOUS_ROCK) {
                registryId(modId(ELEMENT_IGNEOUS_ROCK))
                mass(1.0)
            }
            item(ELEMENT_GRANITE) {
                registryId(modId(ELEMENT_GRANITE))
                mass(1.0)
            }
            item(ELEMENT_ABYSSALITE) {
                registryId(modId(ELEMENT_ABYSSALITE))
                mass(1.0)
            }
            item(ELEMENT_ALGAE) {
                registryId(modId(ELEMENT_ALGAE))
                mass(1.0)
            }
            item(ELEMENT_POLLUTED_DIRT) {
                registryId(modId(ELEMENT_POLLUTED_DIRT))
                mass(1.0)
            }
            item(ELEMENT_METAL_ORE) {
                registryId(modId(ELEMENT_METAL_ORE))
                mass(1.0)
            }
            item(ELEMENT_REFINED_METAL) {
                registryId(modId(ELEMENT_REFINED_METAL))
                mass(1.0)
            }
            val extraElementItems = listOf(
                ELEMENT_DIRT,
                ELEMENT_SAND,
                ELEMENT_TOXIC_SAND,
                ELEMENT_SALT,
                ELEMENT_TABLE_SALT,
                ELEMENT_SLIME_MOLD,
                ELEMENT_PLANT_FIBER,
                ELEMENT_FABRICATED_WOOD,
                ELEMENT_WOOD_LOG,
                ELEMENT_BUILDING_WOOD,
                ELEMENT_PHOSPHORITE,
                ELEMENT_FERTILIZER,
                ELEMENT_BLEACH_STONE,
                ELEMENT_CERAMIC,
                ELEMENT_CARBON,
                ELEMENT_PEAT,
                ELEMENT_REFINED_CARBON,
                ELEMENT_LIME,
                ELEMENT_FOSSIL,
                ELEMENT_CLAY,
                ELEMENT_GOLD_AMALGAM,
                ELEMENT_GOLD,
                ELEMENT_IRON,
                ELEMENT_IRON_ORE,
                ELEMENT_COPPER,
                ELEMENT_COPPER_ORE,
                ELEMENT_ALUMINUM,
                ELEMENT_ALUMINUM_ORE,
                ELEMENT_COBALTITE,
                ELEMENT_COBALT,
                ELEMENT_TUNGSTEN,
                ELEMENT_TUNGSTEN_ORE,
                ELEMENT_STEEL,
                ELEMENT_DIAMOND,
                ELEMENT_REFINED_LIPID,
                ELEMENT_SULFUR,
                ELEMENT_FULLERENE,
                ELEMENT_POLYPROPYLENE,
                ELEMENT_HARD_POLYPROPYLENE,
                ELEMENT_TEMP_CONDUCTOR_SOLID,
                ELEMENT_MILK_FAT,
                ELEMENT_ISORESIN,
                ELEMENT_SUPER_INSULATOR,
                ELEMENT_NIOBIUM,
                ELEMENT_ENRICHED_URANIUM,
                ELEMENT_AMBER,
                ELEMENT_OXYROCK,
                ELEMENT_GRAPHITE,
                ELEMENT_KATAIRITE,
                ELEMENT_ICE_BELLY_POOP,
                ELEMENT_EGG_SHELL,
                ELEMENT_CRAB_SHELL,
                ELEMENT_CRAB_WOOD_SHELL,
                ELEMENT_GOLD_BELLY_CROWN,
                ELEMENT_GARBAGE_ELECTROBANK,
                ELEMENT_SELF_CHARGING_ELECTROBANK,
                ELEMENT_COLD_WHEAT_SEED,
                ELEMENT_SPICE_NUT,
                ELEMENT_BEAN_PLANT_SEED,
                ELEMENT_DEW_DRIP,
                ELEMENT_KELP
            )
            for (elementId in extraElementItems) {
                item(elementId) {
                    registryId(modId(elementId))
                    mass(1.0)
                }
            }
            item(POWER_STATION_TOOLS) {
                registryId(modId(POWER_STATION_TOOLS))
            }
        }

        unimplementedSpecs = unimplementedEntries.map { entry ->
            OniItemSpec(
                key = entry.key,
                registryId = modId(unimplementedRegistryPath(entry.id)),
                source = entry.source,
                properties = OniItemProperties(
                    notes = "Unimplemented dummy item.",
                    extras = mapOf(
                        "unimplemented_id" to entry.id.toString(),
                        "unimplemented_key" to entry.key,
                        "unimplemented_source" to entry.source.name
                    )
                )
            )
        }

        for (spec in coreSpecs + unimplementedSpecs) {
            val registryId = spec.registryId ?: continue
            itemSpecsByRegistryId[registryId] = spec
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
        val item = itemSuppliers[fullId]?.invoke() ?: return null
        val spec = itemSpecsByRegistryId[fullId]
        if (spec != null) {
            itemSpecsByItem.putIfAbsent(item, spec)
        }
        return item
    }

    fun itemByBlockId(blockId: String): Item? {
        val identifier = Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:$blockId") ?: return null
        return BuiltInRegistries.ITEM.getOptional(identifier).orElse(null)
    }

    fun specs(): List<OniItemSpec> = coreSpecs + unimplementedSpecs

    fun specByItem(item: Item): OniItemSpec? = specByItemId(item)

    fun specByItemId(item: Item): OniItemSpec? {
        val existing = itemSpecsByItem[item]
        if (existing != null) {
            return existing
        }
        val id = BuiltInRegistries.ITEM.getKey(item)
        if (id != null) {
            val spec = itemSpecsByRegistryId[id.toString()]
            if (spec != null) {
                itemSpecsByItem.putIfAbsent(item, spec)
                return spec
            }
        }
        return null
    }

    private fun normalizeId(id: String): String {
        return if (id.contains(":")) {
            id
        } else {
            "${AbstractModBootstrap.MOD_ID}:$id"
        }
    }

    private fun modId(path: String): String {
        return "${AbstractModBootstrap.MOD_ID}:$path"
    }
}

private data class UnimplementedItem(
    val id: Int,
    val source: OniItemSource,
    val key: String
)

private fun unimplementedRegistryPath(id: Int): String {
    return "unimplemented_item_$id"
}

enum class OniItemSource {
    CORE,
    UNIMPLEMENTED_STRINGS_ITEMS,
    UNIMPLEMENTED_PREFAB
}

data class OniItemProperties(
    val mass: Double? = null,
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
    private var mass: Double? = null
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

    fun mass(value: Double) {
        mass = value
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
                mass = mass,
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
