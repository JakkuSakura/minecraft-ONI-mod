package mconi.common.sim;

import mconi.common.sim.model.FluidSpecies;
import mconi.common.sim.model.OccupancyState;
import mconi.common.sim.model.OniCellState;

/**
 * Initial atmosphere kernel pass: resolves occupancy and computes pressure from mass.
 */
public class OniAtmosphereKernel
{
	private static final double STANDARD_TEMPERATURE_K = 293.15D;
	private static final double STANDARD_PRESSURE_KPA = 101.325D;
	private static final double STANDARD_AIR_DENSITY_KG_PER_M3 = 1.225D;

	public void run(OniSimulationGrid grid, OniSimulationConfig config)
	{
		double cellVolume = Math.pow(config.cellSize(), 3);
		for (OniCellState cell : grid.cells())
		{
			updateCell(cell, cellVolume);
		}
	}

	private void updateCell(OniCellState cell, double cellVolumeM3)
	{
		double totalGasMassKg = cell.totalGasMassKg();
		double scaledDensity = totalGasMassKg / Math.max(0.0001D, cellVolumeM3);
		double pressureKpa = (scaledDensity / STANDARD_AIR_DENSITY_KG_PER_M3)
				* (cell.temperatureK() / STANDARD_TEMPERATURE_K)
				* STANDARD_PRESSURE_KPA;
		cell.setPressureKpa(pressureKpa);

		if (cell.fluidSpecies() != FluidSpecies.NONE && cell.fluidMassKg() > 0.0D)
		{
			cell.setOccupancyState(OccupancyState.FLUID);
			return;
		}

		if (totalGasMassKg > 0.0D)
		{
			cell.setOccupancyState(OccupancyState.GAS);
			return;
		}

		cell.setOccupancyState(OccupancyState.VACUUM);
	}
}
