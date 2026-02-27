package mconi.common.content

object OniMaterialMass {
    const val ITEM_MASS_KG = 1

    // Values follow ONI element defaultMass from Module:Data/solid (game data).
    const val REGOLITH_DEFAULT_MASS_KG = 1000
    const val SEDIMENTARY_ROCK_DEFAULT_MASS_KG = 1840
    const val IGNEOUS_ROCK_DEFAULT_MASS_KG = 1840
    const val GRANITE_DEFAULT_MASS_KG = 1840
    const val ABYSSALITE_DEFAULT_MASS_KG = 500 // elementId=Katairite
    const val ALGAE_DEFAULT_MASS_KG = 200
    const val POLLUTED_DIRT_DEFAULT_MASS_KG = 1000 // elementId=ToxicSand
    // Values follow ONI element defaultMass from Module:Data/liquid (game data).
    const val WATER_DEFAULT_MASS_KG = 1000
    const val POLLUTED_WATER_DEFAULT_MASS_KG = 1000
    const val CRUDE_OIL_DEFAULT_MASS_KG = 870
    const val LAVA_DEFAULT_MASS_KG = 1840

    fun blockDefaultMassKg(id: String): Int {
        return when (id) {
            OniBlockIds.REGOLITH -> REGOLITH_DEFAULT_MASS_KG
            OniBlockIds.SEDIMENTARY_ROCK -> SEDIMENTARY_ROCK_DEFAULT_MASS_KG
            OniBlockIds.IGNEOUS_ROCK -> IGNEOUS_ROCK_DEFAULT_MASS_KG
            OniBlockIds.GRANITE -> GRANITE_DEFAULT_MASS_KG
            OniBlockIds.ABYSSALITE -> ABYSSALITE_DEFAULT_MASS_KG
            OniBlockIds.ALGAE -> ALGAE_DEFAULT_MASS_KG
            OniBlockIds.POLLUTED_DIRT -> POLLUTED_DIRT_DEFAULT_MASS_KG
            else -> ITEM_MASS_KG
        }
    }

    fun blockDigYieldKg(id: String): Int {
        return blockDefaultMassKg(id).coerceAtLeast(1)
    }

    fun fluidDefaultMassKg(species: mconi.common.sim.model.FluidSpecies): Int {
        return when (species) {
            mconi.common.sim.model.FluidSpecies.WATER -> WATER_DEFAULT_MASS_KG
            mconi.common.sim.model.FluidSpecies.POLLUTED_WATER -> POLLUTED_WATER_DEFAULT_MASS_KG
            mconi.common.sim.model.FluidSpecies.CRUDE_OIL -> CRUDE_OIL_DEFAULT_MASS_KG
            mconi.common.sim.model.FluidSpecies.LAVA -> LAVA_DEFAULT_MASS_KG
            else -> 0
        }
    }
}
