package mconi.common.sim;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Server-authoritative runtime loop controller for ONI simulation systems.
 */
public class OniSimulationRuntime
{
	private final OniSimulationConfig config = new OniSimulationConfig();
	private final OniSimulationGrid grid = new OniSimulationGrid();
	private final AtomicLong serverTicks = new AtomicLong(0L);
	private final AtomicLong simulationTicks = new AtomicLong(0L);
	private final AtomicLong lastSimulationTick = new AtomicLong(-1L);
	private volatile boolean started;
	private volatile boolean paused;

	public void bootstrap()
	{
		this.started = false;
		this.paused = false;
	}

	public OniSimulationConfig config()
	{
		return config;
	}

	public OniSimulationGrid grid()
	{
		return grid;
	}

	public void onServerStarted()
	{
		serverTicks.set(0L);
		simulationTicks.set(0L);
		lastSimulationTick.set(-1L);
		started = true;
		paused = false;
	}

	public void onServerStopped()
	{
		started = false;
		paused = false;
		grid.clear();
	}

	public void onServerTick()
	{
		if (!started)
		{
			onServerStarted();
		}

		long tick = serverTicks.incrementAndGet();
		if (paused)
		{
			return;
		}

		if ((tick % config.tickInterval()) == 0L)
		{
			runOneSimulationStep(tick);
		}
	}

	public void setRunning(boolean running)
	{
		if (!started && running)
		{
			onServerStarted();
		}
		this.paused = !running;
	}

	public void runOneSimulationStep(long serverTick)
	{
		lastSimulationTick.set(serverTick);
		simulationTicks.incrementAndGet();
	}

	public OniSimulationSnapshot snapshot()
	{
		return new OniSimulationSnapshot(
				started && !paused,
				serverTicks.get(),
				simulationTicks.get(),
				lastSimulationTick.get(),
				config.tickInterval(),
				config.cellSize(),
				grid.activeCellCount());
	}
}
