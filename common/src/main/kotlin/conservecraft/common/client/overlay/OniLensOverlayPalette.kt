package conservecraft.common.client.overlay

import conservecraft.common.element.OniElements

object OniLensOverlayPalette {
    data class Color(val r: Float, val g: Float, val b: Float, val a: Float)

    fun thermalColor(tempK: Double): Color {
        val normalized = ((tempK - 250.0) / 200.0).coerceIn(0.0, 1.0)
        val r = normalized.toFloat()
        val b = (1.0 - normalized).toFloat()
        val g = (0.2 + 0.6 * (1.0 - kotlin.math.abs(normalized - 0.5) * 2.0)).toFloat()
        return Color(r, g, b, 0.6f)
    }

    fun gasColor(gasId: String, alpha: Float): Color {
        return when (gasId) {
            OniElements.GAS_OXYGEN.id -> Color(0.3f, 0.7f, 1.0f, alpha)
            OniElements.GAS_CARBON_DIOXIDE.id -> Color(0.6f, 0.6f, 0.6f, alpha)
            OniElements.GAS_HYDROGEN.id -> Color(0.9f, 0.9f, 1.0f, alpha)
            OniElements.GAS_METHANE.id -> Color(0.7f, 0.9f, 0.4f, alpha)
            OniElements.GAS_STEAM.id -> Color(0.9f, 0.9f, 0.9f, alpha)
            OniElements.GAS_CHLORINE.id -> Color(0.8f, 0.95f, 0.2f, alpha)
            else -> Color(0.7f, 0.7f, 0.9f, alpha)
        }
    }

    fun liquidColor(liquidId: String, alpha: Float): Color {
        return when (liquidId) {
            OniElements.LIQUID_WATER -> Color(0.2f, 0.5f, 1.0f, alpha)
            OniElements.LIQUID_POLLUTED_WATER -> Color(0.4f, 0.7f, 0.2f, alpha)
            OniElements.LIQUID_CRUDE_OIL -> Color(0.2f, 0.15f, 0.1f, alpha)
            OniElements.LIQUID_LAVA -> Color(1.0f, 0.4f, 0.1f, alpha)
            OniElements.LIQUID_SALT_WATER -> Color(0.3f, 0.6f, 1.0f, alpha)
            OniElements.LIQUID_BRINE -> Color(0.4f, 0.6f, 0.9f, alpha)
            OniElements.LIQUID_ETHANOL -> Color(0.9f, 0.6f, 0.2f, alpha)
            OniElements.LIQUID_PETROLEUM -> Color(0.2f, 0.1f, 0.05f, alpha)
            OniElements.LIQUID_MILK -> Color(0.9f, 0.9f, 0.85f, alpha)
            OniElements.LIQUID_NATURAL_RESIN -> Color(0.9f, 0.8f, 0.2f, alpha)
            OniElements.LIQUID_PHYTO_OIL -> Color(0.4f, 0.8f, 0.2f, alpha)
            OniElements.LIQUID_MOLTEN_GLASS -> Color(0.7f, 0.9f, 1.0f, alpha)
            OniElements.LIQUID_SUPER_COOLANT -> Color(0.4f, 0.9f, 0.95f, alpha)
            OniElements.LIQUID_VISCO_GEL -> Color(0.3f, 0.8f, 0.7f, alpha)
            else -> Color(0.5f, 0.7f, 0.9f, alpha)
        }
    }
}
