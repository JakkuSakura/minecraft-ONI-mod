package mconi.common.sim.subsystem;

public class NoopSubsystem implements SimulationSubsystem
{
	private final String id;

	public NoopSubsystem(String id)
	{
		this.id = id;
	}

	@Override
	public String id()
	{
		return id;
	}

	@Override
	public void run(SimulationContext context)
	{
		// intentionally empty until concrete subsystem implementation lands
	}
}
