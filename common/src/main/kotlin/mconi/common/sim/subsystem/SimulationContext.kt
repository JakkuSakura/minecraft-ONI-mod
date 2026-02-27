package mconi.common.sim.subsystem

import mconi.common.sim.OniSimulationConfig
import mconi.common.sim.OniSimulationRuntime
import net.minecraft.server.level.ServerLevel

class SimulationContext(
    private val serverTick: Long,
    private val config: OniSimulationConfig,
    private val level: ServerLevel,
    private val runtime: OniSimulationRuntime,
) {
    fun serverTick(): Long = serverTick
    fun config(): OniSimulationConfig = config
    fun level(): ServerLevel = level
    fun runtime(): OniSimulationRuntime = runtime
}
