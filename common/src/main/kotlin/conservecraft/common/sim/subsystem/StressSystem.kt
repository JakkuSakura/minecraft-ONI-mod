package conservecraft.common.sim.subsystem

import conservecraft.common.sim.model.BreathingBand
import conservecraft.common.world.OniMatterAccess
import net.minecraft.server.level.ServerLevel

class StressSystem : OniSystem {
    override fun id(): String = "stress"

    override fun run(context: SystemContext) {
        val runtime = context.runtime()
        val stress = runtime.stressState()
        val level = context.level()
        val players = level.players()
        if (players.isEmpty()) {
            stress.setScore((stress.score() - 0.2).coerceAtLeast(0.0))
            return
        }

        var delta = 0.0
        for (player in players) {
            val pos = player.blockPosition()
            val band = breathingBandAt(level, pos)
            delta += when (band) {
                BreathingBand.HEALTHY -> -0.02
                BreathingBand.STRESSED -> 0.05
                BreathingBand.CRITICAL -> 0.15
            }
            val temp = OniMatterAccess.matterEntity(level, pos)?.temperatureK() ?: 293.15
            if (temp > 330.0 || temp < 265.0) {
                delta += 0.03
            }
        }

        val normalizedDelta = delta / players.size.toDouble()
        stress.setScore(stress.score() + normalizedDelta * 10.0)
    }

    private fun breathingBandAt(level: ServerLevel, pos: net.minecraft.core.BlockPos): BreathingBand {
        val state = level.getBlockState(pos)
        val gas = OniMatterAccess.gasSpec(state) ?: return BreathingBand.CRITICAL
        val entity = OniMatterAccess.matterEntity(level, pos) ?: return BreathingBand.CRITICAL
        if (entity.mass() <= 0.0) {
            return BreathingBand.CRITICAL
        }
        return if (gas == conservecraft.common.element.OniElements.GAS_OXYGEN) {
            BreathingBand.HEALTHY
        } else {
            BreathingBand.CRITICAL
        }
    }
}
