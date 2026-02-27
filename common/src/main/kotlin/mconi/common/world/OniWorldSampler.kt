package mconi.common.world

import mconi.common.sim.OniServices
import mconi.common.sim.OniSimulationConfig
import mconi.common.sim.OniSimulationGrid
import mconi.common.sim.OniWorldFoundation
import mconi.common.sim.model.FluidSpecies
import mconi.common.sim.model.GasSpecies
import mconi.common.sim.model.OccupancyState
import mconi.common.sim.model.OniCellState
import mconi.common.content.OniMaterialMass
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
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
            cell.setWorldBlockKey(VOID_BLOCK_KEY)
            return
        }

        if (OniWorldFoundation.isVoidBand(y, maxY, config)) {
            cell.setOccupancyState(OccupancyState.VOID)
            clearCellMass(cell)
            cell.setWorldBlockKey(VOID_BLOCK_KEY)
            return
        }

        val state = level.getBlockState(BlockPos(x, y, z))
        val blockKey = BuiltInRegistries.BLOCK.getKey(state.block).toString()
        val sameBlock = blockKey == cell.worldBlockKey()
        if (!state.fluidState.isEmpty) {
            val species = if (state.fluidState.`is`(Fluids.LAVA)) {
                FluidSpecies.LAVA
            } else {
                FluidSpecies.WATER
            }
            if (!sameBlock || cell.occupancyState() != OccupancyState.FLUID || cell.fluidSpecies() != species) {
                clearCellMass(cell)
                cell.setFluidState(species, OniMaterialMass.fluidDefaultMassKg(species).toDouble())
                cell.setOccupancyState(OccupancyState.FLUID)
                cell.setTemperatureK(if (species == FluidSpecies.LAVA) 1300.0 else 293.15)
                cell.setWorldBlockKey(blockKey)
            }
            return
        }

        val waterBlock = OniBlockLookup.block(OniBlockIds.WATER)
        val pollutedWaterBlock = OniBlockLookup.block(OniBlockIds.POLLUTED_WATER)
        val crudeOilBlock = OniBlockLookup.block(OniBlockIds.CRUDE_OIL)
        val lavaBlock = OniBlockLookup.block(OniBlockIds.LAVA)
        if (state.`is`(waterBlock) || state.`is`(pollutedWaterBlock) || state.`is`(crudeOilBlock) || state.`is`(lavaBlock)) {
            val species = when {
                state.`is`(lavaBlock) -> FluidSpecies.LAVA
                state.`is`(pollutedWaterBlock) -> FluidSpecies.POLLUTED_WATER
                state.`is`(crudeOilBlock) -> FluidSpecies.CRUDE_OIL
                else -> FluidSpecies.WATER
            }
            if (!sameBlock || cell.occupancyState() != OccupancyState.FLUID || cell.fluidSpecies() != species) {
                cell.setFluidState(species, OniMaterialMass.fluidDefaultMassKg(species).toDouble())
                cell.setTemperatureK(if (species == FluidSpecies.LAVA) 1300.0 else 293.15)
                cell.setOccupancyState(OccupancyState.FLUID)
                cell.setWorldBlockKey(blockKey)
            }
            return
        }

        val oxygenBlock = OniBlockLookup.block(OniBlockIds.OXYGEN_GAS)
        val co2Block = OniBlockLookup.block(OniBlockIds.CARBON_DIOXIDE_GAS)
        val hydrogenBlock = OniBlockLookup.block(OniBlockIds.HYDROGEN_GAS)
        if (state.`is`(oxygenBlock)) {
            if (!sameBlock || cell.occupancyState() != OccupancyState.GAS) {
                clearCellMass(cell)
                cell.setGasMassKg(GasSpecies.O2, config.baseO2MassKg())
                cell.setOccupancyState(OccupancyState.GAS)
                cell.setTemperatureK(293.15)
                cell.setWorldBlockKey(blockKey)
            }
            return
        }
        if (state.`is`(co2Block)) {
            if (!sameBlock || cell.occupancyState() != OccupancyState.GAS) {
                clearCellMass(cell)
                cell.setGasMassKg(GasSpecies.CO2, config.baseCO2MassKg())
                cell.setOccupancyState(OccupancyState.GAS)
                cell.setTemperatureK(293.15)
                cell.setWorldBlockKey(blockKey)
            }
            return
        }
        if (state.`is`(hydrogenBlock)) {
            if (!sameBlock || cell.occupancyState() != OccupancyState.GAS) {
                clearCellMass(cell)
                cell.setGasMassKg(GasSpecies.H2, config.baseH2MassKg())
                cell.setOccupancyState(OccupancyState.GAS)
                cell.setTemperatureK(293.15)
                cell.setWorldBlockKey(blockKey)
            }
            return
        }

        if (!state.isAir) {
            if (!sameBlock || cell.occupancyState() != OccupancyState.SOLID) {
                cell.setOccupancyState(OccupancyState.SOLID)
                clearCellMass(cell)
                cell.setWorldBlockKey(blockKey)
            }
            injectProducerEffects(level, x, y, z, minY, maxY, config, grid)
            return
        }

        if (!sameBlock || cell.occupancyState() == OccupancyState.SOLID) {
            cell.setOccupancyState(OccupancyState.GAS)
            cell.setGasMassKg(GasSpecies.O2, config.baseO2MassKg())
            cell.setGasMassKg(GasSpecies.CO2, config.baseCO2MassKg())
            cell.setGasMassKg(GasSpecies.H2, 0.0)
            cell.setTemperatureK(293.15)
            cell.setWorldBlockKey(blockKey)
        }

        if (OniWorldFoundation.isLavaBand(y, minY, config)) {
            val mass = OniMaterialMass.fluidDefaultMassKg(FluidSpecies.LAVA).toDouble()
            cell.setFluidState(FluidSpecies.LAVA, mass)
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

    private const val VOID_BLOCK_KEY = "mconi:void"
}
