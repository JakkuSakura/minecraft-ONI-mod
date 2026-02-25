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

    fun tasks(): List<BuildTask> = queue.toList()

    fun activeCount(): Int = queue.size

    fun clearCompleted() {
        queue.removeIf { it.progressSeconds >= it.buildTimeSeconds.toDouble() }
    }

    fun clear() {
        queue.clear()
    }
}
