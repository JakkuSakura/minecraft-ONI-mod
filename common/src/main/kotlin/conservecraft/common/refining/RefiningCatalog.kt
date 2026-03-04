package conservecraft.common.refining

object RefiningCatalog {
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

    object Elements {
        const val DIRT = "dirt"
        const val SAND = "sand"
        const val TOXIC_SAND = "toxic_sand"
        const val SALT = "salt"
        const val TABLE_SALT = "table_salt"
        const val SLIME_MOLD = "slime_mold"
        const val ALGAE = "algae"
        const val PLANT_FIBER = "plant_fiber"
        const val FABRICATED_WOOD = "fabricated_wood"
        const val WOOD_LOG = "wood_log"
        const val BUILDING_WOOD = "building_wood"
        const val PHOSPHORITE = "phosphorite"
        const val FERTILIZER = "fertilizer"
        const val BLEACH_STONE = "bleach_stone"
        const val CERAMIC = "ceramic"
        const val CARBON = "carbon"
        const val PEAT = "peat"
        const val REFINED_CARBON = "refined_carbon"
        const val LIME = "lime"
        const val FOSSIL = "fossil"
        const val CLAY = "clay"
        const val GOLD_AMALGAM = "gold_amalgam"
        const val GOLD = "gold"
        const val IRON = "iron"
        const val IRON_ORE = "iron_ore"
        const val COPPER = "copper"
        const val COPPER_ORE = "copper_ore"
        const val ALUMINUM = "aluminum"
        const val ALUMINUM_ORE = "aluminum_ore"
        const val COBALTITE = "cobaltite"
        const val COBALT = "cobalt"
        const val TUNGSTEN = "tungsten"
        const val TUNGSTEN_ORE = "tungsten_ore"
        const val STEEL = "steel"
        const val DIAMOND = "diamond"
        const val REFINED_LIPID = "refined_lipid"
        const val SULFUR = "sulfur"
        const val FULLERENE = "fullerene"
        const val POLYPROPYLENE = "polypropylene"
        const val HARD_POLYPROPYLENE = "hard_polypropylene"
        const val TEMP_CONDUCTOR_SOLID = "temp_conductor_solid"
        const val MILK_FAT = "milk_fat"
        const val ISORESIN = "isoresin"
        const val SUPER_INSULATOR = "super_insulator"
        const val NIOBIUM = "niobium"
        const val ENRICHED_URANIUM = "enriched_uranium"
        const val AMBER = "amber"
        const val OXYROCK = "oxyrock"
        const val GRAPHITE = "graphite"
        const val KATAIRITE = "katairite"
        const val ICE_BELLY_POOP = "ice_belly_poop"
        const val EGG_SHELL = "egg_shell"
        const val CRAB_SHELL = "crab_shell"
        const val CRAB_WOOD_SHELL = "crab_wood_shell"
        const val GOLD_BELLY_CROWN = "gold_belly_crown"
        const val GARBAGE_ELECTROBANK = "garbage_electrobank"
        const val SELF_CHARGING_ELECTROBANK = "self_charging_electrobank"
        const val COLD_WHEAT_SEED = "cold_wheat_seed"
        const val SPICE_NUT = "spice_nut"
        const val BEAN_PLANT_SEED = "bean_plant_seed"
        const val DEW_DRIP = "dew_drip"
        const val KELP = "kelp"

        const val WATER = "water"
        const val POLLUTED_WATER = "polluted_water"
        const val CRUDE_OIL = "crude_oil"
        const val LAVA = "lava"
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

        const val OXYGEN = "oxygen"
        const val CARBON_DIOXIDE = "carbon_dioxide"
        const val HYDROGEN = "hydrogen"
        const val METHANE = "methane"
        const val STEAM = "steam"
        const val CHLORINE = "chlorine"
    }

    private val RAW_MINERALS = listOf(
        "regolith",
        "sedimentary_rock",
        "igneous_rock",
        "granite",
        "abyssalite"
    )

    private val METAL_ORES = listOf(
        Elements.IRON_ORE,
        Elements.COPPER_ORE,
        Elements.GOLD_AMALGAM,
        Elements.ALUMINUM_ORE,
        Elements.COBALTITE,
        Elements.TUNGSTEN_ORE
    )

    private val METAL_REFINED = mapOf(
        Elements.IRON_ORE to Elements.IRON,
        Elements.COPPER_ORE to Elements.COPPER,
        Elements.GOLD_AMALGAM to Elements.GOLD,
        Elements.ALUMINUM_ORE to Elements.ALUMINUM,
        Elements.COBALTITE to Elements.COBALT,
        Elements.TUNGSTEN_ORE to Elements.TUNGSTEN
    )

    private val FILTER_MEDIA = listOf(
        Elements.SAND,
        "regolith"
    )

    private val COMPOSTABLE = listOf(
        Elements.POLLUTED_WATER,
        Elements.SLIME_MOLD,
        Elements.ALGAE,
        Elements.DIRT,
        Elements.TOXIC_SAND
    )

    private val PLASTIFIABLE_LIQUIDS = listOf(
        Elements.PETROLEUM,
        Elements.ETHANOL
    )

    private fun solid(vararg ids: String) = RefiningIngredient(ids.toList(), 0.0, RefiningPhase.SOLID)
    private fun liquid(vararg ids: String) = RefiningIngredient(ids.toList(), 0.0, RefiningPhase.LIQUID)
    private fun gas(vararg ids: String) = RefiningIngredient(ids.toList(), 0.0, RefiningPhase.GAS)

    private fun ingredient(ids: List<String>, amount: Double, phase: RefiningPhase) =
        RefiningIngredient(ids, amount, phase)

    private fun output(
        id: String,
        amount: Double,
        phase: RefiningPhase,
        mode: TemperatureMode,
        minTemp: Double = 0.0,
        fixedTemp: Double? = null,
        store: Boolean = true
    ) = RefiningOutput(id, amount, phase, mode, minTemp, fixedTemp, store)

    private fun batch(
        id: String,
        timeSeconds: Double,
        inputs: List<RefiningIngredient>,
        outputs: List<RefiningOutput>
    ) = RefiningRecipe(id, timeSeconds, inputs, outputs, continuous = false)

    private fun continuous(
        id: String,
        inputs: List<RefiningIngredient>,
        outputs: List<RefiningOutput>
    ) = RefiningRecipe(id, 1.0, inputs, outputs, continuous = true)

    @JvmField
    val SPECS: Map<String, RefiningBuildingSpec> = buildMap {
        put(
            COMPOST,
            RefiningBuildingSpec(
                id = COMPOST,
                powerW = 0.0,
                selfHeatKw = 1.0,
                recipes = listOf(
                    continuous(
                        "compost_to_dirt",
                        inputs = listOf(ingredient(COMPOSTABLE, 0.1, RefiningPhase.SOLID)),
                        outputs = listOf(
                            output(Elements.DIRT, 0.1, RefiningPhase.SOLID, TemperatureMode.FIXED, fixedTemp = 348.15)
                        )
                    )
                )
            )
        )

        put(
            WATER_PURIFIER,
            RefiningBuildingSpec(
                id = WATER_PURIFIER,
                powerW = 120.0,
                selfHeatKw = 4.0,
                recipes = listOf(
                    continuous(
                        "sieve",
                        inputs = listOf(
                            ingredient(FILTER_MEDIA, 1.0, RefiningPhase.SOLID),
                            ingredient(listOf(Elements.POLLUTED_WATER), 5.0, RefiningPhase.LIQUID)
                        ),
                        outputs = listOf(
                            output(Elements.WATER, 5.0, RefiningPhase.LIQUID, TemperatureMode.AVERAGE_INPUT),
                            output(Elements.TOXIC_SAND, 0.2, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT)
                        )
                    )
                ),
                liquidInput = true,
                liquidOutput = true
            )
        )

        put(
            DESALINATOR,
            RefiningBuildingSpec(
                id = DESALINATOR,
                powerW = 480.0,
                selfHeatKw = 8.0,
                recipes = listOf(
                    continuous(
                        "salt_water_desalination",
                        inputs = listOf(ingredient(listOf(Elements.SALT_WATER), 5.0, RefiningPhase.LIQUID)),
                        outputs = listOf(
                            output(Elements.WATER, 4.65, RefiningPhase.LIQUID, TemperatureMode.AVERAGE_INPUT),
                            output(Elements.SALT, 0.35, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT)
                        )
                    ),
                    continuous(
                        "brine_desalination",
                        inputs = listOf(ingredient(listOf(Elements.BRINE), 5.0, RefiningPhase.LIQUID)),
                        outputs = listOf(
                            output(Elements.WATER, 3.5, RefiningPhase.LIQUID, TemperatureMode.AVERAGE_INPUT),
                            output(Elements.SALT, 1.5, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT)
                        )
                    )
                ),
                liquidInput = true,
                liquidOutput = true
            )
        )

        put(
            FERTILIZER_MAKER,
            RefiningBuildingSpec(
                id = FERTILIZER_MAKER,
                powerW = 120.0,
                selfHeatKw = 2.0,
                recipes = listOf(
                    continuous(
                        "fertilizer",
                        inputs = listOf(
                            ingredient(listOf(Elements.POLLUTED_WATER), 0.039, RefiningPhase.LIQUID),
                            ingredient(listOf(Elements.DIRT), 0.065, RefiningPhase.SOLID),
                            ingredient(listOf(Elements.PHOSPHORITE), 0.025999999, RefiningPhase.SOLID)
                        ),
                        outputs = listOf(
                            output(Elements.FERTILIZER, 0.12, RefiningPhase.SOLID, TemperatureMode.FIXED, fixedTemp = 323.15),
                            output(Elements.METHANE, 0.01, RefiningPhase.GAS, TemperatureMode.FIXED, fixedTemp = 349.15, store = false)
                        )
                    )
                ),
                liquidInput = true
            )
        )

        put(
            ALGAE_DISTILLERY,
            RefiningBuildingSpec(
                id = ALGAE_DISTILLERY,
                powerW = 120.0,
                selfHeatKw = 1.0,
                recipes = listOf(
                    continuous(
                        "slime_to_algae",
                        inputs = listOf(ingredient(listOf(Elements.SLIME_MOLD), 0.6, RefiningPhase.SOLID)),
                        outputs = listOf(
                            output(Elements.ALGAE, 0.2, RefiningPhase.SOLID, TemperatureMode.FIXED, fixedTemp = 303.15),
                            output(Elements.POLLUTED_WATER, 0.40000004, RefiningPhase.LIQUID, TemperatureMode.FIXED, fixedTemp = 303.15)
                        )
                    )
                ),
                liquidOutput = true
            )
        )

        put(
            ETHANOL_DISTILLERY,
            RefiningBuildingSpec(
                id = ETHANOL_DISTILLERY,
                powerW = 240.0,
                selfHeatKw = 4.0,
                recipes = listOf(
                    continuous(
                        "wood_to_ethanol",
                        inputs = listOf(ingredient(listOf(Elements.BUILDING_WOOD), 1.0, RefiningPhase.SOLID)),
                        outputs = listOf(
                            output(Elements.ETHANOL, 0.5, RefiningPhase.LIQUID, TemperatureMode.FIXED, fixedTemp = 346.5),
                            output(Elements.TOXIC_SAND, 1.0 / 3.0, RefiningPhase.SOLID, TemperatureMode.FIXED, fixedTemp = 366.5),
                            output(Elements.CARBON_DIOXIDE, 1.0 / 6.0, RefiningPhase.GAS, TemperatureMode.FIXED, fixedTemp = 366.5, store = false)
                        )
                    )
                ),
                liquidOutput = true
            )
        )

        put(
            FABRICATED_WOOD_MAKER,
            RefiningBuildingSpec(
                id = FABRICATED_WOOD_MAKER,
                powerW = 480.0,
                selfHeatKw = 1.0,
                heatedTemperatureK = 333.15,
                recipes = listOf(
                    batch(
                        "fabricated_wood",
                        timeSeconds = 40.0,
                        inputs = listOf(
                            ingredient(listOf(Elements.PLANT_FIBER), 90.0, RefiningPhase.SOLID),
                            ingredient(listOf(Elements.NATURAL_RESIN), 10.0, RefiningPhase.LIQUID)
                        ),
                        outputs = listOf(
                            output(Elements.FABRICATED_WOOD, 100.0, RefiningPhase.SOLID, TemperatureMode.HEATED)
                        )
                    )
                ),
                liquidInput = true
            )
        )

        val rockCrusherRecipes = mutableListOf<RefiningRecipe>()
        for (raw in RAW_MINERALS) {
            rockCrusherRecipes.add(
                batch(
                    "${raw}_to_sand",
                    timeSeconds = 40.0,
                    inputs = listOf(ingredient(listOf(raw), 100.0, RefiningPhase.SOLID)),
                    outputs = listOf(
                        output(Elements.SAND, 100.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT)
                    )
                )
            )
        }
        for ((ore, refined) in METAL_REFINED) {
            rockCrusherRecipes.add(
                batch(
                    "${ore}_to_${refined}",
                    timeSeconds = 40.0,
                    inputs = listOf(ingredient(listOf(ore), 100.0, RefiningPhase.SOLID)),
                    outputs = listOf(
                        output(refined, 50.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT),
                        output(Elements.SAND, 50.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT)
                    )
                )
            )
        }
        rockCrusherRecipes.add(
            batch(
                "egg_shell_to_lime",
                timeSeconds = 40.0,
                inputs = listOf(ingredient(listOf(Elements.EGG_SHELL), 5.0, RefiningPhase.SOLID)),
                outputs = listOf(output(Elements.LIME, 5.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT))
            )
        )
        rockCrusherRecipes.add(
            batch(
                "crab_shell_to_lime",
                timeSeconds = 40.0,
                inputs = listOf(ingredient(listOf(Elements.CRAB_SHELL), 10.0, RefiningPhase.SOLID)),
                outputs = listOf(output(Elements.LIME, 10.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT))
            )
        )
        rockCrusherRecipes.add(
            batch(
                "crab_wood_shell_to_wood",
                timeSeconds = 40.0,
                inputs = listOf(ingredient(listOf(Elements.CRAB_WOOD_SHELL), 500.0, RefiningPhase.SOLID)),
                outputs = listOf(output(Elements.WOOD_LOG, 500.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT))
            )
        )
        rockCrusherRecipes.add(
            batch(
                "fossil_to_lime",
                timeSeconds = 40.0,
                inputs = listOf(ingredient(listOf(Elements.FOSSIL), 100.0, RefiningPhase.SOLID)),
                outputs = listOf(
                    output(Elements.LIME, 5.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT),
                    output("sedimentary_rock", 95.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT)
                )
            )
        )
        rockCrusherRecipes.add(
            batch(
                "garbage_electrobank_to_katairite",
                timeSeconds = 40.0,
                inputs = listOf(ingredient(listOf(Elements.GARBAGE_ELECTROBANK), 1.0, RefiningPhase.SOLID)),
                outputs = listOf(output(Elements.KATAIRITE, 100.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT))
            )
        )
        rockCrusherRecipes.add(
            batch(
                "salt_to_table_salt",
                timeSeconds = 40.0,
                inputs = listOf(ingredient(listOf(Elements.SALT), 100.0, RefiningPhase.SOLID)),
                outputs = listOf(
                    output(Elements.TABLE_SALT, 100.0 * 5E-05, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT),
                    output(Elements.SAND, 100.0 * (1.0 - 5E-05), RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT)
                )
            )
        )
        rockCrusherRecipes.add(
            batch(
                "fullerene_to_graphite",
                timeSeconds = 40.0,
                inputs = listOf(ingredient(listOf(Elements.FULLERENE), 100.0, RefiningPhase.SOLID)),
                outputs = listOf(
                    output(Elements.GRAPHITE, 90.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT),
                    output(Elements.SAND, 10.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT)
                )
            )
        )
        rockCrusherRecipes.add(
            batch(
                "ice_belly_poop",
                timeSeconds = 40.0,
                inputs = listOf(ingredient(listOf(Elements.ICE_BELLY_POOP), 120.0, RefiningPhase.SOLID)),
                outputs = listOf(
                    output(Elements.PHOSPHORITE, 120.0 * 0.2667, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT),
                    output(Elements.CLAY, 120.0 * (1.0 - 0.2667), RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT)
                )
            )
        )
        rockCrusherRecipes.add(
            batch(
                "gold_belly_crown",
                timeSeconds = 40.0,
                inputs = listOf(ingredient(listOf(Elements.GOLD_BELLY_CROWN), 1.0, RefiningPhase.SOLID)),
                outputs = listOf(output(Elements.GOLD_AMALGAM, 250.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT))
            )
        )
        put(
            ROCK_CRUSHER,
            RefiningBuildingSpec(
                id = ROCK_CRUSHER,
                powerW = 240.0,
                selfHeatKw = 16.0,
                recipes = rockCrusherRecipes
            )
        )

        val kilnRecipes = mutableListOf<RefiningRecipe>()
        kilnRecipes.add(
            batch(
                "clay_to_ceramic",
                timeSeconds = 40.0,
                inputs = listOf(
                    ingredient(listOf(Elements.CLAY), 100.0, RefiningPhase.SOLID),
                    ingredient(listOf(Elements.WOOD_LOG), 200.0, RefiningPhase.SOLID)
                ),
                outputs = listOf(output(Elements.CERAMIC, 100.0, RefiningPhase.SOLID, TemperatureMode.HEATED))
            )
        )
        kilnRecipes.add(
            batch(
                "fabricated_wood_to_ceramic",
                timeSeconds = 40.0,
                inputs = listOf(
                    ingredient(listOf(Elements.CLAY), 100.0, RefiningPhase.SOLID),
                    ingredient(listOf(Elements.FABRICATED_WOOD), 200.0, RefiningPhase.SOLID)
                ),
                outputs = listOf(output(Elements.CERAMIC, 100.0, RefiningPhase.SOLID, TemperatureMode.HEATED))
            )
        )
        kilnRecipes.add(
            batch(
                "carbon_to_refined_carbon",
                timeSeconds = 40.0,
                inputs = listOf(ingredient(listOf(Elements.CARBON), 125.0, RefiningPhase.SOLID)),
                outputs = listOf(output(Elements.REFINED_CARBON, 100.0, RefiningPhase.SOLID, TemperatureMode.HEATED))
            )
        )
        kilnRecipes.add(
            batch(
                "peat_to_refined_carbon",
                timeSeconds = 40.0,
                inputs = listOf(ingredient(listOf(Elements.PEAT), 300.0, RefiningPhase.SOLID)),
                outputs = listOf(output(Elements.REFINED_CARBON, 100.0, RefiningPhase.SOLID, TemperatureMode.HEATED))
            )
        )
        put(
            KILN,
            RefiningBuildingSpec(
                id = KILN,
                powerW = 0.0,
                selfHeatKw = 4.0,
                heatedTemperatureK = 353.15,
                recipes = kilnRecipes
            )
        )

        put(
            OIL_REFINERY,
            RefiningBuildingSpec(
                id = OIL_REFINERY,
                powerW = 480.0,
                selfHeatKw = 8.0,
                recipes = listOf(
                    continuous(
                        "crude_to_petroleum",
                        inputs = listOf(ingredient(listOf(Elements.CRUDE_OIL), 10.0, RefiningPhase.LIQUID)),
                        outputs = listOf(
                            output(Elements.PETROLEUM, 5.0, RefiningPhase.LIQUID, TemperatureMode.FIXED, fixedTemp = 348.15),
                            output(Elements.METHANE, 0.09, RefiningPhase.GAS, TemperatureMode.FIXED, fixedTemp = 348.15, store = false)
                        )
                    )
                ),
                liquidInput = true,
                liquidOutput = true
            )
        )

        put(
            POLYMERIZER,
            RefiningBuildingSpec(
                id = POLYMERIZER,
                powerW = 240.0,
                selfHeatKw = 32.0,
                recipes = listOf(
                    continuous(
                        "plastic",
                        inputs = listOf(ingredient(PLASTIFIABLE_LIQUIDS, 5.0 / 6.0, RefiningPhase.LIQUID)),
                        outputs = listOf(
                            output(Elements.POLYPROPYLENE, 0.5, RefiningPhase.SOLID, TemperatureMode.FIXED, fixedTemp = 348.15),
                            output(Elements.STEAM, 1.0 / 120.0, RefiningPhase.GAS, TemperatureMode.FIXED, fixedTemp = 473.15),
                            output(Elements.CARBON_DIOXIDE, 1.0 / 120.0, RefiningPhase.GAS, TemperatureMode.FIXED, fixedTemp = 423.15)
                        )
                    )
                ),
                liquidInput = true,
                gasOutput = true
            )
        )

        put(
            OXYLITE_REFINERY,
            RefiningBuildingSpec(
                id = OXYLITE_REFINERY,
                powerW = 1200.0,
                selfHeatKw = 4.0,
                recipes = listOf(
                    continuous(
                        "oxylite",
                        inputs = listOf(
                            ingredient(listOf(Elements.OXYGEN), 0.6, RefiningPhase.GAS),
                            ingredient(listOf(Elements.GOLD), 0.003, RefiningPhase.SOLID)
                        ),
                        outputs = listOf(
                            output(Elements.OXYROCK, 0.6, RefiningPhase.SOLID, TemperatureMode.FIXED, fixedTemp = 303.15)
                        )
                    )
                ),
                gasInput = true
            )
        )

        put(
            CHLORINATOR,
            RefiningBuildingSpec(
                id = CHLORINATOR,
                powerW = 480.0,
                selfHeatKw = 2.0,
                recipes = listOf(
                    batch(
                        "bleach_stone",
                        timeSeconds = 40.0,
                        inputs = listOf(
                            ingredient(listOf(Elements.SALT), 30.0, RefiningPhase.SOLID),
                            ingredient(listOf(Elements.GOLD), 0.5, RefiningPhase.SOLID)
                        ),
                        outputs = listOf(
                            output(Elements.BLEACH_STONE, 10.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT),
                            output(Elements.SAND, 19.999998, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT)
                        )
                    )
                )
            )
        )

        put(
            CHEMICAL_REFINERY,
            RefiningBuildingSpec(
                id = CHEMICAL_REFINERY,
                powerW = 480.0,
                selfHeatKw = 1.0,
                heatedTemperatureK = 313.15,
                recipes = listOf(
                    batch(
                        "salt_water",
                        timeSeconds = 40.0,
                        inputs = listOf(
                            ingredient(listOf(Elements.WATER), 93.0, RefiningPhase.LIQUID),
                            ingredient(listOf(Elements.SALT), 7.0, RefiningPhase.SOLID)
                        ),
                        outputs = listOf(
                            output(Elements.SALT_WATER, 100.0, RefiningPhase.LIQUID, TemperatureMode.AVERAGE_INPUT)
                        )
                    ),
                    batch(
                        "refined_lipid",
                        timeSeconds = 40.0,
                        inputs = listOf(
                            ingredient(listOf(Elements.PHYTO_OIL), 160.0, RefiningPhase.LIQUID),
                            ingredient(listOf(Elements.BLEACH_STONE), 40.0, RefiningPhase.SOLID)
                        ),
                        outputs = listOf(
                            output(Elements.REFINED_LIPID, 200.0, RefiningPhase.SOLID, TemperatureMode.HEATED)
                        )
                    ),
                    batch(
                        "super_coolant",
                        timeSeconds = 80.0,
                        inputs = listOf(
                            ingredient(listOf(Elements.FULLERENE), 1.0, RefiningPhase.SOLID),
                            ingredient(listOf(Elements.GOLD), 49.5, RefiningPhase.SOLID),
                            ingredient(listOf(Elements.PETROLEUM), 49.5, RefiningPhase.LIQUID)
                        ),
                        outputs = listOf(
                            output(Elements.SUPER_COOLANT, 100.0, RefiningPhase.LIQUID, TemperatureMode.HEATED)
                        )
                    ),
                    batch(
                        "visco_gel",
                        timeSeconds = 80.0,
                        inputs = listOf(
                            ingredient(listOf(Elements.ISORESIN), 35.0, RefiningPhase.SOLID),
                            ingredient(listOf(Elements.PETROLEUM), 65.0, RefiningPhase.LIQUID)
                        ),
                        outputs = listOf(
                            output(Elements.VISCO_GEL, 100.0, RefiningPhase.LIQUID, TemperatureMode.HEATED)
                        )
                    )
                ),
                liquidInput = true,
                liquidOutput = true
            )
        )

        put(
            SUPERMATERIAL_REFINERY,
            RefiningBuildingSpec(
                id = SUPERMATERIAL_REFINERY,
                powerW = 1600.0,
                selfHeatKw = 16.0,
                heatedTemperatureK = 313.15,
                recipes = listOf(
                    batch(
                        "fullerene",
                        timeSeconds = 80.0,
                        inputs = listOf(
                            ingredient(listOf(Elements.GRAPHITE), 90.0, RefiningPhase.SOLID),
                            ingredient(listOf(Elements.SULFUR), 5.0, RefiningPhase.SOLID),
                            ingredient(listOf(Elements.ALUMINUM), 5.0, RefiningPhase.SOLID)
                        ),
                        outputs = listOf(
                            output(Elements.FULLERENE, 100.0, RefiningPhase.SOLID, TemperatureMode.HEATED)
                        )
                    ),
                    batch(
                        "hard_plastic",
                        timeSeconds = 80.0,
                        inputs = listOf(
                            ingredient(listOf(Elements.TEMP_CONDUCTOR_SOLID), 15.0, RefiningPhase.SOLID),
                            ingredient(listOf(Elements.POLYPROPYLENE), 70.0, RefiningPhase.SOLID),
                            ingredient(listOf(Elements.MILK_FAT), 15.0, RefiningPhase.SOLID)
                        ),
                        outputs = listOf(
                            output(Elements.HARD_POLYPROPYLENE, 100.0, RefiningPhase.SOLID, TemperatureMode.HEATED)
                        )
                    ),
                    batch(
                        "super_insulator",
                        timeSeconds = 80.0,
                        inputs = listOf(
                            ingredient(listOf(Elements.ISORESIN), 15.0, RefiningPhase.SOLID),
                            ingredient(listOf(Elements.KATAIRITE), 80.0, RefiningPhase.SOLID),
                            ingredient(listOf(Elements.PLANT_FIBER), 5.0, RefiningPhase.SOLID)
                        ),
                        outputs = listOf(
                            output(Elements.SUPER_INSULATOR, 100.0, RefiningPhase.SOLID, TemperatureMode.HEATED)
                        )
                    ),
                    batch(
                        "temp_conductor",
                        timeSeconds = 80.0,
                        inputs = listOf(
                            ingredient(listOf(Elements.NIOBIUM), 5.0, RefiningPhase.SOLID),
                            ingredient(listOf(Elements.TUNGSTEN), 95.0, RefiningPhase.SOLID)
                        ),
                        outputs = listOf(
                            output(Elements.TEMP_CONDUCTOR_SOLID, 100.0, RefiningPhase.SOLID, TemperatureMode.HEATED)
                        )
                    ),
                    batch(
                        "self_charging_power_bank",
                        timeSeconds = 80.0,
                        inputs = listOf(ingredient(listOf(Elements.ENRICHED_URANIUM), 10.0, RefiningPhase.SOLID)),
                        outputs = listOf(
                            output(Elements.SELF_CHARGING_ELECTROBANK, 1.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT)
                        )
                    )
                )
            )
        )

        put(
            DIAMOND_PRESS,
            RefiningBuildingSpec(
                id = DIAMOND_PRESS,
                powerW = 240.0,
                selfHeatKw = 16.0,
                recipes = listOf(
                    batch(
                        "diamond",
                        timeSeconds = 80.0,
                        inputs = listOf(ingredient(listOf(Elements.REFINED_CARBON), 100.0, RefiningPhase.SOLID)),
                        outputs = listOf(output(Elements.DIAMOND, 100.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT))
                    )
                )
            )
        )

        put(
            MILK_FAT_SEPARATOR,
            RefiningBuildingSpec(
                id = MILK_FAT_SEPARATOR,
                powerW = 480.0,
                selfHeatKw = 8.0,
                recipes = listOf(
                    continuous(
                        "milk_fat",
                        inputs = listOf(ingredient(listOf(Elements.MILK), 1.0, RefiningPhase.LIQUID)),
                        outputs = listOf(
                            output(Elements.MILK_FAT, 0.089999996, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT),
                            output(Elements.BRINE, 0.80999994, RefiningPhase.LIQUID, TemperatureMode.AVERAGE_INPUT),
                            output(Elements.CARBON_DIOXIDE, 0.100000024, RefiningPhase.GAS, TemperatureMode.FIXED, fixedTemp = 348.15, store = false)
                        )
                    )
                ),
                liquidInput = true,
                liquidOutput = true
            )
        )

        put(
            MILK_PRESS,
            RefiningBuildingSpec(
                id = MILK_PRESS,
                powerW = 0.0,
                selfHeatKw = 2.0,
                recipes = listOf(
                    batch(
                        "wheat_milk",
                        timeSeconds = 40.0,
                        inputs = listOf(
                            ingredient(listOf(Elements.COLD_WHEAT_SEED), 10.0, RefiningPhase.SOLID),
                            ingredient(listOf(Elements.WATER), 15.0, RefiningPhase.LIQUID)
                        ),
                        outputs = listOf(output(Elements.MILK, 20.0, RefiningPhase.LIQUID, TemperatureMode.AVERAGE_INPUT))
                    ),
                    batch(
                        "nut_milk",
                        timeSeconds = 40.0,
                        inputs = listOf(
                            ingredient(listOf(Elements.SPICE_NUT), 3.0, RefiningPhase.SOLID),
                            ingredient(listOf(Elements.WATER), 17.0, RefiningPhase.LIQUID)
                        ),
                        outputs = listOf(output(Elements.MILK, 20.0, RefiningPhase.LIQUID, TemperatureMode.AVERAGE_INPUT))
                    ),
                    batch(
                        "bean_milk",
                        timeSeconds = 40.0,
                        inputs = listOf(
                            ingredient(listOf(Elements.BEAN_PLANT_SEED), 2.0, RefiningPhase.SOLID),
                            ingredient(listOf(Elements.WATER), 18.0, RefiningPhase.LIQUID)
                        ),
                        outputs = listOf(output(Elements.MILK, 20.0, RefiningPhase.LIQUID, TemperatureMode.AVERAGE_INPUT))
                    ),
                    batch(
                        "dew_drip_milk",
                        timeSeconds = 40.0,
                        inputs = listOf(ingredient(listOf(Elements.DEW_DRIP), 2.0, RefiningPhase.SOLID)),
                        outputs = listOf(output(Elements.MILK, 20.0, RefiningPhase.LIQUID, TemperatureMode.AVERAGE_INPUT))
                    ),
                    batch(
                        "slime_to_phyto_oil",
                        timeSeconds = 40.0,
                        inputs = listOf(ingredient(listOf(Elements.SLIME_MOLD), 100.0, RefiningPhase.SOLID)),
                        outputs = listOf(
                            output(Elements.PHYTO_OIL, 70.0, RefiningPhase.LIQUID, TemperatureMode.AVERAGE_INPUT),
                            output(Elements.DIRT, 30.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT)
                        )
                    ),
                    batch(
                        "kelp_to_phyto_oil",
                        timeSeconds = 40.0,
                        inputs = listOf(
                            ingredient(listOf(Elements.KELP), 25.0, RefiningPhase.SOLID),
                            ingredient(listOf(Elements.WATER), 75.0, RefiningPhase.LIQUID)
                        ),
                        outputs = listOf(output(Elements.PHYTO_OIL, 100.0, RefiningPhase.LIQUID, TemperatureMode.AVERAGE_INPUT))
                    ),
                    batch(
                        "amber_to_resin",
                        timeSeconds = 40.0,
                        inputs = listOf(ingredient(listOf(Elements.AMBER), 100.0, RefiningPhase.SOLID)),
                        outputs = listOf(
                            output(Elements.NATURAL_RESIN, 50.0, RefiningPhase.LIQUID, TemperatureMode.AVERAGE_INPUT),
                            output(Elements.FOSSIL, 25.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT),
                            output(Elements.SAND, 25.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT)
                        )
                    )
                ),
                liquidOutput = true
            )
        )

        put(
            GLASS_FORGE,
            RefiningBuildingSpec(
                id = GLASS_FORGE,
                powerW = 1200.0,
                selfHeatKw = 16.0,
                recipes = listOf(
                    batch(
                        "molten_glass",
                        timeSeconds = 40.0,
                        inputs = listOf(ingredient(listOf(Elements.SAND), 100.0, RefiningPhase.SOLID)),
                        outputs = listOf(output(Elements.MOLTEN_GLASS, 25.0, RefiningPhase.LIQUID, TemperatureMode.MELTED))
                    )
                ),
                liquidOutput = true
            )
        )

        val metalRecipes = mutableListOf<RefiningRecipe>()
        for ((ore, refined) in METAL_REFINED) {
            metalRecipes.add(
                batch(
                    "${ore}_refine",
                    timeSeconds = 40.0,
                    inputs = listOf(ingredient(listOf(ore), 100.0, RefiningPhase.SOLID)),
                    outputs = listOf(output(refined, 100.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT))
                )
            )
        }
        metalRecipes.add(
            batch(
                "steel",
                timeSeconds = 40.0,
                inputs = listOf(
                    ingredient(listOf(Elements.IRON), 70.0, RefiningPhase.SOLID),
                    ingredient(listOf(Elements.REFINED_CARBON), 20.0, RefiningPhase.SOLID),
                    ingredient(listOf(Elements.LIME), 10.0, RefiningPhase.SOLID)
                ),
                outputs = listOf(output(Elements.STEEL, 100.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT))
            )
        )
        put(
            METAL_REFINERY,
            RefiningBuildingSpec(
                id = METAL_REFINERY,
                powerW = 1200.0,
                selfHeatKw = 16.0,
                recipes = metalRecipes,
                liquidInput = true,
                liquidOutput = true
            )
        )

        put(
            SLUDGE_PRESS,
            RefiningBuildingSpec(
                id = SLUDGE_PRESS,
                powerW = 120.0,
                selfHeatKw = 4.0,
                recipes = listOf(
                    batch(
                        "slime_mold_press",
                        timeSeconds = 20.0,
                        inputs = listOf(ingredient(listOf(Elements.SLIME_MOLD), 150.0, RefiningPhase.SOLID)),
                        outputs = listOf(
                            output(Elements.ALGAE, 90.0, RefiningPhase.SOLID, TemperatureMode.AVERAGE_INPUT),
                            output(Elements.POLLUTED_WATER, 60.0, RefiningPhase.LIQUID, TemperatureMode.AVERAGE_INPUT)
                        )
                    )
                ),
                liquidOutput = true
            )
        )
    }

    fun spec(id: String): RefiningBuildingSpec? = SPECS[id]

    fun allSpecs(): List<RefiningBuildingSpec> = SPECS.values.toList()
}
