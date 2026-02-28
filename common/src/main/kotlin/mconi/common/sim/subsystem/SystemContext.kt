package mconi.common.sim.subsystem

import mconi.common.sim.OniSystemConfig
import mconi.common.sim.OniSystemRuntime
import net.minecraft.server.level.ServerLevel

class SystemContext(
    private val serverTick: Long,
    private val config: OniSystemConfig,
    private val level: ServerLevel,
    private val runtime: OniSystemRuntime,
) {
    fun serverTick(): Long = serverTick
    fun config(): OniSystemConfig = config
    fun level(): ServerLevel = level
    fun runtime(): OniSystemRuntime = runtime
}
