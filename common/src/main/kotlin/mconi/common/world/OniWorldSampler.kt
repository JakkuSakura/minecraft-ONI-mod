package mconi.common.world

import mconi.common.sim.OniServices
import mconi.common.sim.OniSimulationConfig
import mconi.common.sim.OniSimulationGrid
import mconi.common.sim.OniWorldFoundation
import mconi.common.sim.model.FluidSpecies
import mconi.common.sim.model.GasSpecies
import mconi.common.sim.model.OccupancyState
import mconi.common.sim.model.OniCellState
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.Blocks
import mconi.common.block.OniBlockLookup
import mconi.common.content.OniBlockIds
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluids
import java.util.concurrent.atomic.AtomicLong

object OniWorldSampler {
    private val sampleTick = AtomicLong(0L)

    @JvmStatic
    fun sampleAroundPlayers(server: MinecraftServer) {
        val config = OniServices.simulationRuntime().config()
        val tick = sampleTick.incrementAndGet()
        if (tick % config.worldSampleIntervalTicks() != 0L) {
            return
        }

        val level = server.overworld() ?: return
        for (player in level.players()) {
            val pos = player.blockPosition()
            sampleBox(level, pos.x, pos.y, pos.z, config.worldSampleRadiusBlocks())
        }
    }

    @JvmStatic
    fun sampleBox(level: ServerLevel, centerX: Int, centerY: Int, centerZ: Int, radiusBlocks: Int): Int {
        val config = OniServices.simulationRuntime().config()
        val grid = OniServices.simulationRuntime().grid()
        val cellSize = config.cellSize()
        val minY = level.minY
        val maxY = level.maxY - 1

        var samples = 0
        var x = centerX - radiusBlocks
        while (x <= centerX + radiusBlocks) {
            var y = centerY - radiusBlocks
            while (y <= centerY + radiusBlocks) {
                if (y < minY || y > maxY) {
                    y += cellSize
                    continue
                }
                var z = centerZ - radiusBlocks
                while (z <= centerZ + radiusBlocks) {
                    val cell = grid.getOrCreateCellAtBlock(x, y, z, cellSize)
                    hydrateCellFromWorld(level, x, y, z, minY, maxY, config, grid, cell)
                    samples++
                    z += cellSize
                }
                y += cellSize
            }
            x += cellSize
        }
        return samples
    }

    private fun hydrateCellFromWorld(
        level: ServerLevel,
        x: Int,
        y: Int,
        z: Int,
        minY: Int,
        maxY: Int,
        config: OniSimulationConfig,
        grid: OniSimulationGrid,
        cell: OniCellState
    ) {
        if (!OniWorldFoundation.isWithinHorizontalBounds(x, z, config)) {
            cell.setOccupancyState(OccupancyState.VOID)
            clearCellMass(cell)
            return
        }

        if (OniWorldFoundation.isVoidBand(y, maxY, config)) {
            cell.setOccupancyState(OccupancyState.VOID)
            clearCellMass(cell)
            return
        }

        val state = level.getBlockState(BlockPos(x, y, z))
        if (!state.fluidState.isEmpty) {
            if (state.fluidState.`is`(Fluids.LAVA)) {
                cell.setFluidState(FluidSpecies.LAVA, 4000.0)
                cell.setTemperatureK(1300.0)
            } else {
                cell.setFluidState(FluidSpecies.WATER, 4000.0)
                cell.setTemperatureK(293.15)
            }
            cell.setOccupancyState(OccupancyState.FLUID)
            return
        }

        if (!state.isAir) {
            cell.setOccupancyState(OccupancyState.SOLID)
            clearCellMass(cell)
            injectProducerEffects(level, x, y, z, minY, maxY, config, grid)
            return
        }

        cell.setOccupancyState(OccupancyState.GAS)
        cell.setGasMassKg(GasSpecies.O2, config.baseO2MassKg())
        cell.setGasMassKg(GasSpecies.CO2, config.baseCO2MassKg())
        cell.setGasMassKg(GasSpecies.H2, 0.0)
        cell.setTemperatureK(293.15)

        if (OniWorldFoundation.isLavaBand(y, minY, config)) {
            cell.setFluidState(FluidSpecies.LAVA, 4000.0)
            cell.setOccupancyState(OccupancyState.FLUID)
            cell.setTemperatureK(1300.0)
        }

        injectProducerEffects(level, x, y, z, minY, maxY, config, grid)
    }

    private fun injectProducerEffects(
        level: ServerLevel,
        x: Int,
        y: Int,
        z: Int,
        minY: Int,
        maxY: Int,
        config: OniSimulationConfig,
        grid: OniSimulationGrid
    ) {
        val state: BlockState = level.getBlockState(BlockPos(x, y, z))
        val algaeBlock = OniBlockLookup.block(OniBlockIds.ALGAE)
        if (state.`is`(Blocks.MOSS_BLOCK) || state.`is`(Blocks.MOSS_CARPET) || state.`is`(Blocks.SEAGRASS) || state.`is`(algaeBlock)) {
            injectGasAt(grid, x, y + 1, z, minY, maxY, config.baseO2MassKg(), 0.0)
        }

        if (state.`is`(Blocks.CAMPFIRE) || state.`is`(Blocks.FURNACE) || state.`is`(Blocks.BLAST_FURNACE)) {
            injectGasAt(grid, x, y + 1, z, minY, maxY, 0.0, config.baseCO2MassKg())
        }
    }

    private fun injectGasAt(
        grid: OniSimulationGrid,
        x: Int,
        y: Int,
        z: Int,
        minY: Int,
        maxY: Int,
        o2Mass: Double,
        co2Mass: Double
    ) {
        if (y < minY || y > maxY) {
            return
        }
        val cell = grid.getOrCreateCellAtBlock(x, y, z, OniServices.simulationRuntime().config().cellSize())
        cell.setOccupancyState(OccupancyState.GAS)
        cell.setGasMassKg(GasSpecies.O2, cell.gasMassKg(GasSpecies.O2) + o2Mass)
        cell.setGasMassKg(GasSpecies.CO2, cell.gasMassKg(GasSpecies.CO2) + co2Mass)
    }

    private fun clearCellMass(cell: OniCellState) {
        cell.setFluidState(FluidSpecies.NONE, 0.0)
        cell.setGasMassKg(GasSpecies.O2, 0.0)
        cell.setGasMassKg(GasSpecies.CO2, 0.0)
        cell.setGasMassKg(GasSpecies.H2, 0.0)
    }
}
