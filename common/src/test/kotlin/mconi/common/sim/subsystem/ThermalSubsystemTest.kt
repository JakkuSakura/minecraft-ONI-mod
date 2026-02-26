package mconi.common.sim.subsystem

import mconi.common.sim.OniSimulationRuntime
import mconi.common.sim.model.OccupancyState
import kotlin.test.Test
import kotlin.test.assertTrue

class ThermalSubsystemTest {
    @Test
    fun voidCellsCoolTowardSpace() {
        val runtime = OniSimulationRuntime()
        runtime.bootstrap()
        val cell = runtime.grid().getOrCreateCellAtBlock(0, 0, 0, runtime.config().cellSize())
        cell.setOccupancyState(OccupancyState.VOID)
        cell.setTemperatureK(320.0)
        val context = SimulationContext(0L, runtime.config(), runtime.grid(), runtime)

        ThermalSubsystem().run(context)

        assertTrue(cell.temperatureK() < 320.0)
    }

    @Test
    fun flagsOverheatedSolids() {
        val runtime = OniSimulationRuntime()
        runtime.bootstrap()
        val cell = runtime.grid().getOrCreateCellAtBlock(0, 0, 0, runtime.config().cellSize())
        cell.setOccupancyState(OccupancyState.SOLID)
        cell.setTemperatureK(500.0)
        val context = SimulationContext(0L, runtime.config(), runtime.grid(), runtime)

        ThermalSubsystem().run(context)

        assertTrue(cell.overheated())
    }
}
