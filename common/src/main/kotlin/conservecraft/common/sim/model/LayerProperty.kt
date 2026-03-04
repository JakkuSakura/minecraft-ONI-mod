package conservecraft.common.sim.model

class LayerProperty(
    private val layer: String,
    private val key: String,
    private val value: String,
) {
    fun layer(): String = layer
    fun key(): String = key
    fun value(): String = value
}
