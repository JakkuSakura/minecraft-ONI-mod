package mconi.common.sim

import mconi.common.element.OniElements
import mconi.common.sim.model.OccupancyState
import mconi.common.sim.model.OniBlockData
import net.minecraft.core.BlockPos
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OniAtmosphereKernelTest {
    @Test
    fun diffusionMovesMassTowardEqualization() {
        val config = OniSimulationConfig()
        config.setGasTransferKgPerStep(10.0)

        val left = BlockPos(0, 0, 0)
        val right = BlockPos(1, 0, 0)
        val leftCell = OniBlockData()
        val rightCell = OniBlockData()
        leftCell.setGasMassKg(OniElements.GAS_OXYGEN, 10.0)
        rightCell.setGasMassKg(OniElements.GAS_OXYGEN, 0.1)

        val cells = linkedMapOf(left to leftCell, right to rightCell)
        OniAtmosphereKernel().runOnCells(cells, config)

        val leftAfter = leftCell.gasMassKg(OniElements.GAS_OXYGEN)
        val rightAfter = rightCell.gasMassKg(OniElements.GAS_OXYGEN)
        assertTrue(leftAfter < 10.0)
        assertTrue(rightAfter > 0.1)
        assertEquals(10.1, leftAfter + rightAfter, 1e-6)
    }

    @Test
    fun voidCellsDrainGas() {
        val config = OniSimulationConfig()
        config.setVoidGasDrainFraction(0.5)

        val pos = BlockPos(0, 0, 0)
        val cell = OniBlockData()
        cell.setOccupancyState(OccupancyState.VOID)
        cell.setGasMassKg(OniElements.GAS_OXYGEN, 10.0)

        val cells = mutableMapOf(pos to cell)
        OniAtmosphereKernel().runOnCells(cells, config)

        assertEquals(5.0, cell.gasMassKg(OniElements.GAS_OXYGEN), 1e-6)
        assertEquals(0.0, cell.pressureKpa(), 1e-6)
    }
}
