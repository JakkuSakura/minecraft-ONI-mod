package mconi.common.sim.subsystem;

public interface SimulationSubsystem
{
	String id();
	void run(SimulationContext context);
}
