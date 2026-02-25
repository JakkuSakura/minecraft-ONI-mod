package mconi.common.sim

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Central service registry for ONI simulation systems.
 */
object OniServices {
    private val initialized = AtomicBoolean(false)
    private val simulationRuntime = OniSimulationRuntime()

    @JvmStatic
    fun bootstrap() {
        if (initialized.compareAndSet(false, true)) {
            simulationRuntime.bootstrap()
        }
    }

    @JvmStatic
    fun simulationRuntime(): OniSimulationRuntime = simulationRuntime
}
