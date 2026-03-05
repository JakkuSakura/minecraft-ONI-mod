package conservecraft.common.element

import net.minecraft.resources.Identifier

data class ElementSpec(
    val id: String,
    val itemId: Identifier,
    val phase: ElementPhase,
    val specificHeatCapacity: Double,
    val thermalConductivityWmK: Double?,
    val massPerItem: Double?,
    val clotterThresholdItems: Int?,
    val transforms: List<ElementTransform>,
)
