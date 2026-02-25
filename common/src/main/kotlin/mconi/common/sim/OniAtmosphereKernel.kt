package mconi.common.sim

import mconi.common.sim.model.FluidSpecies
import mconi.common.sim.model.OccupancyState
import mconi.common.sim.model.OniCellState

/**
 * Initial atmosphere kernel pass: resolves occupancy and computes pressure from mass.
 */
class OniAtmosphereKernel {
    fun run(grid: OniSimulationGrid, config: OniSimulationConfig) {
        val cellVolume = Math.pow(config.cellSize().toDouble(), 3.0)
        for (cell in grid.cells()) {
            updateCell(cell, cellVolume)
        }
    }

    private fun updateCell(cell: OniCellState, cellVolumeM3: Double) {
        val totalGasMassKg = cell.totalGasMassKg()
        val scaledDensity = totalGasMassKg / maxOf(0.0001, cellVolumeM3)
        val pressureKpa = (scaledDensity / STANDARD_AIR_DENSITY_KG_PER_M3) *
            (cell.temperatureK() / STANDARD_TEMPERATURE_K) *
            STANDARD_PRESSURE_KPA
        cell.setPressureKpa(pressureKpa)

        if (cell.fluidSpecies() != FluidSpecies.NONE && cell.fluidMassKg() > 0.0) {
            cell.setOccupancyState(OccupancyState.FLUID)
            return
        }

        if (totalGasMassKg > 0.0) {
            cell.setOccupancyState(OccupancyState.GAS)
            return
        }

        cell.setOccupancyState(OccupancyState.VACUUM)
    }

    companion object {
        private const val STANDARD_TEMPERATURE_K = 293.15
        private const val STANDARD_PRESSURE_KPA = 101.325
        private const val STANDARD_AIR_DENSITY_KG_PER_M3 = 1.225
    }
}
