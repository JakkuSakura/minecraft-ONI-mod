package mconi.common.sim

class OniPowerState {
    private var generationW: Double = 0.0
    private var demandW: Double = 0.0
    private var storedEnergyJ: Double = 0.0
    private var tripped: Boolean = false
    private var generatorCount: Int = 0
    private var consumerCount: Int = 0
    private var batteryCount: Int = 0
    private var wireCount: Int = 0

    fun generationW(): Double = generationW
    fun demandW(): Double = demandW
    fun storedEnergyJ(): Double = storedEnergyJ
    fun tripped(): Boolean = tripped
    fun generatorCount(): Int = generatorCount
    fun consumerCount(): Int = consumerCount
    fun batteryCount(): Int = batteryCount
    fun wireCount(): Int = wireCount

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

    fun setGeneratorCount(generatorCount: Int) {
        this.generatorCount = generatorCount.coerceAtLeast(0)
    }

    fun setConsumerCount(consumerCount: Int) {
        this.consumerCount = consumerCount.coerceAtLeast(0)
    }

    fun setBatteryCount(batteryCount: Int) {
        this.batteryCount = batteryCount.coerceAtLeast(0)
    }

    fun setWireCount(wireCount: Int) {
        this.wireCount = wireCount.coerceAtLeast(0)
    }
}
