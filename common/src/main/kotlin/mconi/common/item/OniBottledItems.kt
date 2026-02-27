package mconi.common.item

import mconi.common.element.OniElements

data class BottledItemSpec(
    val id: String,
    val phase: MatterPhase,
    val massKg: Double,
    val temperatureK: Double
)

object OniBottledItems {
    @JvmField
    val SPECS = listOf(
        BottledItemSpec(OniItemFactory.BOTTLED_OXYGEN, MatterPhase.GAS, 1.0, 295.0),
        BottledItemSpec(OniItemFactory.BOTTLED_CO2, MatterPhase.GAS, 1.0, 295.0),
        BottledItemSpec(OniItemFactory.BOTTLED_HYDROGEN, MatterPhase.GAS, 1.0, 295.0),
    ) + OniElements.LIQUID_SPECS.map { spec ->
        BottledItemSpec(spec.bottledItemId, MatterPhase.LIQUID, spec.defaultMassKg, spec.defaultTemperatureK)
    }
}
