package mconi.common.item

data class OniBlueprint(
    val id: String,
    val requiredResearch: String,
    val materialSlots: List<OniMaterialSlot>,
    val buildTimeSeconds: Int,
)

data class OniMaterialSlot(
    val slotId: String,
    val amount: Int,
    val allowedItems: List<String>,
)

object OniBlueprintRegistry {
    private val blueprints: MutableMap<String, OniBlueprint> = LinkedHashMap()

    init {
        register(
            OniBlueprint(
                "oxygen_diffuser",
                "oxygen",
                listOf(
                    OniMaterialSlot("metal_ore", 200, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("raw_mineral", 50, OniElementCatalog.RAW_MINERAL),
                ),
                30
            )
        )
        register(
            OniBlueprint(
                "algae_deoxidizer",
                "oxygen",
                listOf(
                    OniMaterialSlot("metal_ore", 200, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("raw_mineral", 50, OniElementCatalog.RAW_MINERAL),
                ),
                25
            )
        )
        register(
            OniBlueprint(
                "co2_scrubber",
                "oxygen",
                listOf(
                    OniMaterialSlot("metal_ore", 100, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("raw_mineral", 50, OniElementCatalog.RAW_MINERAL),
                ),
                35
            )
        )
        register(
            OniBlueprint(
                "liquid_pump",
                "liquids",
                listOf(
                    OniMaterialSlot("metal_ore", 400, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("refined_metal", 50, OniElementCatalog.REFINED_METAL),
                ),
                45
            )
        )
        register(
            OniBlueprint(
                "gas_pump",
                "liquids",
                listOf(
                    OniMaterialSlot("metal_ore", 50, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("refined_metal", 25, OniElementCatalog.REFINED_METAL),
                ),
                45
            )
        )
        register(
            OniBlueprint(
                "manual_generator",
                "power",
                listOf(
                    OniMaterialSlot("metal_ore", 200, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("raw_mineral", 100, OniElementCatalog.RAW_MINERAL),
                ),
                40
            )
        )
        register(
            OniBlueprint(
                "battery",
                "power",
                listOf(
                    OniMaterialSlot("metal_ore", 200, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("refined_metal", 50, OniElementCatalog.REFINED_METAL),
                ),
                35
            )
        )
        register(
            OniBlueprint(
                "power_wire",
                "power",
                listOf(
                    OniMaterialSlot("metal_ore", 25, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("raw_mineral", 10, OniElementCatalog.RAW_MINERAL),
                ),
                10
            )
        )
        register(
            OniBlueprint(
                "wire",
                "power",
                listOf(
                    OniMaterialSlot("metal_ore", 25, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("raw_mineral", 10, OniElementCatalog.RAW_MINERAL),
                ),
                10
            )
        )
        register(
            OniBlueprint(
                "wire_bridge",
                "power",
                listOf(
                    OniMaterialSlot("metal_ore", 40, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("raw_mineral", 10, OniElementCatalog.RAW_MINERAL),
                ),
                12
            )
        )
        register(
            OniBlueprint(
                "conductive_wire",
                "power",
                listOf(
                    OniMaterialSlot("refined_metal", 50, OniElementCatalog.REFINED_METAL),
                ),
                12
            )
        )
        register(
            OniBlueprint(
                "conductive_wire_bridge",
                "power",
                listOf(
                    OniMaterialSlot("refined_metal", 70, OniElementCatalog.REFINED_METAL),
                ),
                14
            )
        )
        register(
            OniBlueprint(
                "heavi_watt_wire",
                "power",
                listOf(
                    OniMaterialSlot("metal_ore", 75, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("raw_mineral", 20, OniElementCatalog.RAW_MINERAL),
                ),
                14
            )
        )
        register(
            OniBlueprint(
                "heavi_watt_joint_plate",
                "power",
                listOf(
                    OniMaterialSlot("metal_ore", 90, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("raw_mineral", 20, OniElementCatalog.RAW_MINERAL),
                ),
                16
            )
        )
        register(
            OniBlueprint(
                "heavi_watt_conductive_wire",
                "power",
                listOf(
                    OniMaterialSlot("refined_metal", 100, OniElementCatalog.REFINED_METAL),
                ),
                16
            )
        )
        register(
            OniBlueprint(
                "heavi_watt_conductive_joint_plate",
                "power",
                listOf(
                    OniMaterialSlot("refined_metal", 110, OniElementCatalog.REFINED_METAL),
                ),
                18
            )
        )
        register(
            OniBlueprint(
                "power_transformer_small",
                "power",
                listOf(
                    OniMaterialSlot("metal_ore", 200, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("refined_metal", 50, OniElementCatalog.REFINED_METAL),
                ),
                25
            )
        )
        register(
            OniBlueprint(
                "power_transformer",
                "power",
                listOf(
                    OniMaterialSlot("metal_ore", 250, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("refined_metal", 75, OniElementCatalog.REFINED_METAL),
                ),
                30
            )
        )
        register(
            OniBlueprint(
                "smart_battery",
                "power",
                listOf(
                    OniMaterialSlot("refined_metal", 200, OniElementCatalog.REFINED_METAL),
                ),
                25
            )
        )
        register(
            OniBlueprint(
                "jumbo_battery",
                "power",
                listOf(
                    OniMaterialSlot("metal_ore", 200, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("refined_metal", 100, OniElementCatalog.REFINED_METAL),
                ),
                30
            )
        )
        register(
            OniBlueprint(
                "coal_generator",
                "power",
                listOf(
                    OniMaterialSlot("metal_ore", 600, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("refined_metal", 50, OniElementCatalog.REFINED_METAL),
                ),
                35
            )
        )
        register(
            OniBlueprint(
                "hydrogen_generator",
                "power",
                listOf(
                    OniMaterialSlot("metal_ore", 300, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("refined_metal", 50, OniElementCatalog.REFINED_METAL),
                ),
                35
            )
        )
        register(
            OniBlueprint(
                "natural_gas_generator",
                "power",
                listOf(
                    OniMaterialSlot("metal_ore", 300, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("refined_metal", 50, OniElementCatalog.REFINED_METAL),
                ),
                35
            )
        )
        register(
            OniBlueprint(
                "petroleum_generator",
                "power",
                listOf(
                    OniMaterialSlot("metal_ore", 320, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("refined_metal", 75, OniElementCatalog.REFINED_METAL),
                ),
                40
            )
        )
        register(
            OniBlueprint(
                "power_control_station",
                "power",
                listOf(
                    OniMaterialSlot("refined_metal", 100, OniElementCatalog.REFINED_METAL),
                    OniMaterialSlot("raw_mineral", 50, OniElementCatalog.RAW_MINERAL),
                ),
                25
            )
        )
        register(
            OniBlueprint(
                "power_switch",
                "power",
                listOf(
                    OniMaterialSlot("refined_metal", 50, OniElementCatalog.REFINED_METAL),
                ),
                12
            )
        )
        register(
            OniBlueprint(
                "power_shutoff",
                "power",
                listOf(
                    OniMaterialSlot("refined_metal", 75, OniElementCatalog.REFINED_METAL),
                ),
                14
            )
        )
        register(
            OniBlueprint(
                "power_generator",
                "power",
                listOf(
                    OniMaterialSlot("metal_ore", 800, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("refined_metal", 200, OniElementCatalog.REFINED_METAL),
                ),
                40
            )
        )
        register(
            OniBlueprint(
                "research_desk",
                "research",
                listOf(
                    OniMaterialSlot("metal_ore", 400, OniElementCatalog.METAL_ORE),
                    OniMaterialSlot("raw_mineral", 100, OniElementCatalog.RAW_MINERAL),
                ),
                35
            )
        )
    }

    @JvmStatic
    fun register(blueprint: OniBlueprint) {
        blueprints[blueprint.id] = blueprint
    }

    @JvmStatic
    fun get(id: String): OniBlueprint? = blueprints[id]

    @JvmStatic
    fun allIds(): Set<String> = blueprints.keys
}
