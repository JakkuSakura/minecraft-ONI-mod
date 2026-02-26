package mconi.common.sim.subsystem

class PowerSubsystem : SimulationSubsystem {
    override fun id(): String = "power"

    override fun run(context: SimulationContext) {
        val runtime = context.runtime()
        val state = runtime.powerState()
        val generatorCount = state.generatorCount().coerceAtLeast(0)
        val consumerCount = state.consumerCount().coerceAtLeast(0)
        val batteryCount = state.batteryCount().coerceAtLeast(0)
        val wireCount = state.wireCount().coerceAtLeast(0)

        val generation = generatorCount * GENERATION_PER_NODE_W
        val demand = consumerCount * DEMAND_PER_NODE_W
        val wireCapacity = wireCount * WIRE_CAPACITY_W

        state.setGenerationW(generation)
        state.setDemandW(demand)

        val effectiveDemand = if (wireCapacity > 0.0) minOf(demand, wireCapacity) else demand
        val deltaW = generation - effectiveDemand
        val maxStored = batteryCount * BATTERY_CAPACITY_J
        val nextStored = (state.storedEnergyJ() + deltaW * context.config().tickInterval()).coerceIn(0.0, maxStored)
        state.setStoredEnergyJ(nextStored)

        val overload = demand > wireCapacity && wireCapacity > 0.0
        val deficit = demand > generation && state.storedEnergyJ() <= 0.0
        state.setTripped(overload || deficit)
    }

    companion object {
        private const val GENERATION_PER_NODE_W = 500.0
        private const val DEMAND_PER_NODE_W = 350.0
        private const val WIRE_CAPACITY_W = 800.0
        private const val BATTERY_CAPACITY_J = 50_000.0
    }
}
