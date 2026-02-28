package mconi.common.sim

class OniSystemSnapshot(
    private val running: Boolean,
    private val serverTicks: Long,
    private val systemTicks: Long,
    private val lastSystemTick: Long,
    private val tickInterval: Int,
    private val cellSize: Int,
    private val activeCells: Int,
    private val powerGenerationW: Double,
    private val powerDemandW: Double,
    private val storedEnergyJ: Double,
    private val powerTripped: Boolean,
    private val colonyStress: Double,
    private val unlockedResearchCount: Int,
    private val activeConstructionCount: Int,
) {
    fun running(): Boolean = running
    fun serverTicks(): Long = serverTicks
    fun systemTicks(): Long = systemTicks
    fun lastSystemTick(): Long = lastSystemTick
    fun tickInterval(): Int = tickInterval
    fun cellSize(): Int = cellSize
    fun activeCells(): Int = activeCells
    fun powerGenerationW(): Double = powerGenerationW
    fun powerDemandW(): Double = powerDemandW
    fun storedEnergyJ(): Double = storedEnergyJ
    fun powerTripped(): Boolean = powerTripped
    fun colonyStress(): Double = colonyStress
    fun unlockedResearchCount(): Int = unlockedResearchCount
    fun activeConstructionCount(): Int = activeConstructionCount
}
