package mconi.common.sim

object OniConstructionRates {
    private const val BASE_MATERIALS_PER_SECOND = 10.0
    private const val BASE_BUILD_SECONDS_PER_SECOND = 1.0

    fun speedMultiplier(stressScore: Double): Double {
        return when {
            stressScore >= 85.0 -> 0.25
            stressScore >= 60.0 -> 0.6
            stressScore >= 30.0 -> 0.85
            else -> 1.0
        }
    }

    fun materialsPerSecond(multiplier: Double): Double = BASE_MATERIALS_PER_SECOND * multiplier

    fun buildSecondsPerSecond(multiplier: Double): Double = BASE_BUILD_SECONDS_PER_SECOND * multiplier
}
