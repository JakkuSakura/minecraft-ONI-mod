package mconi.common.sim.subsystem

class ResearchConstructionSystem : OniSystem {
    override fun id(): String = "research_construction"

    override fun run(context: SystemContext) {
        val runtime = context.runtime()
        val research = runtime.researchState()
        val construction = runtime.constructionState()
        val stress = runtime.stressState().score()
        val powerTripped = runtime.powerState().tripped()

        val speedMultiplier = when {
            powerTripped -> 0.0
            stress >= 85.0 -> 0.25
            stress >= 60.0 -> 0.6
            stress >= 30.0 -> 0.85
            else -> 1.0
        }

        for (task in construction.tasks()) {
            if (!research.isUnlocked(task.requiredResearch)) {
                task.pausedReason = "Missing research: ${task.requiredResearch}"
                continue
            }

            if (task.depositedMaterials < task.requiredMaterialUnits) {
                task.pausedReason = "Missing materials"
                continue
            }

            if (speedMultiplier == 0.0) {
                task.pausedReason = "No power"
                continue
            }

            task.pausedReason = ""
            task.progressSeconds += speedMultiplier * (context.config().tickInterval() / 20.0)
        }

        construction.clearCompleted()
    }
}
