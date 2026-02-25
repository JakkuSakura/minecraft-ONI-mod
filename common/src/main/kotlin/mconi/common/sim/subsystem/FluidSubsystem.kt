package mconi.common.sim.subsystem

import mconi.common.sim.model.FluidSpecies

class FluidSubsystem : SimulationSubsystem {
    override fun id(): String = "fluid"

    override fun run(context: SimulationContext) {
        for (cell in context.grid().cells()) {
            val mass = cell.fluidMassKg()
            val species = cell.fluidSpecies()
            if (species == FluidSpecies.NONE || mass <= 0.0) {
                cell.setFluidState(FluidSpecies.NONE, 0.0)
                continue
            }

            var nextMass = mass.coerceAtMost(MAX_FLUID_MASS_PER_CELL_KG)

            // Minimal phase coupling: hot water slowly boils away from liquid storage.
            if ((species == FluidSpecies.WATER || species == FluidSpecies.POLLUTED_WATER) && cell.temperatureK() > 373.15) {
                nextMass -= BOIL_OFF_KG_PER_STEP
            }

            if (nextMass <= 0.0) {
                cell.setFluidState(FluidSpecies.NONE, 0.0)
            } else {
                cell.setFluidState(species, nextMass)
            }
        }
    }

    companion object {
        private const val MAX_FLUID_MASS_PER_CELL_KG = 4000.0
        private const val BOIL_OFF_KG_PER_STEP = 1.0
    }
}
