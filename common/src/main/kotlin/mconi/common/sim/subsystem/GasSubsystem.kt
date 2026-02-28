package mconi.common.sim.subsystem

import mconi.common.sim.OniAtmosphereKernel

class GasSubsystem : SimulationSubsystem {
    private val kernel = OniAtmosphereKernel()

    override fun id(): String = "gas"

    override fun run(context: SimulationContext) {
        kernel.run(context.level(), context.config())
    }
}
