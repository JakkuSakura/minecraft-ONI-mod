package mconi.common.sim.subsystem

import mconi.common.sim.OniSimulationRuntime
import mconi.common.sim.model.BreathingBand
import kotlin.test.Test
import kotlin.test.assertTrue

class StressSubsystemTest {
    @Test
    fun stressIncreasesInCriticalConditions() {
        val runtime = OniSimulationRuntime()
        runtime.bootstrap()
        val cell = runtime.grid().getOrCreateCellAtBlock(0, 0, 0, runtime.config().cellSize())
        cell.setBreathingBand(BreathingBand.CRITICAL)
        cell.setTemperatureK(340.0)
        val context = SimulationContext(0L, runtime.config(), runtime.grid(), runtime)

        StressSubsystem().run(context)

        assertTrue(runtime.stressState().score() > 0.0)
    }
}
