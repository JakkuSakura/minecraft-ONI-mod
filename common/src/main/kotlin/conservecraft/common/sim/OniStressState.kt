package conservecraft.common.sim

import net.minecraft.world.entity.player.Player
import java.util.UUID

// TODO: there is an accumulated stress score, also stress accumulation rate at any given time, calculated by nearby environment and activities. This is what the stress score is updated from every tick, and also what the stress score is displayed as in the UI. The player score is what the player sees, which is either the colony score or their personal score if they have one. Personal scores are set by certain events, such as being attacked by an oni, or being in a certain area, etc. Personal scores decay over time back to the colony score.
class OniStressState {
    private var colonyScore: Double = 0.0
    private val playerScores: MutableMap<UUID, Double> = LinkedHashMap()

    fun score(): Double {
        if (playerScores.isEmpty()) {
            return colonyScore
        }
        return playerScores.values.average()
    }

    fun score(player: Player): Double {
        return playerScores.getOrPut(player.uuid) { colonyScore }
    }

    fun setScore(score: Double) {
        val clamped = score.coerceIn(0.0, 100.0)
        colonyScore = clamped
        for (entry in playerScores.entries) {
            entry.setValue(clamped)
        }
    }

    fun setScore(player: Player, score: Double) {
        playerScores[player.uuid] = score.coerceIn(0.0, 100.0)
    }
}
