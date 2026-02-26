package mconi.common.sim.subsystem

import mconi.common.sim.OniSimulationRuntime
import mconi.common.sim.model.BreathingBand
import mconi.common.sim.model.GasSpecies
import mconi.common.sim.model.OccupancyState
import kotlin.test.Test
import kotlin.test.assertEquals

class OxygenSubsystemTest {
    @Test
    fun computesBreathingBands() {
        val runtime = OniSimulationRuntime()
        runtime.bootstrap()
        val cell = runtime.grid().getOrCreateCellAtBlock(0, 0, 0, runtime.config().cellSize())
        cell.setOccupancyState(OccupancyState.GAS)
        cell.setGasMassKg(GasSpecies.O2, 8.0)
        cell.setGasMassKg(GasSpecies.CO2, 0.2)
        cell.setPressureKpa(100.0)
        val context = SimulationContext(0L, runtime.config(), runtime.grid(), runtime)

        OxygenSubsystem().run(context)

        assertEquals(BreathingBand.HEALTHY, cell.breathingBand())
    }

    @Test
    fun criticalWhenLowOxygen() {
        val runtime = OniSimulationRuntime()
        runtime.bootstrap()
        val cell = runtime.grid().getOrCreateCellAtBlock(0, 0, 0, runtime.config().cellSize())
        cell.setOccupancyState(OccupancyState.GAS)
        cell.setGasMassKg(GasSpecies.O2, 1.0)
        cell.setGasMassKg(GasSpecies.CO2, 8.0)
        cell.setPressureKpa(10.0)
        val context = SimulationContext(0L, runtime.config(), runtime.grid(), runtime)

        OxygenSubsystem().run(context)

        assertEquals(BreathingBand.CRITICAL, cell.breathingBand())
    }
}
