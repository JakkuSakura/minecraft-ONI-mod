package mconi.common.world

import mconi.common.sim.OniServices
import mconi.common.sim.OniSimulationConfig
import mconi.common.sim.OniSimulationGrid
import mconi.common.sim.OniWorldFoundation
import mconi.common.element.OniElements
import mconi.common.sim.model.OccupancyState
import mconi.common.sim.model.OniCellState
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.block.Blocks
import mconi.common.block.OniBlockLookup
import mconi.common.block.OniBlockFactory
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
            val liquidId = if (state.fluidState.`is`(Fluids.LAVA)) {
                OniElements.LIQUID_LAVA
            } else {
                OniElements.LIQUID_WATER
            }
            if (!sameBlock || cell.occupancyState() != OccupancyState.FLUID || cell.fluidId() != liquidId) {
                clearCellMass(cell)
                cell.setFluidState(liquidId, OniBlockFactory.liquidDefaultMassKg(liquidId).toDouble())
                cell.setOccupancyState(OccupancyState.FLUID)
                cell.setTemperatureK(if (liquidId == OniElements.LIQUID_LAVA) 1300.0 else 293.15)
                cell.setWorldBlockKey(blockKey)
            }
            return
        }

        val waterBlock = OniBlockLookup.block(OniBlockFactory.WATER)
        val pollutedWaterBlock = OniBlockLookup.block(OniBlockFactory.POLLUTED_WATER)
        val crudeOilBlock = OniBlockLookup.block(OniBlockFactory.CRUDE_OIL)
        val lavaBlock = OniBlockLookup.block(OniBlockFactory.LAVA)
        if (state.`is`(waterBlock) || state.`is`(pollutedWaterBlock) || state.`is`(crudeOilBlock) || state.`is`(lavaBlock)) {
            val liquidId = when {
                state.`is`(lavaBlock) -> OniElements.LIQUID_LAVA
                state.`is`(pollutedWaterBlock) -> OniElements.LIQUID_POLLUTED_WATER
                state.`is`(crudeOilBlock) -> OniElements.LIQUID_CRUDE_OIL
                else -> OniElements.LIQUID_WATER
            }
            if (!sameBlock || cell.occupancyState() != OccupancyState.FLUID || cell.fluidId() != liquidId) {
                cell.setFluidState(liquidId, OniBlockFactory.liquidDefaultMassKg(liquidId).toDouble())
                cell.setTemperatureK(if (liquidId == OniElements.LIQUID_LAVA) 1300.0 else 293.15)
                cell.setOccupancyState(OccupancyState.FLUID)
                cell.setWorldBlockKey(blockKey)
            }
            return
        }

        val oxygenBlock = OniBlockLookup.block(OniBlockFactory.OXYGEN_GAS)
        val co2Block = OniBlockLookup.block(OniBlockFactory.CARBON_DIOXIDE_GAS)
        val hydrogenBlock = OniBlockLookup.block(OniBlockFactory.HYDROGEN_GAS)
        if (state.`is`(oxygenBlock)) {
            if (!sameBlock || cell.occupancyState() != OccupancyState.GAS) {
                clearCellMass(cell)
                cell.setGasMassKg(OniElements.GAS_OXYGEN, config.baseO2MassKg())
                cell.setOccupancyState(OccupancyState.GAS)
                cell.setTemperatureK(293.15)
                cell.setWorldBlockKey(blockKey)
            }
            return
        }
        if (state.`is`(co2Block)) {
            if (!sameBlock || cell.occupancyState() != OccupancyState.GAS) {
                clearCellMass(cell)
                cell.setGasMassKg(OniElements.GAS_CARBON_DIOXIDE, config.baseCO2MassKg())
                cell.setOccupancyState(OccupancyState.GAS)
                cell.setTemperatureK(293.15)
                cell.setWorldBlockKey(blockKey)
            }
            return
        }
        if (state.`is`(hydrogenBlock)) {
            if (!sameBlock || cell.occupancyState() != OccupancyState.GAS) {
                clearCellMass(cell)
                cell.setGasMassKg(OniElements.GAS_HYDROGEN, config.baseH2MassKg())
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
            cell.setGasMassKg(OniElements.GAS_OXYGEN, config.baseO2MassKg())
            cell.setGasMassKg(OniElements.GAS_CARBON_DIOXIDE, config.baseCO2MassKg())
            cell.setGasMassKg(OniElements.GAS_HYDROGEN, 0.0)
            cell.setTemperatureK(293.15)
            cell.setWorldBlockKey(blockKey)
        }

        if (OniWorldFoundation.isLavaBand(y, minY, config)) {
            val mass = OniBlockFactory.liquidDefaultMassKg(OniElements.LIQUID_LAVA).toDouble()
            cell.setFluidState(OniElements.LIQUID_LAVA, mass)
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
        val algaeBlock = OniBlockLookup.block(OniBlockFactory.ALGAE)
        val oxygenDiffuser = OniBlockLookup.block(OniBlockFactory.OXYGEN_DIFFUSER)
        val algaeDeoxidizer = OniBlockLookup.block(OniBlockFactory.ALGAE_DEOXIDIZER)
        val co2Scrubber = OniBlockLookup.block(OniBlockFactory.CO2_SCRUBBER)
        val gasPump = OniBlockLookup.block(OniBlockFactory.GAS_PUMP)
        val liquidPump = OniBlockLookup.block(OniBlockFactory.LIQUID_PUMP)
        val coalGenerator = OniBlockLookup.block(OniBlockFactory.COAL_GENERATOR)
        val hydrogenGenerator = OniBlockLookup.block(OniBlockFactory.HYDROGEN_GENERATOR)
        val naturalGasGenerator = OniBlockLookup.block(OniBlockFactory.NATURAL_GAS_GENERATOR)
        val petroleumGenerator = OniBlockLookup.block(OniBlockFactory.PETROLEUM_GENERATOR)
        if (state.`is`(Blocks.MOSS_BLOCK) || state.`is`(Blocks.MOSS_CARPET) || state.`is`(Blocks.SEAGRASS) || state.`is`(algaeBlock)) {
            injectGasAt(grid, x, y + 1, z, minY, maxY, config.baseO2MassKg(), 0.0)
        }

        if (state.`is`(Blocks.CAMPFIRE) || state.`is`(Blocks.FURNACE) || state.`is`(Blocks.BLAST_FURNACE)) {
            injectGasAt(grid, x, y + 1, z, minY, maxY, 0.0, config.baseCO2MassKg())
        }

        if (state.`is`(oxygenDiffuser)) {
            injectGasAt(grid, x, y + 1, z, minY, maxY, OXYGEN_DIFFUSER_O2_KG, 0.0)
        }
        if (state.`is`(algaeDeoxidizer)) {
            injectGasAt(grid, x, y + 1, z, minY, maxY, ALGAE_DEOXIDIZER_O2_KG, ALGAE_DEOXIDIZER_CO2_KG)
        }
        if (state.`is`(co2Scrubber)) {
            scrubGasAt(grid, x, y + 1, z, minY, maxY, CO2_SCRUBBER_CO2_KG, CO2_SCRUBBER_O2_KG)
        }
        if (state.`is`(gasPump)) {
            pumpGasAt(grid, x, y + 1, z, minY, maxY, GAS_PUMP_KG_PER_STEP)
        }
        if (state.`is`(liquidPump)) {
            pumpFluidAt(grid, x, y + 1, z, minY, maxY, LIQUID_PUMP_KG_PER_STEP)
        }
        if (state.`is`(coalGenerator) || state.`is`(naturalGasGenerator) || state.`is`(petroleumGenerator)) {
            injectGasAt(grid, x, y + 1, z, minY, maxY, 0.0, GENERATOR_CO2_KG)
        }
        if (state.`is`(hydrogenGenerator)) {
            consumeGasAt(grid, x, y + 1, z, minY, maxY, OniElements.GAS_HYDROGEN, HYDROGEN_GENERATOR_CONSUME_KG)
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
        cell.setGasMassKg(OniElements.GAS_OXYGEN, cell.gasMassKg(OniElements.GAS_OXYGEN) + o2Mass)
        cell.setGasMassKg(OniElements.GAS_CARBON_DIOXIDE, cell.gasMassKg(OniElements.GAS_CARBON_DIOXIDE) + co2Mass)
    }

    private fun clearCellMass(cell: OniCellState) {
        cell.setFluidState(OniElements.LIQUID_NONE, 0.0)
        cell.setGasMassKg(OniElements.GAS_OXYGEN, 0.0)
        cell.setGasMassKg(OniElements.GAS_CARBON_DIOXIDE, 0.0)
        cell.setGasMassKg(OniElements.GAS_HYDROGEN, 0.0)
    }

    private fun scrubGasAt(
        grid: OniSimulationGrid,
        x: Int,
        y: Int,
        z: Int,
        minY: Int,
        maxY: Int,
        co2Kg: Double,
        o2Kg: Double
    ) {
        if (y < minY || y > maxY) {
            return
        }
        val cell = grid.getOrCreateCellAtBlock(x, y, z, OniServices.simulationRuntime().config().cellSize())
        if (cell.occupancyState() == OccupancyState.SOLID) {
            return
        }
        val current = cell.gasMassKg(OniElements.GAS_CARBON_DIOXIDE)
        val removed = minOf(current, co2Kg).coerceAtLeast(0.0)
        cell.setGasMassKg(OniElements.GAS_CARBON_DIOXIDE, current - removed)
        if (o2Kg > 0.0) {
            cell.setGasMassKg(OniElements.GAS_OXYGEN, cell.gasMassKg(OniElements.GAS_OXYGEN) + o2Kg)
        }
        cell.setOccupancyState(OccupancyState.GAS)
    }

    private fun pumpGasAt(
        grid: OniSimulationGrid,
        x: Int,
        y: Int,
        z: Int,
        minY: Int,
        maxY: Int,
        amountKg: Double
    ) {
        if (y < minY || y > maxY) {
            return
        }
        val cell = grid.getOrCreateCellAtBlock(x, y, z, OniServices.simulationRuntime().config().cellSize())
        if (cell.occupancyState() != OccupancyState.GAS) {
            return
        }
        val total = cell.totalGasMassKg()
        if (total <= 0.0) {
            return
        }
        val removed = minOf(total, amountKg).coerceAtLeast(0.0)
        val ratio = (total - removed) / total
        for (species in OniElements.GASES) {
            cell.setGasMassKg(species, cell.gasMassKg(species) * ratio)
        }
    }

    private fun pumpFluidAt(
        grid: OniSimulationGrid,
        x: Int,
        y: Int,
        z: Int,
        minY: Int,
        maxY: Int,
        amountKg: Double
    ) {
        if (y < minY || y > maxY) {
            return
        }
        val cell = grid.getOrCreateCellAtBlock(x, y, z, OniServices.simulationRuntime().config().cellSize())
        if (cell.occupancyState() != OccupancyState.FLUID) {
            return
        }
        val mass = cell.fluidMassKg()
        if (mass <= 0.0) {
            return
        }
        val remaining = (mass - amountKg).coerceAtLeast(0.0)
        if (remaining <= 0.0) {
            cell.setFluidState(OniElements.LIQUID_NONE, 0.0)
        } else {
            cell.setFluidState(cell.fluidId(), remaining)
        }
    }

    private fun consumeGasAt(
        grid: OniSimulationGrid,
        x: Int,
        y: Int,
        z: Int,
        minY: Int,
        maxY: Int,
        species: OniElements.GasSpec,
        amountKg: Double
    ) {
        if (y < minY || y > maxY) {
            return
        }
        val cell = grid.getOrCreateCellAtBlock(x, y, z, OniServices.simulationRuntime().config().cellSize())
        if (cell.occupancyState() != OccupancyState.GAS) {
            return
        }
        val current = cell.gasMassKg(species)
        val remaining = (current - amountKg).coerceAtLeast(0.0)
        cell.setGasMassKg(species, remaining)
    }

    private const val VOID_BLOCK_KEY = "mconi:void"
    private const val OXYGEN_DIFFUSER_O2_KG = 1.6
    private const val ALGAE_DEOXIDIZER_O2_KG = 1.2
    private const val ALGAE_DEOXIDIZER_CO2_KG = 0.05
    private const val CO2_SCRUBBER_CO2_KG = 1.0
    private const val CO2_SCRUBBER_O2_KG = 0.7
    private const val GAS_PUMP_KG_PER_STEP = 1.5
    private const val LIQUID_PUMP_KG_PER_STEP = 120.0
    private const val GENERATOR_CO2_KG = 0.8
    private const val HYDROGEN_GENERATOR_CONSUME_KG = 0.6
}
