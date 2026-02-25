package mconi.common.sim

class OniStressState {
    private var score: Double = 0.0

    fun score(): Double = score

    fun setScore(score: Double) {
        this.score = score.coerceIn(0.0, 100.0)
    }
}
