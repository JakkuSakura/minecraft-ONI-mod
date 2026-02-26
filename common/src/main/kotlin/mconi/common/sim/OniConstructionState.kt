package mconi.common.sim

class OniConstructionState {
    data class BuildTask(
        val blueprintId: String,
        val requiredResearch: String,
        val requiredMaterialUnits: Int,
        val buildTimeSeconds: Int,
        var depositedMaterials: Int = 0,
        var progressSeconds: Double = 0.0,
        var pausedReason: String = "Missing materials",
    )

    private val queue: MutableList<BuildTask> = ArrayList()

    fun queueTask(task: BuildTask) {
        queue.add(task)
    }

    fun queueBlueprint(id: String): BuildTask? {
        val blueprint = OniBlueprintRegistry.get(id) ?: return null
        val task = BuildTask(
            blueprintId = blueprint.id,
            requiredResearch = blueprint.requiredResearch,
            requiredMaterialUnits = blueprint.requiredMaterialUnits,
            buildTimeSeconds = blueprint.buildTimeSeconds,
        )
        queue.add(task)
        return task
    }

    fun tasks(): List<BuildTask> = queue.toList()

    fun activeCount(): Int = queue.size

    fun nextTaskNeedingMaterials(): BuildTask? {
        return queue.firstOrNull { it.depositedMaterials < it.requiredMaterialUnits }
    }

    fun clearCompleted() {
        queue.removeIf { it.progressSeconds >= it.buildTimeSeconds.toDouble() }
    }

    fun clear() {
        queue.clear()
    }
}
