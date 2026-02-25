package mconi.common.sim.subsystem

import mconi.common.sim.model.BreathingBand

class StressSubsystem : SimulationSubsystem {
    override fun id(): String = "stress"

    override fun run(context: SimulationContext) {
        val runtime = context.runtime()
        val stress = runtime.stressState()

        val cells = context.grid().cells()
        if (cells.isEmpty()) {
            stress.setScore((stress.score() - 0.2).coerceAtLeast(0.0))
            return
        }

        var delta = 0.0
        for (cell in cells) {
            delta += when (cell.breathingBand()) {
                BreathingBand.HEALTHY -> -0.02
                BreathingBand.STRESSED -> 0.05
                BreathingBand.CRITICAL -> 0.15
            }

            val temp = cell.temperatureK()
            if (temp > 330.0 || temp < 265.0) {
                delta += 0.03
            }
        }

        val normalizedDelta = delta / cells.size.toDouble()
        stress.setScore(stress.score() + normalizedDelta * 10.0)
    }
}
