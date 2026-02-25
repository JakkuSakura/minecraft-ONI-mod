package mconi.common.sim.subsystem

interface SimulationSubsystem {
    fun id(): String
    fun run(context: SimulationContext)
}
