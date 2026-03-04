package conservecraft.common.sim

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Central service registry for ONI world systems.
 */
object OniServices {
    private val started = AtomicBoolean(false)
    private val systemRuntime = OniSystemRuntime()

    @JvmStatic
    fun bootstrap() {
        if (started.compareAndSet(false, true)) {
            systemRuntime.bootstrap()
        }
    }

    @JvmStatic
    fun systemRuntime(): OniSystemRuntime = systemRuntime
}
