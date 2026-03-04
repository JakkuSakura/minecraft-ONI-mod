package conservecraft.common.element

data class ElementTransform(
    val tag: ElementTransformTag,
    val minTempK: Double,
    val targetElementId: String
)
