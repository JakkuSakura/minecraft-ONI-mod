package mconi.common.sim

class OniPowerState {
    private var generationW: Double = 0.0
    private var demandW: Double = 0.0
    private var storedEnergyJ: Double = 0.0
    private var tripped: Boolean = false

    fun generationW(): Double = generationW
    fun demandW(): Double = demandW
    fun storedEnergyJ(): Double = storedEnergyJ
    fun tripped(): Boolean = tripped

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
}
