package mconi.common.sim

import mconi.common.element.OniElements
import mconi.common.sim.model.OccupancyState
import mconi.common.sim.model.OniBlockData
import mconi.common.sim.subsystem.LiquidSubsystem
import mconi.common.world.BlockEntryView
import net.minecraft.core.BlockPos
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class LiquidSubsystemTest {
    @Test
    fun liquidFallsDownwardIntoEmptyCell() {
        val config = OniSimulationConfig()
        config.setLiquidTransferKgPerStep(100.0)

        val above = BlockPos(0, 1, 0)
        val below = BlockPos(0, 0, 0)
        val aboveCell = OniBlockData()
        aboveCell.setLiquidState(OniElements.LIQUID_WATER, 30.0)
        aboveCell.setOccupancyState(OccupancyState.VACUUM)
        val belowCell = OniBlockData()
        belowCell.setOccupancyState(OccupancyState.VACUUM)

        val entries = listOf(
            BlockEntryView(above, aboveCell),
            BlockEntryView(below, belowCell),
        )
        LiquidSubsystem().runEntries(entries, config)

        assertEquals(0.0, aboveCell.liquidMassKg(), 1e-6)
        assertEquals(OniElements.LIQUID_NONE, aboveCell.liquidId())
        assertEquals(30.0, belowCell.liquidMassKg(), 1e-6)
        assertEquals(OniElements.LIQUID_WATER, belowCell.liquidId())
    }

    @Test
    fun liquidSpreadsLaterallyTowardLowerMass() {
        val config = OniSimulationConfig()
        config.setLiquidTransferKgPerStep(1000.0)

        val left = BlockPos(0, 0, 0)
        val right = BlockPos(1, 0, 0)
        val leftCell = OniBlockData()
        leftCell.setLiquidState(OniElements.LIQUID_WATER, 100.0)
        leftCell.setOccupancyState(OccupancyState.VACUUM)
        val rightCell = OniBlockData()
        rightCell.setOccupancyState(OccupancyState.VACUUM)

        val entries = listOf(
            BlockEntryView(left, leftCell),
            BlockEntryView(right, rightCell),
        )
        LiquidSubsystem().runEntries(entries, config)

        assertEquals(75.0, leftCell.liquidMassKg(), 1e-6)
        assertEquals(25.0, rightCell.liquidMassKg(), 1e-6)
    }

    @Test
    fun voidCellsDrainLiquid() {
        val config = OniSimulationConfig()
        config.setVoidLiquidDrainFraction(0.5)

        val pos = BlockPos(0, 0, 0)
        val cell = OniBlockData()
        cell.setLiquidState(OniElements.LIQUID_WATER, 10.0)
        cell.setOccupancyState(OccupancyState.VOID)

        val entries = listOf(BlockEntryView(pos, cell))
        LiquidSubsystem().runEntries(entries, config)

        assertEquals(5.0, cell.liquidMassKg(), 1e-6)
        assertEquals(OniElements.LIQUID_WATER, cell.liquidId())
    }
}
