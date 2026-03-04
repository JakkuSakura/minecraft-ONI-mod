package conservecraft.common.sim.subsystem

import conservecraft.common.sim.OniSystemConfig
import conservecraft.common.sim.OniSystemRuntime
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
