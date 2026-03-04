package conservecraft.common.item

import conservecraft.common.element.OniElements

data class BottledItemSpec(
    val id: String,
    val phase: MatterPhase,
    val mass: Double,
    val temperatureK: Double
)

object OniBottledItems {
    @JvmField
    val SPECS = listOf(
        BottledItemSpec(OniItemFactory.BOTTLED_OXYGEN, MatterPhase.GAS, 1.0, 295.0),
        BottledItemSpec(OniItemFactory.BOTTLED_CO2, MatterPhase.GAS, 1.0, 295.0),
        BottledItemSpec(OniItemFactory.BOTTLED_HYDROGEN, MatterPhase.GAS, 1.0, 295.0),
        BottledItemSpec(OniItemFactory.BOTTLED_METHANE, MatterPhase.GAS, 1.0, 295.0),
        BottledItemSpec(OniItemFactory.BOTTLED_STEAM, MatterPhase.GAS, 1.0, 373.15),
        BottledItemSpec(OniItemFactory.BOTTLED_CHLORINE, MatterPhase.GAS, 1.0, 295.0),
    ) + OniElements.LIQUID_SPECS.map { spec ->
        BottledItemSpec(spec.bottledItemId, MatterPhase.LIQUID, spec.bottledMass(), spec.bottledTemperatureK())
    }
}
