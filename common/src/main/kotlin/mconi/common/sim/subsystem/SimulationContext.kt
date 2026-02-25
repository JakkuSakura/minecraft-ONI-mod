package mconi.common.sim.subsystem

import mconi.common.sim.OniSimulationConfig
import mconi.common.sim.OniSimulationGrid
import mconi.common.sim.OniSimulationRuntime

class SimulationContext(
    private val serverTick: Long,
    private val config: OniSimulationConfig,
    private val grid: OniSimulationGrid,
    private val runtime: OniSimulationRuntime,
) {
    fun serverTick(): Long = serverTick
    fun config(): OniSimulationConfig = config
    fun grid(): OniSimulationGrid = grid
    fun runtime(): OniSimulationRuntime = runtime
}
