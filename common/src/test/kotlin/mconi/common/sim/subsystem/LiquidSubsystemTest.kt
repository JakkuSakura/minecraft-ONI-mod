package mconi.common.sim.subsystem

import mconi.common.sim.OniSimulationRuntime
import mconi.common.element.OniElements
import mconi.common.sim.model.OccupancyState
import kotlin.test.Test
import kotlin.test.assertTrue

class LiquidSubsystemTest {
    @Test
    fun movesLiquidDownward() {
        val runtime = OniSimulationRuntime()
        runtime.bootstrap()
        val config = runtime.config()
        config.setLiquidTransferKgPerStep(500.0)
        val grid = runtime.grid()
        val cellSize = config.cellSize()
        val top = grid.getOrCreateCellAtBlock(0, 4, 0, cellSize)
        val bottom = grid.getOrCreateCellAtBlock(0, 0, 0, cellSize)
        top.setOccupancyState(OccupancyState.LIQUID)
        top.setLiquidState(OniElements.LIQUID_WATER, 1000.0)
        bottom.setOccupancyState(OccupancyState.VACUUM)
        bottom.setLiquidState(OniElements.LIQUID_NONE, 0.0)
        val context = SimulationContext(0L, config, grid, runtime)

        LiquidSubsystem().run(context)

        assertTrue(bottom.liquidMassKg() > 0.0)
        assertTrue(top.liquidMassKg() < 1000.0)
    }
}
