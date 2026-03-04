package conservecraft.common.sim

import conservecraft.common.item.OniBlueprintRegistry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class OniConstructionStateTest {
    @Test
    fun queueBlueprintUsesRegistryValues() {
        val blueprintId = OniBlueprintRegistry.allIds().firstOrNull()
        assertNotNull(blueprintId)
        val blueprint = OniBlueprintRegistry.get(blueprintId)
        assertNotNull(blueprint)

        val state = OniConstructionState()
        val task = state.queueBlueprint(blueprint.id)

        assertNotNull(task)
        assertEquals(blueprint.id, task.blueprintId)
        assertEquals(blueprint.requiredResearch, task.requiredResearch)
        assertEquals(blueprint.buildTimeSeconds, task.buildTimeSeconds)
        assertEquals(blueprint.materialSlots.sumOf { it.amount }, task.requiredMaterialUnits)
        assertEquals(1, state.activeCount())
    }

    @Test
    fun clearCompletedRemovesFinishedTasks() {
        val state = OniConstructionState()
        state.queueTask(
            OniConstructionState.BuildTask(
                blueprintId = "conservecraft:test",
                requiredResearch = "power",
                requiredMaterialUnits = 10,
                buildTimeSeconds = 5
            ).apply { progressSeconds = 5.0 }
        )
        state.queueTask(
            OniConstructionState.BuildTask(
                blueprintId = "conservecraft:test2",
                requiredResearch = "oxygen",
                requiredMaterialUnits = 5,
                buildTimeSeconds = 5
            )
        )

        state.clearCompleted()

        assertEquals(1, state.activeCount())
    }
}
