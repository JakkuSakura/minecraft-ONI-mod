package mconi.common.sim.subsystem;

import mconi.common.sim.OniSimulationConfig;
import mconi.common.sim.OniSimulationGrid;

public class SimulationContext
{
	private final long serverTick;
	private final OniSimulationConfig config;
	private final OniSimulationGrid grid;

	public SimulationContext(long serverTick, OniSimulationConfig config, OniSimulationGrid grid)
	{
		this.serverTick = serverTick;
		this.config = config;
		this.grid = grid;
	}

	public long serverTick() { return serverTick; }
	public OniSimulationConfig config() { return config; }
	public OniSimulationGrid grid() { return grid; }
}
