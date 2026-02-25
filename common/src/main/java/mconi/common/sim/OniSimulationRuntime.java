package mconi.common.sim;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Server-authoritative runtime loop controller for ONI simulation systems.
 */
public class OniSimulationRuntime
{
	private final OniSimulationConfig config = new OniSimulationConfig();
	private final AtomicLong serverTicks = new AtomicLong(0L);
	private final AtomicLong simulationTicks = new AtomicLong(0L);
	private final AtomicLong lastSimulationTick = new AtomicLong(-1L);
	private volatile boolean running;

	public void bootstrap()
	{
		this.running = false;
	}

	public OniSimulationConfig config()
	{
		return config;
	}

	public void onServerStarted()
	{
		serverTicks.set(0L);
		simulationTicks.set(0L);
		lastSimulationTick.set(-1L);
		running = true;
	}

	public void onServerStopped()
	{
		running = false;
	}

	public void onServerTick()
	{
		long tick = serverTicks.incrementAndGet();
		if (!running)
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
		this.running = running;
	}

	public void runOneSimulationStep(long serverTick)
	{
		lastSimulationTick.set(serverTick);
		simulationTicks.incrementAndGet();
	}

	public OniSimulationSnapshot snapshot()
	{
		return new OniSimulationSnapshot(
				running,
				serverTicks.get(),
				simulationTicks.get(),
				lastSimulationTick.get(),
				config.tickInterval(),
				config.cellSize());
	}
}
