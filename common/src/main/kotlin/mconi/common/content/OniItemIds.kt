package mconi.common.content

object OniItemIds {
    const val BOTTLED_OXYGEN = "bottled_oxygen"
    const val BOTTLED_CO2 = "bottled_co2"
    const val BOTTLED_HYDROGEN = "bottled_hydrogen"
    const val BOTTLED_WATER = "bottled_water"
    const val BOTTLED_POLLUTED_WATER = "bottled_polluted_water"

    @JvmField
    val ALL = listOf(
        BOTTLED_OXYGEN,
        BOTTLED_CO2,
        BOTTLED_HYDROGEN,
        BOTTLED_WATER,
        BOTTLED_POLLUTED_WATER
    )
}
