package conservecraft.common.sim.subsystem

import conservecraft.common.element.OniElements
import net.minecraft.core.BlockPos
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FluidFlowKernelTest {
    @Test
    fun gasDiffusionConservesMassAndMixesTemperature() {
        val posA = BlockPos(0, 0, 0)
        val posB = BlockPos(1, 0, 0)
        val cells = mapOf(
            posA to FluidFlowKernel.CellState(
                phase = FluidFlowKernel.Phase.GAS,
                elementId = OniElements.GAS_OXYGEN.id,
                mass = 200.0,
                temperatureK = 300.0
            ),
            posB to FluidFlowKernel.CellState(
                phase = FluidFlowKernel.Phase.GAS,
                elementId = OniElements.GAS_OXYGEN.id,
                mass = 100.0,
                temperatureK = 200.0
            )
        )

        val updated = FluidFlowKernel.applyGasFlow(
            cells,
            FluidFlowKernel.FlowConfig(
                maxTransferPerStep = 1000.0,
                referenceMass = 100.0,
                downwardBias = 0.0
            )
        )

        val nextA = updated[posA]
        val nextB = updated[posB]
        assertNotNull(nextA)
        assertNotNull(nextB)
        assertEquals(188.0, nextA.mass, 1e-6)
        assertEquals(112.0, nextB.mass, 1e-6)
        val expectedTempB = (100.0 * 200.0 + 12.0 * 300.0) / 112.0
        assertEquals(expectedTempB, nextB.temperatureK, 1e-6)
        assertEquals(300.0, nextA.temperatureK, 1e-6)
        assertEquals(300.0, nextA.mass + nextB.mass, 1e-6)
    }

    @Test
    fun heavierGasSwapsWithLighterGas() {
        val posA = BlockPos(0, 0, 0)
        val posB = BlockPos(1, 0, 0)
        val cells = mapOf(
            posA to FluidFlowKernel.CellState(
                phase = FluidFlowKernel.Phase.GAS,
                elementId = OniElements.GAS_CARBON_DIOXIDE.id,
                mass = 200.0,
                temperatureK = 310.0
            ),
            posB to FluidFlowKernel.CellState(
                phase = FluidFlowKernel.Phase.GAS,
                elementId = OniElements.GAS_HYDROGEN.id,
                mass = 50.0,
                temperatureK = 250.0
            )
        )

        val updated = FluidFlowKernel.applyGasFlow(
            cells,
            FluidFlowKernel.FlowConfig(
                maxTransferPerStep = 1000.0,
                referenceMass = 100.0,
                downwardBias = 0.0
            )
        )

        val nextA = updated[posA]
        val nextB = updated[posB]
        assertNotNull(nextA)
        assertNotNull(nextB)
        assertEquals(OniElements.GAS_HYDROGEN.id, nextA.elementId)
        assertEquals(OniElements.GAS_CARBON_DIOXIDE.id, nextB.elementId)
        assertEquals(50.0, nextA.mass, 1e-6)
        assertEquals(200.0, nextB.mass, 1e-6)
        assertEquals(250.0, nextA.temperatureK, 1e-6)
        assertEquals(310.0, nextB.temperatureK, 1e-6)
    }

    @Test
    fun liquidDisplacesGasBySwap() {
        val posA = BlockPos(0, 0, 0)
        val posB = BlockPos(1, 0, 0)
        val cells = mapOf(
            posA to FluidFlowKernel.CellState(
                phase = FluidFlowKernel.Phase.LIQUID,
                elementId = OniElements.LIQUID_WATER,
                mass = 1000.0,
                temperatureK = 280.0
            ),
            posB to FluidFlowKernel.CellState(
                phase = FluidFlowKernel.Phase.GAS,
                elementId = OniElements.GAS_OXYGEN.id,
                mass = 10.0,
                temperatureK = 300.0
            )
        )

        val updated = FluidFlowKernel.applyLiquidFlow(
            cells,
            FluidFlowKernel.FlowConfig(
                maxTransferPerStep = 1000.0,
                referenceMass = 1000.0,
                downwardBias = 0.25
            )
        )

        val nextA = updated[posA]
        val nextB = updated[posB]
        assertNotNull(nextA)
        assertNotNull(nextB)
        assertEquals(FluidFlowKernel.Phase.GAS, nextA.phase)
        assertEquals(FluidFlowKernel.Phase.LIQUID, nextB.phase)
        assertEquals(OniElements.GAS_OXYGEN.id, nextA.elementId)
        assertEquals(OniElements.LIQUID_WATER, nextB.elementId)
        assertEquals(10.0, nextA.mass, 1e-6)
        assertEquals(1000.0, nextB.mass, 1e-6)
    }

    @Test
    fun liquidFlowsDownwardWithBias() {
        val posA = BlockPos(0, 1, 0)
        val posB = BlockPos(0, 0, 0)
        val cells = mapOf(
            posA to FluidFlowKernel.CellState(
                phase = FluidFlowKernel.Phase.LIQUID,
                elementId = OniElements.LIQUID_WATER,
                mass = 1000.0,
                temperatureK = 300.0
            ),
            posB to FluidFlowKernel.CellState(
                phase = FluidFlowKernel.Phase.EMPTY,
                elementId = null,
                mass = 0.0,
                temperatureK = 293.15
            )
        )

        val updated = FluidFlowKernel.applyLiquidFlow(
            cells,
            FluidFlowKernel.FlowConfig(
                maxTransferPerStep = 500.0,
                referenceMass = 1000.0,
                downwardBias = 0.25
            )
        )

        val nextA = updated[posA]
        val nextB = updated[posB]
        assertNotNull(nextA)
        assertNotNull(nextB)
        assertEquals(500.0, nextA.mass, 1e-6)
        assertEquals(500.0, nextB.mass, 1e-6)
        assertEquals(300.0, nextA.temperatureK, 1e-6)
        assertEquals(300.0, nextB.temperatureK, 1e-6)
        assertTrue(nextB.phase == FluidFlowKernel.Phase.LIQUID)
    }
}
