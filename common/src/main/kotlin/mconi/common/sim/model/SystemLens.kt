package mconi.common.sim.model

enum class SystemLens {
    ATMOSPHERE,
    LIQUID,
    THERMAL,
    GAS,
    POWER,
    STRESS,
    RESEARCH,
    CONSTRUCTION;

    companion object {
        @JvmStatic
        fun fromInput(input: String): SystemLens? {
            val normalized = input.trim().uppercase()
            if (normalized == "FLUID") {
                return LIQUID
            }
            if (normalized == "OXYGEN") {
                return GAS
            }
            return try {
                valueOf(normalized)
            } catch (_: IllegalArgumentException) {
                null
            }
        }
    }
}
