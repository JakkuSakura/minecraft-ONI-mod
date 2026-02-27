package mconi.common.sim

import mconi.common.sim.subsystem.AtmosphereSubsystem
import mconi.common.sim.subsystem.LiquidSubsystem
import mconi.common.sim.subsystem.OxygenSubsystem
import mconi.common.sim.subsystem.PowerSubsystem
import mconi.common.sim.subsystem.ResearchConstructionSubsystem
import mconi.common.sim.subsystem.SimulationContext
import mconi.common.sim.subsystem.SimulationSubsystem
import mconi.common.sim.subsystem.StressSubsystem
import mconi.common.sim.subsystem.ThermalSubsystem
import java.util.concurrent.atomic.AtomicLong

/**
 * Server-authoritative runtime loop controller for ONI simulation systems.
 */
class OniSimulationRuntime {
    private val config = OniSimulationConfig()
    private val subsystems: MutableList<SimulationSubsystem> = ArrayList()
    private val powerState = OniPowerState()
    private val stressState = OniStressState()
    private val researchState = OniResearchState()
    private val constructionState = OniConstructionState()
    private val serverTicks = AtomicLong(0L)
    private val simulationTicks = AtomicLong(0L)
    private val lastSimulationTick = AtomicLong(-1L)
    @Volatile private var lastActiveBlocks = 0
    @Volatile private var started = false
    @Volatile private var paused = false

    fun bootstrap() {
        started = false
        paused = false
        subsystems.clear()
        subsystems.add(LiquidSubsystem())
        subsystems.add(AtmosphereSubsystem())
        subsystems.add(ThermalSubsystem())
        subsystems.add(OxygenSubsystem())
        subsystems.add(PowerSubsystem())
        subsystems.add(StressSubsystem())
        subsystems.add(ResearchConstructionSubsystem())
    }

    fun config(): OniSimulationConfig = config
    fun powerState(): OniPowerState = powerState
    fun stressState(): OniStressState = stressState
    fun researchState(): OniResearchState = researchState
    fun constructionState(): OniConstructionState = constructionState

    fun onServerStarted() {
        serverTicks.set(0L)
        simulationTicks.set(0L)
        lastSimulationTick.set(-1L)
        started = true
        paused = false
    }

    fun onServerStopped() {
        started = false
        paused = false
        lastActiveBlocks = 0
        powerState.setGenerationW(0.0)
        powerState.setDemandW(0.0)
        powerState.setStoredEnergyJ(0.0)
        powerState.setTripped(false)
        stressState.setScore(0.0)
        researchState.clear()
        constructionState.clear()
    }

    fun onServerTick(server: net.minecraft.server.MinecraftServer) {
        if (!started) {
            onServerStarted()
        }

        val tick = serverTicks.incrementAndGet()
        if (paused) {
            return
        }

        if ((tick % config.tickInterval()) == 0L) {
            val level = server.overworld() ?: return
            runOneSimulationStep(tick, level)
        }
    }

    fun setRunning(running: Boolean) {
        if (!started && running) {
            onServerStarted()
        }
        paused = !running
    }

    fun runOneSimulationStep(serverTick: Long, level: net.minecraft.server.level.ServerLevel) {
        val context = SimulationContext(serverTick, config, level, this)
        for (subsystem in subsystems) {
            subsystem.run(context)
        }
        lastActiveBlocks = mconi.common.world.OniChunkDataAccess.blockCount(level)
        lastSimulationTick.set(serverTick)
        simulationTicks.incrementAndGet()
    }

    fun pipelineOrder(): List<String> {
        val order: MutableList<String> = ArrayList(subsystems.size)
        for (subsystem in subsystems) {
            order.add(subsystem.id())
        }
        return order.toList()
    }

    fun snapshot(): OniSimulationSnapshot {
        return OniSimulationSnapshot(
            started && !paused,
            serverTicks.get(),
            simulationTicks.get(),
            lastSimulationTick.get(),
            config.tickInterval(),
            config.cellSize(),
            lastActiveBlocks,
            powerState.generationW(),
            powerState.demandW(),
            powerState.storedEnergyJ(),
            powerState.tripped(),
            stressState.score(),
            researchState.unlockedCount(),
            constructionState.activeCount(),
        )
    }
}
