package mconi.common.sim.model

import java.util.EnumMap

class OniCellState {
    private val gasMassKg: EnumMap<GasSpecies, Double> = EnumMap(GasSpecies::class.java)
    private var occupancyState: OccupancyState = OccupancyState.VACUUM
    private var fluidSpecies: FluidSpecies = FluidSpecies.NONE
    private var fluidMassKg: Double = 0.0
    private var temperatureK: Double = 293.15
    private var pressureKpa: Double = 0.0
    private var o2Fraction: Double = 0.0
    private var co2Fraction: Double = 0.0
    private var breathingBand: BreathingBand = BreathingBand.CRITICAL

    init {
        for (species in GasSpecies.values()) {
            gasMassKg[species] = 0.0
        }
    }

    fun occupancyState(): OccupancyState = occupancyState
    fun fluidSpecies(): FluidSpecies = fluidSpecies
    fun fluidMassKg(): Double = fluidMassKg
    fun temperatureK(): Double = temperatureK
    fun pressureKpa(): Double = pressureKpa
    fun o2Fraction(): Double = o2Fraction
    fun co2Fraction(): Double = co2Fraction
    fun breathingBand(): BreathingBand = breathingBand

    fun gasMassKg(species: GasSpecies): Double = gasMassKg[species] ?: 0.0

    fun setOccupancyState(occupancyState: OccupancyState) {
        this.occupancyState = occupancyState
    }

    fun setFluidState(species: FluidSpecies, massKg: Double) {
        this.fluidSpecies = species
        this.fluidMassKg = maxOf(0.0, massKg)
    }

    fun setGasMassKg(species: GasSpecies, massKg: Double) {
        gasMassKg[species] = maxOf(0.0, massKg)
    }

    fun setTemperatureK(temperatureK: Double) {
        this.temperatureK = temperatureK
    }

    fun setPressureKpa(pressureKpa: Double) {
        this.pressureKpa = maxOf(0.0, pressureKpa)
    }

    fun setO2Fraction(o2Fraction: Double) {
        this.o2Fraction = o2Fraction.coerceIn(0.0, 1.0)
    }

    fun setCO2Fraction(co2Fraction: Double) {
        this.co2Fraction = co2Fraction.coerceIn(0.0, 1.0)
    }

    fun setBreathingBand(breathingBand: BreathingBand) {
        this.breathingBand = breathingBand
    }

    fun totalGasMassKg(): Double {
        var total = 0.0
        for (mass in gasMassKg.values) {
            total += mass
        }
        return total
    }
}
