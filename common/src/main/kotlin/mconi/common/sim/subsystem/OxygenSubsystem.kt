package mconi.common.sim.subsystem

import mconi.common.sim.model.BreathingBand
import mconi.common.sim.model.OccupancyState
import mconi.common.sim.model.PressureBand
import mconi.common.element.OniElements

class OxygenSubsystem : SimulationSubsystem {
    override fun id(): String = "oxygen"

    override fun run(context: SimulationContext) {
        for (cell in context.grid().cells()) {
            if (cell.occupancyState() != OccupancyState.GAS) {
                cell.setO2Fraction(0.0)
                cell.setCO2Fraction(0.0)
                cell.setBreathingBand(BreathingBand.CRITICAL)
                continue
            }

            val total = cell.totalGasMassKg()
            if (total <= 0.0) {
                cell.setO2Fraction(0.0)
                cell.setCO2Fraction(0.0)
                cell.setBreathingBand(BreathingBand.CRITICAL)
                continue
            }

            val o2Fraction = cell.gasMassKg(OniElements.GAS_OXYGEN) / total
            val co2Fraction = cell.gasMassKg(OniElements.GAS_CARBON_DIOXIDE) / total
            cell.setO2Fraction(o2Fraction)
            cell.setCO2Fraction(co2Fraction)

            val pressureBand = PressureBand.fromKpa(cell.pressureKpa())
            val band = when {
                pressureBand == PressureBand.VACUUM -> BreathingBand.CRITICAL
                o2Fraction >= 0.18 && co2Fraction < 0.06 && pressureBand == PressureBand.BREATHABLE -> BreathingBand.HEALTHY
                o2Fraction >= 0.12 && co2Fraction < 0.12 -> BreathingBand.STRESSED
                pressureBand == PressureBand.THIN -> BreathingBand.STRESSED
                else -> BreathingBand.CRITICAL
            }
            cell.setBreathingBand(band)
        }
    }
}
