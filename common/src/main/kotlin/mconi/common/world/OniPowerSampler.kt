package mconi.common.world

import mconi.common.sim.OniServices
import mconi.common.sim.OniSimulationConfig
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.Blocks
import java.util.concurrent.atomic.AtomicLong

object OniPowerSampler {
    private val sampleTick = AtomicLong(0L)

    @JvmStatic
    fun sampleAroundPlayers(server: MinecraftServer) {
        val config = OniServices.simulationRuntime().config()
        val tick = sampleTick.incrementAndGet()
        if (tick % config.worldSampleIntervalTicks() != 0L) {
            return
        }
        val level: ServerLevel = server.overworld() ?: return

        var generatorCount = 0
        var consumerCount = 0
        var batteryCount = 0
        var wireCount = 0

        for (player: ServerPlayer in level.players()) {
            val pos = player.blockPosition()
            val radius = config.worldSampleRadiusBlocks()
            var x = pos.x - radius
            while (x <= pos.x + radius) {
                var y = pos.y - radius
                while (y <= pos.y + radius) {
                    if (y < level.minY || y > level.maxY) {
                        y++
                        continue
                    }
                    var z = pos.z - radius
                    while (z <= pos.z + radius) {
                        val state = level.getBlockState(BlockPos(x, y, z))
                        if (state.`is`(Blocks.REDSTONE_WIRE)) {
                            wireCount++
                        } else if (state.`is`(Blocks.REDSTONE_TORCH) || state.`is`(Blocks.REDSTONE_BLOCK)) {
                            generatorCount++
                        } else if (state.`is`(Blocks.IRON_BLOCK)) {
                            consumerCount++
                        } else if (state.`is`(Blocks.GOLD_BLOCK)) {
                            batteryCount++
                        }
                        z++
                    }
                    y++
                }
                x++
            }
        }

        val power = OniServices.simulationRuntime().powerState()
        power.setGeneratorCount(generatorCount)
        power.setConsumerCount(consumerCount)
        power.setBatteryCount(batteryCount)
        power.setWireCount(wireCount)
    }
}
