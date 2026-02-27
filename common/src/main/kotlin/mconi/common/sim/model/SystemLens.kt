package mconi.common.sim.model

enum class SystemLens {
    ATMOSPHERE,
    LIQUID,
    THERMAL,
    OXYGEN,
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
            return try {
                valueOf(normalized)
            } catch (_: IllegalArgumentException) {
                null
            }
        }
    }
}
