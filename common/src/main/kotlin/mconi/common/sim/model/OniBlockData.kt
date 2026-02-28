package mconi.common.sim.model

import mconi.common.element.OniElements

class OniBlockData {
    private val gasMassKg: MutableMap<OniElements.GasSpec, Double> = LinkedHashMap()
    private val gasFraction: MutableMap<OniElements.GasSpec, Double> = LinkedHashMap()
    private var occupancyState: OccupancyState = OccupancyState.VACUUM
    private var liquidId: String = OniElements.LIQUID_NONE
    private var liquidMassKg: Double = 0.0
    private var temperatureK: Double = 293.15
    private var pressureKpa: Double = 0.0
    private var breathingBand: BreathingBand = BreathingBand.CRITICAL
    private var overheated: Boolean = false

    init {
        for (species in OniElements.GASES) {
            gasMassKg[species] = 0.0
            gasFraction[species] = 0.0
        }
    }

    fun occupancyState(): OccupancyState = occupancyState
    fun liquidId(): String = liquidId
    fun liquidMassKg(): Double = liquidMassKg
    fun temperatureK(): Double = temperatureK
    fun pressureKpa(): Double = pressureKpa
    fun breathingBand(): BreathingBand = breathingBand
    fun overheated(): Boolean = overheated

    fun gasMassKg(species: OniElements.GasSpec): Double = gasMassKg[species] ?: 0.0
    fun gasFraction(species: OniElements.GasSpec): Double = gasFraction[species] ?: 0.0

    fun setOccupancyState(occupancyState: OccupancyState) {
        this.occupancyState = occupancyState
    }

    fun setLiquidState(liquidId: String, massKg: Double) {
        this.liquidId = liquidId
        this.liquidMassKg = maxOf(0.0, massKg)
    }

    fun setGasMassKg(species: OniElements.GasSpec, massKg: Double) {
        gasMassKg[species] = maxOf(0.0, massKg)
    }

    fun setGasFraction(species: OniElements.GasSpec, fraction: Double) {
        gasFraction[species] = fraction.coerceIn(0.0, 1.0)
    }

    fun setTemperatureK(temperatureK: Double) {
        this.temperatureK = temperatureK
    }

    fun setPressureKpa(pressureKpa: Double) {
        this.pressureKpa = maxOf(0.0, pressureKpa)
    }


    fun setBreathingBand(breathingBand: BreathingBand) {
        this.breathingBand = breathingBand
    }

    fun setOverheated(overheated: Boolean) {
        this.overheated = overheated
    }

    fun totalGasMassKg(): Double {
        var total = 0.0
        for (mass in gasMassKg.values) {
            total += mass
        }
        return total
    }
}
