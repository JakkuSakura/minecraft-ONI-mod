package mconi.common.sim.subsystem

import mconi.common.sim.OniSimulationRuntime
import mconi.common.element.OniElements
import mconi.common.sim.model.OccupancyState
import kotlin.test.Test
import kotlin.test.assertTrue

class FluidSubsystemTest {
    @Test
    fun movesFluidDownward() {
        val runtime = OniSimulationRuntime()
        runtime.bootstrap()
        val config = runtime.config()
        config.setFluidTransferKgPerStep(500.0)
        val grid = runtime.grid()
        val cellSize = config.cellSize()
        val top = grid.getOrCreateCellAtBlock(0, 4, 0, cellSize)
        val bottom = grid.getOrCreateCellAtBlock(0, 0, 0, cellSize)
        top.setOccupancyState(OccupancyState.FLUID)
        top.setFluidState(OniElements.LIQUID_WATER, 1000.0)
        bottom.setOccupancyState(OccupancyState.VACUUM)
        bottom.setFluidState(OniElements.LIQUID_NONE, 0.0)
        val context = SimulationContext(0L, config, grid, runtime)

        FluidSubsystem().run(context)

        assertTrue(bottom.fluidMassKg() > 0.0)
        assertTrue(top.fluidMassKg() < 1000.0)
    }
}
