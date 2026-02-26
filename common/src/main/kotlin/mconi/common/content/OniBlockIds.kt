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

    @JvmField
    val ALL = listOf(
        REGOLITH,
        SEDIMENTARY_ROCK,
        IGNEOUS_ROCK,
        GRANITE,
        ABYSSALITE,
        ALGAE,
        POLLUTED_DIRT,
        PRINTING_POD
    )
}
