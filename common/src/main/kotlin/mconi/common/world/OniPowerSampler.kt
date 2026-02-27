package mconi.common.world

import mconi.common.sim.OniServices
import mconi.common.sim.OniSimulationConfig
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import mconi.common.block.OniBlockLookup
import mconi.common.block.OniBlockFactory
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
        val manualGenerator = OniBlockLookup.block(OniBlockFactory.MANUAL_GENERATOR)
        val battery = OniBlockLookup.block(OniBlockFactory.BATTERY)
        val powerWire = OniBlockLookup.block(OniBlockFactory.POWER_WIRE)
        val oxygenDiffuser = OniBlockLookup.block(OniBlockFactory.OXYGEN_DIFFUSER)
        val algaeDeoxidizer = OniBlockLookup.block(OniBlockFactory.ALGAE_DEOXIDIZER)
        val co2Scrubber = OniBlockLookup.block(OniBlockFactory.CO2_SCRUBBER)
        val gasPump = OniBlockLookup.block(OniBlockFactory.GAS_PUMP)
        val liquidPump = OniBlockLookup.block(OniBlockFactory.LIQUID_PUMP)
        val wire = OniBlockLookup.block(OniBlockFactory.WIRE)
        val wireBridge = OniBlockLookup.block(OniBlockFactory.WIRE_BRIDGE)
        val conductiveWire = OniBlockLookup.block(OniBlockFactory.CONDUCTIVE_WIRE)
        val conductiveWireBridge = OniBlockLookup.block(OniBlockFactory.CONDUCTIVE_WIRE_BRIDGE)
        val heaviWattWire = OniBlockLookup.block(OniBlockFactory.HEAVI_WATT_WIRE)
        val heaviWattJointPlate = OniBlockLookup.block(OniBlockFactory.HEAVI_WATT_JOINT_PLATE)
        val heaviWattConductiveWire = OniBlockLookup.block(OniBlockFactory.HEAVI_WATT_CONDUCTIVE_WIRE)
        val heaviWattConductiveJointPlate = OniBlockLookup.block(OniBlockFactory.HEAVI_WATT_CONDUCTIVE_JOINT_PLATE)
        val powerTransformer = OniBlockLookup.block(OniBlockFactory.POWER_TRANSFORMER)
        val powerTransformerSmall = OniBlockLookup.block(OniBlockFactory.POWER_TRANSFORMER_SMALL)
        val smartBattery = OniBlockLookup.block(OniBlockFactory.SMART_BATTERY)
        val jumboBattery = OniBlockLookup.block(OniBlockFactory.JUMBO_BATTERY)
        val coalGenerator = OniBlockLookup.block(OniBlockFactory.COAL_GENERATOR)
        val hydrogenGenerator = OniBlockLookup.block(OniBlockFactory.HYDROGEN_GENERATOR)
        val naturalGasGenerator = OniBlockLookup.block(OniBlockFactory.NATURAL_GAS_GENERATOR)
        val petroleumGenerator = OniBlockLookup.block(OniBlockFactory.PETROLEUM_GENERATOR)
        val powerControlStation = OniBlockLookup.block(OniBlockFactory.POWER_CONTROL_STATION)
        val powerSwitch = OniBlockLookup.block(OniBlockFactory.POWER_SWITCH)
        val powerShutoff = OniBlockLookup.block(OniBlockFactory.POWER_SHUTOFF)

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
                        if (state.`is`(powerWire) || state.`is`(wire) || state.`is`(wireBridge) ||
                            state.`is`(conductiveWire) || state.`is`(conductiveWireBridge) ||
                            state.`is`(heaviWattWire) || state.`is`(heaviWattJointPlate) ||
                            state.`is`(heaviWattConductiveWire) || state.`is`(heaviWattConductiveJointPlate) ||
                            state.`is`(powerSwitch) || state.`is`(powerShutoff)
                        ) {
                            wireCount++
                        } else if (
                            state.`is`(manualGenerator) ||
                            state.`is`(coalGenerator) ||
                            state.`is`(hydrogenGenerator) ||
                            state.`is`(naturalGasGenerator) ||
                            state.`is`(petroleumGenerator)
                        ) {
                            generatorCount++
                        } else if (state.`is`(battery) || state.`is`(smartBattery) || state.`is`(jumboBattery)) {
                            batteryCount++
                        } else if (
                            state.`is`(oxygenDiffuser) ||
                            state.`is`(algaeDeoxidizer) ||
                            state.`is`(co2Scrubber) ||
                            state.`is`(gasPump) ||
                            state.`is`(liquidPump) ||
                            state.`is`(powerTransformer) ||
                            state.`is`(powerTransformerSmall) ||
                            state.`is`(powerControlStation)
                        ) {
                            consumerCount++
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
