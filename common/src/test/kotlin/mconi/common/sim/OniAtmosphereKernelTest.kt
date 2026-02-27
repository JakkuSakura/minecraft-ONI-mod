package mconi.common.sim

import mconi.common.element.OniElements
import mconi.common.sim.model.OccupancyState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OniAtmosphereKernelTest {
    @Test
    fun preservesSolidCells() {
        val grid = OniSimulationGrid()
        val config = OniSimulationConfig()
        val cell = grid.getOrCreateCellAtBlock(0, 0, 0, config.cellSize())
        cell.setOccupancyState(OccupancyState.SOLID)
        cell.setGasMassKg(OniElements.GAS_OXYGEN, 5.0)
        cell.setFluidState(OniElements.LIQUID_WATER, 100.0)

        OniAtmosphereKernel().run(grid, config)

        assertEquals(OccupancyState.SOLID, cell.occupancyState())
        assertEquals(0.0, cell.totalGasMassKg(), 1e-6)
        assertEquals(0.0, cell.fluidMassKg(), 1e-6)
    }

    @Test
    fun voidCellsDrainGas() {
        val grid = OniSimulationGrid()
        val config = OniSimulationConfig()
        config.setVoidGasDrainFraction(0.5)
        val cell = grid.getOrCreateCellAtBlock(0, 0, 0, config.cellSize())
        cell.setOccupancyState(OccupancyState.VOID)
        cell.setGasMassKg(OniElements.GAS_OXYGEN, 10.0)

        OniAtmosphereKernel().run(grid, config)

        assertTrue(cell.gasMassKg(OniElements.GAS_OXYGEN) < 10.0)
        assertEquals(5.0, cell.gasMassKg(OniElements.GAS_OXYGEN), 1e-6)
    }
}
