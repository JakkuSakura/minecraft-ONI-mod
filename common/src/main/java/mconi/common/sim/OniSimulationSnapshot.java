package mconi.common.sim;

public class OniSimulationSnapshot
{
	private final boolean running;
	private final long serverTicks;
	private final long simulationTicks;
	private final long lastSimulationTick;
	private final int tickInterval;
	private final int cellSize;
	private final int activeCells;

	public OniSimulationSnapshot(
			boolean running,
			long serverTicks,
			long simulationTicks,
			long lastSimulationTick,
			int tickInterval,
			int cellSize,
			int activeCells)
	{
		this.running = running;
		this.serverTicks = serverTicks;
		this.simulationTicks = simulationTicks;
		this.lastSimulationTick = lastSimulationTick;
		this.tickInterval = tickInterval;
		this.cellSize = cellSize;
		this.activeCells = activeCells;
	}

	public boolean running() { return running; }
	public long serverTicks() { return serverTicks; }
	public long simulationTicks() { return simulationTicks; }
	public long lastSimulationTick() { return lastSimulationTick; }
	public int tickInterval() { return tickInterval; }
	public int cellSize() { return cellSize; }
	public int activeCells() { return activeCells; }
}
