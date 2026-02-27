package mconi.common.content

object OniBlockIds {
    const val REGOLITH = "regolith"
    const val SEDIMENTARY_ROCK = "sedimentary_rock"
    const val IGNEOUS_ROCK = "igneous_rock"
    const val GRANITE = "granite"
    const val ABYSSALITE = "abyssalite"
    const val ALGAE = "algae"
    const val POLLUTED_DIRT = "polluted_dirt"
    const val PRINTING_POD = "printing_pod"
    const val OXYGEN_GAS = "oxygen_gas"
    const val CARBON_DIOXIDE_GAS = "carbon_dioxide_gas"
    const val HYDROGEN_GAS = "hydrogen_gas"
    const val WATER = "water"
    const val POLLUTED_WATER = "polluted_water"
    const val CRUDE_OIL = "crude_oil"
    const val LAVA = "lava"

    @JvmField
    val SOLIDS = listOf(
        REGOLITH,
        SEDIMENTARY_ROCK,
        IGNEOUS_ROCK,
        GRANITE,
        ABYSSALITE,
        ALGAE,
        POLLUTED_DIRT,
        PRINTING_POD
    )

    @JvmField
    val GASES = listOf(
        OXYGEN_GAS,
        CARBON_DIOXIDE_GAS,
        HYDROGEN_GAS
    )

    @JvmField
    val FLUIDS = listOf(
        WATER,
        POLLUTED_WATER,
        CRUDE_OIL,
        LAVA
    )

    @JvmField
    val ALL = SOLIDS + GASES + FLUIDS
}
