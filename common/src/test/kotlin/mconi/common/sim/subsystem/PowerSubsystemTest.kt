package mconi.common.sim.subsystem

import mconi.common.sim.OniSimulationRuntime
import kotlin.test.Test
import kotlin.test.assertTrue

class PowerSubsystemTest {
    @Test
    fun initializesGenerationFromActiveCells() {
        val runtime = OniSimulationRuntime()
        runtime.bootstrap()
        runtime.grid().getOrCreateCellAtBlock(0, 0, 0, runtime.config().cellSize())
        val context = SimulationContext(0L, runtime.config(), runtime.grid(), runtime)

        PowerSubsystem().run(context)

        assertTrue(runtime.powerState().generationW() > 0.0)
        assertTrue(runtime.powerState().storedEnergyJ() >= 0.0)
    }
}
