package mconi.common.sim.model

enum class PressureBand {
    VACUUM,
    THIN,
    BREATHABLE,
    OVERPRESSURE;

    companion object {
        @JvmStatic
        fun fromKpa(pressureKpa: Double): PressureBand {
            if (pressureKpa < 20.0) {
                return VACUUM
            }
            if (pressureKpa < 70.0) {
                return THIN
            }
            if (pressureKpa <= 180.0) {
                return BREATHABLE
            }
            return OVERPRESSURE
        }
    }
}
