package mconi.common.sim.subsystem

import mconi.common.sim.OniConstructionState
import mconi.common.sim.OniSimulationRuntime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ResearchConstructionSubsystemTest {
    @Test
    fun advancesWhenUnblocked() {
        val runtime = OniSimulationRuntime()
        runtime.bootstrap()
        val task = OniConstructionState.BuildTask(
            blueprintId = "mconi:test",
            requiredResearch = "power",
            requiredMaterialUnits = 10,
            buildTimeSeconds = 10,
        )
        task.depositedMaterials = 10
        runtime.researchState().unlock("power")
        runtime.constructionState().queueTask(task)
        runtime.stressState().setScore(0.0)
        runtime.powerState().setTripped(false)
        val context = SimulationContext(0L, runtime.config(), runtime.grid(), runtime)

        ResearchConstructionSubsystem().run(context)

        assertTrue(task.progressSeconds > 0.0)
        assertEquals("", task.pausedReason)
    }
}
