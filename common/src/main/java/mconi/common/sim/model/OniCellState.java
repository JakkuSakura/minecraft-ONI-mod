package mconi.common.sim.model;

import java.util.EnumMap;

public class OniCellState
{
	private final EnumMap<GasSpecies, Double> gasMassKg = new EnumMap<>(GasSpecies.class);
	private OccupancyState occupancyState = OccupancyState.VACUUM;
	private FluidSpecies fluidSpecies = FluidSpecies.NONE;
	private double fluidMassKg;
	private double temperatureK = 293.15D;
	private double pressureKpa;

	public OniCellState()
	{
		for (GasSpecies species : GasSpecies.values())
		{
			gasMassKg.put(species, 0.0D);
		}
	}

	public OccupancyState occupancyState() { return occupancyState; }
	public FluidSpecies fluidSpecies() { return fluidSpecies; }
	public double fluidMassKg() { return fluidMassKg; }
	public double temperatureK() { return temperatureK; }
	public double pressureKpa() { return pressureKpa; }

	public double gasMassKg(GasSpecies species)
	{
		return gasMassKg.get(species);
	}

	public void setOccupancyState(OccupancyState occupancyState)
	{
		this.occupancyState = occupancyState;
	}

	public void setFluidState(FluidSpecies species, double massKg)
	{
		this.fluidSpecies = species;
		this.fluidMassKg = Math.max(0.0D, massKg);
	}

	public void setGasMassKg(GasSpecies species, double massKg)
	{
		gasMassKg.put(species, Math.max(0.0D, massKg));
	}

	public void setTemperatureK(double temperatureK)
	{
		this.temperatureK = temperatureK;
	}

	public void setPressureKpa(double pressureKpa)
	{
		this.pressureKpa = Math.max(0.0D, pressureKpa);
	}
}
