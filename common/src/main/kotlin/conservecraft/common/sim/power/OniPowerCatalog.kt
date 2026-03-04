package conservecraft.common.sim.power

import conservecraft.common.block.OniBlockFactory
import conservecraft.common.block.OniBlockLookup
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

class OniPowerCatalog : PowerCatalog {
    data class WireSpec(val block: Block, val capacityW: Double)
    data class GeneratorSpec(val block: Block, val outputW: Double)
    data class ConsumerSpec(val block: Block, val demandW: Double)
    data class BatterySpec(val block: Block, val capacityJ: Double)
    data class TransformerSpec(val block: Block, val throughputW: Double)

    val wires: List<WireSpec> = listOf(
        WireSpec(block(OniBlockFactory.POWER_WIRE), 1000.0),
        WireSpec(block(OniBlockFactory.WIRE), 1000.0),
        WireSpec(block(OniBlockFactory.WIRE_BRIDGE), 1000.0),
        WireSpec(block(OniBlockFactory.CONDUCTIVE_WIRE), 2000.0),
        WireSpec(block(OniBlockFactory.CONDUCTIVE_WIRE_BRIDGE), 2000.0),
        WireSpec(block(OniBlockFactory.HEAVI_WATT_WIRE), 20000.0),
        WireSpec(block(OniBlockFactory.HEAVI_WATT_JOINT_PLATE), 20000.0),
        WireSpec(block(OniBlockFactory.HEAVI_WATT_CONDUCTIVE_WIRE), 50000.0),
        WireSpec(block(OniBlockFactory.HEAVI_WATT_CONDUCTIVE_JOINT_PLATE), 50000.0),
        WireSpec(block(OniBlockFactory.POWER_SWITCH), 1000.0),
        WireSpec(block(OniBlockFactory.POWER_SHUTOFF), 1000.0),
    )

    val generators: List<GeneratorSpec> = listOf(
        GeneratorSpec(block(OniBlockFactory.MANUAL_GENERATOR), 400.0),
        GeneratorSpec(block(OniBlockFactory.COAL_GENERATOR), 600.0),
        GeneratorSpec(block(OniBlockFactory.HYDROGEN_GENERATOR), 800.0),
        GeneratorSpec(block(OniBlockFactory.NATURAL_GAS_GENERATOR), 800.0),
        GeneratorSpec(block(OniBlockFactory.PETROLEUM_GENERATOR), 2000.0),
    )

    val consumers: List<ConsumerSpec> = listOf(
        ConsumerSpec(block(OniBlockFactory.OXYGEN_DIFFUSER), 120.0),
        ConsumerSpec(block(OniBlockFactory.ALGAE_DEOXIDIZER), 120.0),
        ConsumerSpec(block(OniBlockFactory.CO2_SCRUBBER), 120.0),
        ConsumerSpec(block(OniBlockFactory.GAS_PUMP), 240.0),
        ConsumerSpec(block(OniBlockFactory.LIQUID_PUMP), 240.0),
        ConsumerSpec(block(OniBlockFactory.POWER_CONTROL_STATION), 240.0),
    )

    val batteries: List<BatterySpec> = listOf(
        BatterySpec(block(OniBlockFactory.BATTERY), 10_000.0),
        BatterySpec(block(OniBlockFactory.SMART_BATTERY), 20_000.0),
        BatterySpec(block(OniBlockFactory.JUMBO_BATTERY), 40_000.0),
    )

    val transformers: List<TransformerSpec> = listOf(
        TransformerSpec(block(OniBlockFactory.POWER_TRANSFORMER_SMALL), 1000.0),
        TransformerSpec(block(OniBlockFactory.POWER_TRANSFORMER), 4000.0),
    )

    override fun wireCapacity(state: BlockState): Double? {
        for (spec in wires) {
            if (state.`is`(spec.block)) {
                return spec.capacityW
            }
        }
        return null
    }

    override fun generatorOutput(state: BlockState): Double? {
        for (spec in generators) {
            if (state.`is`(spec.block)) {
                return spec.outputW
            }
        }
        return null
    }

    override fun consumerDemand(state: BlockState): Double? {
        for (spec in consumers) {
            if (state.`is`(spec.block)) {
                return spec.demandW
            }
        }
        return null
    }

    override fun batteryCapacity(state: BlockState): Double? {
        for (spec in batteries) {
            if (state.`is`(spec.block)) {
                return spec.capacityJ
            }
        }
        return null
    }

    override fun transformerThroughput(state: BlockState): Double? {
        for (spec in transformers) {
            if (state.`is`(spec.block)) {
                return spec.throughputW
            }
        }
        return null
    }

    private fun block(id: String): Block = OniBlockLookup.block(id)
}
