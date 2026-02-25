package mconi.common.sim

class OniSimulationSnapshot(
    private val running: Boolean,
    private val serverTicks: Long,
    private val simulationTicks: Long,
    private val lastSimulationTick: Long,
    private val tickInterval: Int,
    private val cellSize: Int,
    private val activeCells: Int,
    private val powerGenerationW: Double,
    private val powerDemandW: Double,
    private val storedEnergyJ: Double,
    private val powerTripped: Boolean,
    private val colonyStress: Double,
) {
    fun running(): Boolean = running
    fun serverTicks(): Long = serverTicks
    fun simulationTicks(): Long = simulationTicks
    fun lastSimulationTick(): Long = lastSimulationTick
    fun tickInterval(): Int = tickInterval
    fun cellSize(): Int = cellSize
    fun activeCells(): Int = activeCells
    fun powerGenerationW(): Double = powerGenerationW
    fun powerDemandW(): Double = powerDemandW
    fun storedEnergyJ(): Double = storedEnergyJ
    fun powerTripped(): Boolean = powerTripped
    fun colonyStress(): Double = colonyStress
}
