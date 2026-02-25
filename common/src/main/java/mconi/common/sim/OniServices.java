package mconi.common.sim;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Central service registry for ONI simulation systems.
 */
public final class OniServices
{
	private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);
	private static final OniSimulationRuntime SIMULATION_RUNTIME = new OniSimulationRuntime();

	private OniServices() { }

	public static void bootstrap()
	{
		if (INITIALIZED.compareAndSet(false, true))
		{
			SIMULATION_RUNTIME.bootstrap();
		}
	}

	public static OniSimulationRuntime simulationRuntime()
	{
		return SIMULATION_RUNTIME;
	}
}
