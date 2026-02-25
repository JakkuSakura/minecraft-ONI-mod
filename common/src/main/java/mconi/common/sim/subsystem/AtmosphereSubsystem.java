package mconi.common.sim.subsystem;

import mconi.common.sim.OniAtmosphereKernel;

public class AtmosphereSubsystem implements SimulationSubsystem
{
	private final OniAtmosphereKernel kernel = new OniAtmosphereKernel();

	@Override
	public String id()
	{
		return "atmosphere";
	}

	@Override
	public void run(SimulationContext context)
	{
		kernel.run(context.grid(), context.config());
	}
}
