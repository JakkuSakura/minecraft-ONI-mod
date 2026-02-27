package mconi.common.element

import net.minecraft.resources.Identifier

data class ElementSpec(
    val id: String,
    val itemId: Identifier,
    val phase: ElementPhase,
    val thermalConductivityWmK: Double?,
    val massKgPerItem: Double?,
    val clotterThresholdItems: Int?,
    val transforms: List<ElementTransform>,
)
