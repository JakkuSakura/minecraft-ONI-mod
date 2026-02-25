package mconi.common.sim.model;

public enum PressureBand
{
	VACUUM,
	THIN,
	BREATHABLE,
	OVERPRESSURE;

	public static PressureBand fromKpa(double pressureKpa)
	{
		if (pressureKpa < 20.0D) { return VACUUM; }
		if (pressureKpa < 70.0D) { return THIN; }
		if (pressureKpa <= 180.0D) { return BREATHABLE; }
		return OVERPRESSURE;
	}
}
