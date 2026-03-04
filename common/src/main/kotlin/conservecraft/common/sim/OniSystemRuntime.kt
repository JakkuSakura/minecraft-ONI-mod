package conservecraft.common.sim

import conservecraft.common.sim.subsystem.GasSystem
import conservecraft.common.sim.subsystem.LiquidSystem
import conservecraft.common.sim.subsystem.OniSystem
import conservecraft.common.sim.subsystem.PlumbingSystem
import conservecraft.common.sim.subsystem.PowerSystem
import conservecraft.common.sim.subsystem.RefiningSystem
import conservecraft.common.sim.subsystem.ResearchConstructionSystem
import conservecraft.common.sim.subsystem.StressSystem
import conservecraft.common.sim.subsystem.SystemContext
import conservecraft.common.sim.subsystem.ThermalSystem
import conservecraft.common.sim.subsystem.VentilationSystem
import conservecraft.common.world.OniWorldScan
import java.util.concurrent.atomic.AtomicLong

/**
 * Server-authoritative runtime loop controller for ONI world systems.
 */
class OniSystemRuntime {
    private val config = OniSystemConfig()
    private val systems: MutableList<OniSystem> = ArrayList()
    private val powerState = OniPowerState()
    private val stressState = OniStressState()
    private val researchState = OniResearchState()
    private val constructionState = OniConstructionState()
    private val serverTicks = AtomicLong(0L)
    private val systemTicks = AtomicLong(0L)
    private val lastSystemTick = AtomicLong(-1L)
    @Volatile private var lastActiveBlocks = 0
    @Volatile private var started = false
    @Volatile private var paused = false

    fun bootstrap() {
        started = false
        paused = false
        systems.clear()
        systems.add(LiquidSystem())
        systems.add(PlumbingSystem())
        systems.add(ThermalSystem())
        systems.add(GasSystem())
        systems.add(VentilationSystem())
        systems.add(PowerSystem())
        systems.add(RefiningSystem())
        systems.add(StressSystem())
        systems.add(ResearchConstructionSystem())
    }

    fun config(): OniSystemConfig = config
    fun powerState(): OniPowerState = powerState
    fun stressState(): OniStressState = stressState
    fun researchState(): OniResearchState = researchState
    fun constructionState(): OniConstructionState = constructionState

    fun onServerStarted() {
        serverTicks.set(0L)
        systemTicks.set(0L)
        lastSystemTick.set(-1L)
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
        powerState.setBatteryEnergyByPos(emptyMap())
        powerState.setConsumerPoweredByPos(emptySet())
        powerState.setNetworks(emptyList())
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
            runOneSystemStep(tick, level)
        }
    }

    fun setRunning(running: Boolean) {
        if (!started && running) {
            onServerStarted()
        }
        paused = !running
    }

    fun runOneSystemStep(serverTick: Long, level: net.minecraft.server.level.ServerLevel) {
        val context = SystemContext(serverTick, config, level, this)
        for (system in systems) {
            system.run(context)
        }
        lastActiveBlocks = OniWorldScan.positionsAroundPlayers(
            level,
            config.worldSampleRadiusBlocks(),
            config.cellSize()
        ).size
        lastSystemTick.set(serverTick)
        systemTicks.incrementAndGet()
    }

    fun pipelineOrder(): List<String> {
        val order: MutableList<String> = ArrayList(systems.size)
        for (system in systems) {
            order.add(system.id())
        }
        return order.toList()
    }

    fun snapshot(): OniSystemSnapshot {
        return OniSystemSnapshot(
            started && !paused,
            serverTicks.get(),
            systemTicks.get(),
            lastSystemTick.get(),
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
