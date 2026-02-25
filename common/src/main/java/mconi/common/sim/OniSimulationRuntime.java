package mconi.common.sim;

import mconi.common.sim.subsystem.AtmosphereSubsystem;
import mconi.common.sim.subsystem.NoopSubsystem;
import mconi.common.sim.subsystem.SimulationContext;
import mconi.common.sim.subsystem.SimulationSubsystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Server-authoritative runtime loop controller for ONI simulation systems.
 */
public class OniSimulationRuntime
{
	private final OniSimulationConfig config = new OniSimulationConfig();
	private final OniSimulationGrid grid = new OniSimulationGrid();
	private final List<SimulationSubsystem> subsystems = new ArrayList<>();
	private final AtomicLong serverTicks = new AtomicLong(0L);
	private final AtomicLong simulationTicks = new AtomicLong(0L);
	private final AtomicLong lastSimulationTick = new AtomicLong(-1L);
	private volatile boolean started;
	private volatile boolean paused;

	public void bootstrap()
	{
		this.started = false;
		this.paused = false;
		subsystems.clear();
		subsystems.add(new AtmosphereSubsystem());
		subsystems.add(new NoopSubsystem("thermal"));
		subsystems.add(new NoopSubsystem("oxygen"));
		subsystems.add(new NoopSubsystem("power"));
		subsystems.add(new NoopSubsystem("stress"));
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
		SimulationContext context = new SimulationContext(serverTick, config, grid);
		for (SimulationSubsystem subsystem : subsystems)
		{
			subsystem.run(context);
		}
		lastSimulationTick.set(serverTick);
		simulationTicks.incrementAndGet();
	}

	public List<String> pipelineOrder()
	{
		List<String> order = new ArrayList<>(subsystems.size());
		for (SimulationSubsystem subsystem : subsystems)
		{
			order.add(subsystem.id());
		}
		return Collections.unmodifiableList(order);
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
