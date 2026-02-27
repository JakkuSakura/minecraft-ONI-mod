package mconi.common.world

import mconi.common.block.OniBlockLookup
import mconi.common.content.OniBlockIds
import mconi.common.sim.OniServices
import mconi.common.sim.OniWorldFoundation
import mconi.common.sim.model.FluidSpecies
import mconi.common.sim.model.GasSpecies
import mconi.common.sim.model.OccupancyState
import mconi.common.sim.model.OniCellCoordinate
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.material.Fluids
import java.util.concurrent.atomic.AtomicLong

object OniWorldWriter {
    private val writeTick = AtomicLong(0L)
    private val oxygenBlock = OniBlockLookup.block(OniBlockIds.OXYGEN_GAS)
    private val co2Block = OniBlockLookup.block(OniBlockIds.CARBON_DIOXIDE_GAS)
    private val hydrogenBlock = OniBlockLookup.block(OniBlockIds.HYDROGEN_GAS)
    private val waterBlock = OniBlockLookup.block(OniBlockIds.WATER)
    private val pollutedWaterBlock = OniBlockLookup.block(OniBlockIds.POLLUTED_WATER)
    private val crudeOilBlock = OniBlockLookup.block(OniBlockIds.CRUDE_OIL)
    private val lavaBlock = OniBlockLookup.block(OniBlockIds.LAVA)

    @JvmStatic
    fun applyAroundPlayers(server: MinecraftServer) {
        val config = OniServices.simulationRuntime().config()
        val tick = writeTick.incrementAndGet()
        if (tick % config.worldSampleIntervalTicks() != 0L) {
            return
        }
        val level = server.overworld() ?: return
        for (player in level.players()) {
            val pos = player.blockPosition()
            applyBox(level, pos.x, pos.y, pos.z, config.worldSampleRadiusBlocks())
        }
    }

    private fun applyBox(level: ServerLevel, centerX: Int, centerY: Int, centerZ: Int, radiusBlocks: Int) {
        val config = OniServices.simulationRuntime().config()
        val grid = OniServices.simulationRuntime().grid()
        val cellSize = config.cellSize()
        val minY = level.minY
        val maxY = level.maxY - 1

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
                    applyCell(level, x, y, z, minY, maxY, config, grid)
                    z += cellSize
                }
                y += cellSize
            }
            x += cellSize
        }
    }

    private fun applyCell(
        level: ServerLevel,
        x: Int,
        y: Int,
        z: Int,
        minY: Int,
        maxY: Int,
        config: mconi.common.sim.OniSimulationConfig,
        grid: mconi.common.sim.OniSimulationGrid
    ) {
        if (!OniWorldFoundation.isWithinHorizontalBounds(x, z, config)) {
            return
        }
        val cell = grid.getCellAtCoordinate(OniCellCoordinate.fromBlockPosition(x, y, z, config.cellSize()))
        if (OniWorldFoundation.isVoidBand(y, maxY, config)) {
            val target = Blocks.AIR.defaultBlockState()
            setIfReplaceable(level, x, y, z, target)
            cell?.setWorldBlockKey(BuiltInRegistries.BLOCK.getKey(target.block).toString())
            return
        }
        if (OniWorldFoundation.isLavaBand(y, minY, config)) {
            val target = OniBlockLookup.state(OniBlockIds.LAVA)
            setIfReplaceable(level, x, y, z, target)
            cell?.setWorldBlockKey(BuiltInRegistries.BLOCK.getKey(target.block).toString())
            return
        }

        if (cell == null) {
            return
        }
        if (cell.occupancyState() == OccupancyState.SOLID) {
            return
        }

        val state = level.getBlockState(BlockPos(x, y, z))
        if (!state.isAir && state.fluidState.isEmpty && !state.`is`(oxygenBlock) &&
            !state.`is`(co2Block) &&
            !state.`is`(hydrogenBlock) &&
            !state.`is`(waterBlock) &&
            !state.`is`(pollutedWaterBlock) &&
            !state.`is`(crudeOilBlock) &&
            !state.`is`(lavaBlock)
        ) {
            return
        }

        when (cell.occupancyState()) {
            OccupancyState.FLUID -> {
                val target = when (cell.fluidSpecies()) {
                    FluidSpecies.LAVA -> OniBlockLookup.state(OniBlockIds.LAVA)
                    FluidSpecies.WATER -> OniBlockLookup.state(OniBlockIds.WATER)
                    FluidSpecies.POLLUTED_WATER -> OniBlockLookup.state(OniBlockIds.POLLUTED_WATER)
                    FluidSpecies.CRUDE_OIL -> OniBlockLookup.state(OniBlockIds.CRUDE_OIL)
                    else -> Blocks.AIR.defaultBlockState()
                }
                setIfReplaceable(level, x, y, z, target)
                cell.setWorldBlockKey(BuiltInRegistries.BLOCK.getKey(target.block).toString())
            }
            OccupancyState.GAS -> {
                val total = cell.totalGasMassKg()
                if (total <= 0.001) {
                    val target = Blocks.AIR.defaultBlockState()
                    setIfReplaceable(level, x, y, z, target)
                    cell.setWorldBlockKey(BuiltInRegistries.BLOCK.getKey(target.block).toString())
                    return
                }
                val dominant = dominantGas(cell)
                val gasState = when (dominant) {
                    GasSpecies.O2 -> OniBlockLookup.state(OniBlockIds.OXYGEN_GAS)
                    GasSpecies.CO2 -> OniBlockLookup.state(OniBlockIds.CARBON_DIOXIDE_GAS)
                    GasSpecies.H2 -> OniBlockLookup.state(OniBlockIds.HYDROGEN_GAS)
                }
                setIfReplaceable(level, x, y, z, gasState)
                cell.setWorldBlockKey(BuiltInRegistries.BLOCK.getKey(gasState.block).toString())
            }
            OccupancyState.VACUUM,
            OccupancyState.VOID -> {
                val target = Blocks.AIR.defaultBlockState()
                setIfReplaceable(level, x, y, z, target)
                cell.setWorldBlockKey(BuiltInRegistries.BLOCK.getKey(target.block).toString())
            }
            else -> {
            }
        }
    }

    private fun dominantGas(cell: mconi.common.sim.model.OniCellState): GasSpecies {
        var best = GasSpecies.O2
        var bestMass = cell.gasMassKg(GasSpecies.O2)
        val co2 = cell.gasMassKg(GasSpecies.CO2)
        if (co2 > bestMass) {
            best = GasSpecies.CO2
            bestMass = co2
        }
        val h2 = cell.gasMassKg(GasSpecies.H2)
        if (h2 > bestMass) {
            best = GasSpecies.H2
        }
        return best
    }

    private fun setIfReplaceable(level: ServerLevel, x: Int, y: Int, z: Int, state: net.minecraft.world.level.block.state.BlockState) {
        val pos = BlockPos(x, y, z)
        val current = level.getBlockState(pos)
        if (current == state) {
            return
        }
        if (current.isAir || current.fluidState.`is`(Fluids.WATER) || current.fluidState.`is`(Fluids.LAVA) ||
            current.`is`(oxygenBlock) ||
            current.`is`(co2Block) ||
            current.`is`(hydrogenBlock) ||
            current.`is`(waterBlock) ||
            current.`is`(pollutedWaterBlock) ||
            current.`is`(crudeOilBlock) ||
            current.`is`(lavaBlock)
        ) {
            level.setBlock(pos, state, 2)
        }
    }
}
