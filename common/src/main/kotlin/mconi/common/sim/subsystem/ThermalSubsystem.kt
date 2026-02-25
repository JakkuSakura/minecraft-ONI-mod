package mconi.common.sim.subsystem

import mconi.common.sim.model.FluidSpecies
import mconi.common.sim.model.OccupancyState

class ThermalSubsystem : SimulationSubsystem {
    override fun id(): String = "thermal"

    override fun run(context: SimulationContext) {
        for (cell in context.grid().cells()) {
            val temp = cell.temperatureK()
            val occupancy = cell.occupancyState()
            var next = temp

            if (occupancy == OccupancyState.VACUUM || occupancy == OccupancyState.VOID) {
                next += (260.0 - temp) * 0.02
            }

            if (cell.fluidSpecies() == FluidSpecies.LAVA && cell.fluidMassKg() > 0.0) {
                next += 0.8
            }

            cell.setTemperatureK(next)
        }
    }
}
