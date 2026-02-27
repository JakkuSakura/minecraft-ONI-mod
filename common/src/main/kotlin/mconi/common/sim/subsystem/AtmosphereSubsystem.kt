package mconi.common.sim.subsystem

import mconi.common.sim.OniAtmosphereKernel

class AtmosphereSubsystem : SimulationSubsystem {
    private val kernel = OniAtmosphereKernel()

    override fun id(): String = "atmosphere"

    override fun run(context: SimulationContext) {
        kernel.run(context.level(), context.config())
    }
}
