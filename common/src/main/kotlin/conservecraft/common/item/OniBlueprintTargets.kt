package conservecraft.common.item

import conservecraft.common.block.OniBlockFactory

object OniBlueprintTargets {
    private val TARGETS: Map<String, String> = mapOf(
        "oxygen_diffuser" to OniBlockFactory.OXYGEN_DIFFUSER,
        "algae_deoxidizer" to OniBlockFactory.ALGAE_DEOXIDIZER,
        "co2_scrubber" to OniBlockFactory.CO2_SCRUBBER,
        "liquid_pump" to OniBlockFactory.LIQUID_PUMP,
        "gas_pump" to OniBlockFactory.GAS_PUMP,
        "manual_generator" to OniBlockFactory.MANUAL_GENERATOR,
        "battery" to OniBlockFactory.BATTERY,
        "power_wire" to OniBlockFactory.POWER_WIRE,
        "power_generator" to OniBlockFactory.POWER_GENERATOR,
        "research_desk" to OniBlockFactory.RESEARCH_DESK,
    )
    private val BLUEPRINTS_BY_BLOCK: Map<String, String> = TARGETS.entries.associate { it.value to it.key }

    fun blockIdFor(blueprintId: String): String? = TARGETS[blueprintId]

    fun blueprintIdFor(blockId: String): String? = BLUEPRINTS_BY_BLOCK[blockId]
}
