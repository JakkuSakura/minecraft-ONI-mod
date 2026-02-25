package mconi.common.sim.model

enum class SystemLens {
    ATMOSPHERE,
    FLUID,
    THERMAL,
    OXYGEN,
    POWER,
    STRESS,
    RESEARCH,
    CONSTRUCTION;

    companion object {
        @JvmStatic
        fun fromInput(input: String): SystemLens? {
            return try {
                valueOf(input.uppercase())
            } catch (_: IllegalArgumentException) {
                null
            }
        }
    }
}
