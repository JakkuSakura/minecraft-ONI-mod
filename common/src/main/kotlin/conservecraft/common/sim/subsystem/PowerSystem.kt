package conservecraft.common.sim.subsystem

import conservecraft.common.sim.power.OniPowerCatalog
import conservecraft.common.sim.power.OniPowerNetworkBuilder
import conservecraft.common.sim.power.ServerLevelPowerWorldView

class PowerSystem : OniSystem {
    override fun id(): String = "power"

    override fun run(context: SystemContext) {
        val state = context.runtime().powerState()
        val level = context.level()
        val catalog = OniPowerCatalog()
        val view = ServerLevelPowerWorldView(level)
        // Build per-tick power snapshots from the current world state.
        val builder = OniPowerNetworkBuilder(view, context.config(), catalog, state.batteryEnergyByPos())
        val result = builder.build()

        // Persist aggregate metrics and per-block power state for other subsystems.
        state.setGenerationW(result.totalGenerationW)
        state.setDemandW(result.totalDemandW)
        state.setStoredEnergyJ(result.totalStoredEnergyJ)
        state.setTripped(result.tripped)
        state.setBatteryEnergyByPos(result.batteryEnergyByPos)
        state.setConsumerPoweredByPos(result.consumerPoweredByPos)
        state.setNetworks(result.networks)
    }
}
