package mconi.common.sim.subsystem

class PowerSubsystem : SimulationSubsystem {
    override fun id(): String = "power"

    override fun run(context: SimulationContext) {
        val runtime = context.runtime()
        val state = runtime.powerState()
        val activeCells = context.grid().activeCellCount().toDouble()

        if (state.generationW() == 0.0 && state.demandW() == 0.0) {
            state.setGenerationW(activeCells * 12.0)
            state.setDemandW(activeCells * 10.0)
        }

        val deltaW = state.generationW() - state.demandW()
        val nextStored = (state.storedEnergyJ() + deltaW * context.config().tickInterval()).coerceIn(0.0, 5_000_000.0)
        state.setStoredEnergyJ(nextStored)

        val overload = state.demandW() > (state.generationW() + 200.0)
        state.setTripped(overload && state.storedEnergyJ() <= 0.0)
    }
}
