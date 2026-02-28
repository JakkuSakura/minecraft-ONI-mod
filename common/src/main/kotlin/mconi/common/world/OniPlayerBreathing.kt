package mconi.common.world

import mconi.common.sim.model.BreathingBand
import mconi.common.world.OniMatterAccess
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects

object OniPlayerBreathing {
    private const val EFFECT_DURATION_TICKS = 60
    private const val DAMAGE_INTERVAL_TICKS = 40

    @JvmStatic
    fun apply(level: ServerLevel) {
        val gameTime = level.gameTime
        for (player: ServerPlayer in level.players()) {
            val pos = player.blockPosition()
            val band = breathingBandAt(level, pos)
            if (band == BreathingBand.HEALTHY) {
                continue
            }
            if (band == BreathingBand.STRESSED) {
                player.addEffect(MobEffectInstance(MobEffects.SLOWNESS, EFFECT_DURATION_TICKS, 0, true, false))
                player.addEffect(MobEffectInstance(MobEffects.WEAKNESS, EFFECT_DURATION_TICKS, 0, true, false))
            } else {
                player.addEffect(MobEffectInstance(MobEffects.SLOWNESS, EFFECT_DURATION_TICKS, 1, true, false))
                player.addEffect(MobEffectInstance(MobEffects.WEAKNESS, EFFECT_DURATION_TICKS, 1, true, false))
                if (gameTime % DAMAGE_INTERVAL_TICKS == 0L) {
                    player.hurt(level.damageSources().drown(), 1.0f)
                }
            }
        }
    }

    private fun breathingBandAt(level: ServerLevel, pos: net.minecraft.core.BlockPos): BreathingBand {
        val state = level.getBlockState(pos)
        val gas = OniMatterAccess.gasSpec(state) ?: return BreathingBand.CRITICAL
        val entity = OniMatterAccess.matterEntity(level, pos) ?: return BreathingBand.CRITICAL
        if (entity.massKg() <= 0.0) {
            return BreathingBand.CRITICAL
        }
        return if (gas == mconi.common.element.OniElements.GAS_OXYGEN) {
            BreathingBand.HEALTHY
        } else {
            BreathingBand.CRITICAL
        }
    }
}
