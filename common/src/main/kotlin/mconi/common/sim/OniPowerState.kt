package mconi.common.sim

class OniPowerState {
    private var generationW: Double = 0.0
    private var demandW: Double = 0.0
    private var storedEnergyJ: Double = 0.0
    private var tripped: Boolean = false
    private var batteryEnergyByPos: MutableMap<Long, Double> = HashMap()
    private var consumerPoweredByPos: MutableSet<Long> = HashSet()
    private var networks: List<mconi.common.sim.power.OniPowerNetwork> = emptyList()

    fun generationW(): Double = generationW
    fun demandW(): Double = demandW
    fun storedEnergyJ(): Double = storedEnergyJ
    fun tripped(): Boolean = tripped
    fun batteryEnergyByPos(): Map<Long, Double> = batteryEnergyByPos
    fun isConsumerPowered(pos: net.minecraft.core.BlockPos): Boolean = consumerPoweredByPos.contains(pos.asLong())
    fun networks(): List<mconi.common.sim.power.OniPowerNetwork> = networks

    fun setGenerationW(generationW: Double) {
        this.generationW = maxOf(0.0, generationW)
    }

    fun setDemandW(demandW: Double) {
        this.demandW = maxOf(0.0, demandW)
    }

    fun setStoredEnergyJ(storedEnergyJ: Double) {
        this.storedEnergyJ = maxOf(0.0, storedEnergyJ)
    }

    fun setTripped(tripped: Boolean) {
        this.tripped = tripped
    }

    fun setBatteryEnergyByPos(next: Map<Long, Double>) {
        batteryEnergyByPos = HashMap(next)
    }

    fun setConsumerPoweredByPos(next: Set<Long>) {
        consumerPoweredByPos = HashSet(next)
    }

    fun setNetworks(next: List<mconi.common.sim.power.OniPowerNetwork>) {
        networks = next.toList()
    }
}
