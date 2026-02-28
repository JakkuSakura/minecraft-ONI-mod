package mconi.common.sim.power

import mconi.common.TestMinecraftBootstrap
import mconi.common.sim.OniSimulationConfig
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OniPowerNetworkBuilderTest {
    private lateinit var wireBlock: Block
    private lateinit var generatorBlock: Block
    private lateinit var consumerBlock: Block
    private lateinit var batteryBlock: Block
    private lateinit var transformerBlock: Block

    @BeforeAll
    fun bootstrapMinecraft() {
        TestMinecraftBootstrap.ensureBootstrapped()
        wireBlock = Blocks.STONE
        generatorBlock = Blocks.GOLD_BLOCK
        consumerBlock = Blocks.DIAMOND_BLOCK
        batteryBlock = Blocks.EMERALD_BLOCK
        transformerBlock = Blocks.OBSIDIAN
    }

    @Test
    fun `overload trips circuit and consumers are unpowered`() {
        val states = mapOf(
            pos(0, 0, 0) to wireBlock.defaultBlockState(),
            pos(1, 0, 0) to generatorBlock.defaultBlockState(),
            pos(-1, 0, 0) to consumerBlock.defaultBlockState(),
        )
        val catalog = TestCatalog(
            wires = mapOf(wireBlock to 500.0),
            generators = mapOf(generatorBlock to 600.0),
            consumers = mapOf(consumerBlock to 700.0),
        )
        val result = buildResult(states, catalog)

        assertTrue(result.tripped)
        assertTrue(result.consumerPoweredByPos.isEmpty())
    }

    @Test
    fun `battery buffers deficit and keeps consumers powered`() {
        val batteryPos = pos(0, 1, 0)
        val states = mapOf(
            pos(0, 0, 0) to wireBlock.defaultBlockState(),
            pos(1, 0, 0) to generatorBlock.defaultBlockState(),
            pos(-1, 0, 0) to consumerBlock.defaultBlockState(),
            batteryPos to batteryBlock.defaultBlockState(),
        )
        val catalog = TestCatalog(
            wires = mapOf(wireBlock to 1000.0),
            generators = mapOf(generatorBlock to 100.0),
            consumers = mapOf(consumerBlock to 200.0),
            batteries = mapOf(batteryBlock to 1000.0),
        )
        val existingEnergy = mapOf(batteryPos to 500.0)
        val result = buildResult(states, catalog, existingEnergy)

        assertFalse(result.tripped)
        assertTrue(result.consumerPoweredByPos.contains(pos(-1, 0, 0).asLong()))
        assertEquals(400.0, result.batteryEnergyByPos[batteryPos.asLong()] ?: 0.0, 1e-6)
    }

    @Test
    fun `transformer throughput limits power transfer`() {
        val transformerPos = pos(2, 0, 0)
        val states = mapOf(
            pos(0, 0, 0) to generatorBlock.defaultBlockState(),
            pos(1, 0, 0) to wireBlock.defaultBlockState(),
            pos(3, 0, 0) to wireBlock.defaultBlockState(),
            pos(4, 0, 0) to consumerBlock.defaultBlockState(),
            transformerPos to transformerBlock.defaultBlockState(),
        )
        val lowThroughputCatalog = TestCatalog(
            wires = mapOf(wireBlock to 1000.0),
            generators = mapOf(generatorBlock to 1000.0),
            consumers = mapOf(consumerBlock to 500.0),
            transformers = mapOf(transformerBlock to 200.0),
        )
        val lowResult = buildResult(states, lowThroughputCatalog)
        assertEquals(1, lowResult.networks.size)
        assertTrue(lowResult.tripped)

        val highThroughputCatalog = TestCatalog(
            wires = mapOf(wireBlock to 1000.0),
            generators = mapOf(generatorBlock to 1000.0),
            consumers = mapOf(consumerBlock to 500.0),
            transformers = mapOf(transformerBlock to 600.0),
        )
        val highResult = buildResult(states, highThroughputCatalog)
        assertEquals(1, highResult.networks.size)
        assertFalse(highResult.tripped)
        assertTrue(highResult.consumerPoweredByPos.contains(pos(4, 0, 0).asLong()))
    }

    @Test
    fun `separate wire components form multiple networks`() {
        val states = mapOf(
            pos(0, 0, 0) to wireBlock.defaultBlockState(),
            pos(1, 0, 0) to generatorBlock.defaultBlockState(),
            pos(10, 0, 0) to wireBlock.defaultBlockState(),
            pos(11, 0, 0) to consumerBlock.defaultBlockState(),
        )
        val catalog = TestCatalog(
            wires = mapOf(wireBlock to 1000.0),
            generators = mapOf(generatorBlock to 400.0),
            consumers = mapOf(consumerBlock to 200.0),
        )
        val result = buildResult(states, catalog)
        assertEquals(2, result.networks.size)
    }

    private fun buildResult(
        states: Map<BlockPos, BlockState>,
        catalog: PowerCatalog,
        existingBatteryEnergy: Map<BlockPos, Double> = emptyMap()
    ): OniPowerNetworkBuilder.BuildResult {
        val config = OniSimulationConfig()
        config.setWorldSampleRadiusBlocks(16)
        val view = TestWorldView(states)
        val energyByPos = existingBatteryEnergy.mapKeys { it.key.asLong() }
        val builder = OniPowerNetworkBuilder(view, config, catalog, energyByPos)
        return builder.build()
    }

    private fun pos(x: Int, y: Int, z: Int): BlockPos = BlockPos(x, y, z)

    private class TestWorldView(private val states: Map<BlockPos, BlockState>) : PowerWorldView {
        override val minY: Int = -16
        override val maxY: Int = 16

        override fun players(): Iterable<BlockPos> {
            return listOf(BlockPos(0, 0, 0))
        }

        override fun getBlockState(pos: BlockPos): BlockState {
            return states[pos] ?: Blocks.AIR.defaultBlockState()
        }
    }

    private class TestCatalog(
        private val wires: Map<Block, Double> = emptyMap(),
        private val generators: Map<Block, Double> = emptyMap(),
        private val consumers: Map<Block, Double> = emptyMap(),
        private val batteries: Map<Block, Double> = emptyMap(),
        private val transformers: Map<Block, Double> = emptyMap(),
    ) : PowerCatalog {
        override fun wireCapacity(state: BlockState): Double? = wires[state.block]
        override fun generatorOutput(state: BlockState): Double? = generators[state.block]
        override fun consumerDemand(state: BlockState): Double? = consumers[state.block]
        override fun batteryCapacity(state: BlockState): Double? = batteries[state.block]
        override fun transformerThroughput(state: BlockState): Double? = transformers[state.block]
    }
}
