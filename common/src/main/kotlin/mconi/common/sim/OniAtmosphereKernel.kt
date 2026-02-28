package mconi.common.sim

import mconi.common.element.OniElements
import mconi.common.sim.model.OccupancyState
import mconi.common.sim.model.OniBlockData
import mconi.common.world.OniChunkDataAccess
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import java.util.LinkedHashMap

/**
 * Initial atmosphere kernel pass: resolves occupancy and computes pressure from mass.
 */
class OniAtmosphereKernel {
    fun run(level: ServerLevel, config: OniSimulationConfig) {
        val cellVolume = Math.pow(config.cellSize().toDouble(), 3.0)
        val entries: List<mconi.common.world.BlockEntryView> = OniChunkDataAccess.blockEntries(level)
        for (entry in entries) {
            updateCell(entry.data, config)
        }
        val diffusionDeltas = diffuseGases(entries, config) { pos ->
            OniChunkDataAccess.get(level, pos)
        }
        applyGasDeltas(diffusionDeltas) { pos ->
            OniChunkDataAccess.getOrCreate(level, pos)
        }
        val stratifyDeltas = stratifyGases(entries, config) { pos ->
            OniChunkDataAccess.get(level, pos)
        }
        applyGasDeltas(stratifyDeltas) { pos ->
            OniChunkDataAccess.getOrCreate(level, pos)
        }
        for (entry in entries) {
            updatePressure(entry.data, cellVolume)
        }
    }

    internal fun runOnCells(cells: MutableMap<BlockPos, OniBlockData>, config: OniSimulationConfig) {
        val cellVolume = Math.pow(config.cellSize().toDouble(), 3.0)
        val entries: List<mconi.common.world.BlockEntryView> = cells.entries.map { entry ->
            mconi.common.world.BlockEntryView(entry.key, entry.value)
        }
        for (entry in entries) {
            updateCell(entry.data, config)
        }
        val diffusionDeltas = diffuseGases(entries, config) { pos -> cells[pos] }
        applyGasDeltas(diffusionDeltas) { pos -> cells[pos] }
        val stratifyDeltas = stratifyGases(entries, config) { pos -> cells[pos] }
        applyGasDeltas(stratifyDeltas) { pos -> cells[pos] }
        for (entry in entries) {
            updatePressure(entry.data, cellVolume)
        }
    }

    private fun updateCell(cell: OniBlockData, config: OniSimulationConfig) {
        if (cell.occupancyState() == OccupancyState.SOLID) {
            cell.setLiquidState(OniElements.LIQUID_NONE, 0.0)
            for (species in OniElements.GASES) {
                cell.setGasMassKg(species, 0.0)
            }
            cell.setPressureKpa(0.0)
            return
        }

        if (cell.occupancyState() == OccupancyState.VOID) {
            drainVoid(cell, config)
            cell.setPressureKpa(0.0)
            return
        }

        val totalGasMassKg = cell.totalGasMassKg()
        if (cell.liquidId() != OniElements.LIQUID_NONE && cell.liquidMassKg() > 0.0) {
            cell.setOccupancyState(OccupancyState.LIQUID)
            return
        }

        if (totalGasMassKg > 0.0) {
            cell.setOccupancyState(OccupancyState.GAS)
            return
        }

        cell.setOccupancyState(OccupancyState.VACUUM)
    }

    private fun updatePressure(cell: OniBlockData, cellVolumeM3: Double) {
        if (cell.occupancyState() != OccupancyState.GAS) {
            cell.setPressureKpa(0.0)
            return
        }
        val totalGasMassKg = cell.totalGasMassKg()
        val scaledDensity = totalGasMassKg / maxOf(0.0001, cellVolumeM3)
        val pressureKpa = (scaledDensity / STANDARD_AIR_DENSITY_KG_PER_M3) *
            (cell.temperatureK() / STANDARD_TEMPERATURE_K) *
            STANDARD_PRESSURE_KPA
        cell.setPressureKpa(pressureKpa)
    }

    private fun drainVoid(cell: OniBlockData, config: OniSimulationConfig) {
        val drainFraction = config.voidGasDrainFraction().coerceIn(0.0, 1.0)
        for (species in OniElements.GASES) {
            val next = cell.gasMassKg(species) * (1.0 - drainFraction)
            cell.setGasMassKg(species, next)
        }
        val nextLiquid = cell.liquidMassKg() * (1.0 - config.voidLiquidDrainFraction().coerceIn(0.0, 1.0))
        if (nextLiquid <= 0.0) {
            cell.setLiquidState(OniElements.LIQUID_NONE, 0.0)
        } else {
            cell.setLiquidState(cell.liquidId(), nextLiquid)
        }
    }

    private fun diffuseGases(
        entries: List<mconi.common.world.BlockEntryView>,
        config: OniSimulationConfig,
        lookup: (BlockPos) -> OniBlockData?
    ): Map<BlockPos, MutableMap<OniElements.GasSpec, Double>> {
        val maxTransfer = config.gasTransferKgPerStep().coerceAtLeast(0.0)
        if (maxTransfer <= 0.0) {
            return emptyMap()
        }

        val deltas: MutableMap<BlockPos, MutableMap<OniElements.GasSpec, Double>> = HashMap()
        for (entry in entries) {
            val coordinate = entry.pos
            val cell = entry.data
            if (cell.occupancyState() != OccupancyState.GAS) {
                continue
            }
            for (neighbor in neighborsOf(coordinate)) {
                val other = lookup(neighbor) ?: continue
                if (other.occupancyState() != OccupancyState.GAS) {
                    continue
                }
                if (coordinate.x > neighbor.x ||
                    (coordinate.x == neighbor.x && coordinate.y > neighbor.y) ||
                    (coordinate.x == neighbor.x && coordinate.y == neighbor.y && coordinate.z > neighbor.z)
                ) {
                    continue
                }
                for (species in OniElements.GASES) {
                    val massA = cell.gasMassKg(species)
                    val massB = other.gasMassKg(species)
                    val diff = massA - massB
                    if (kotlin.math.abs(diff) < 0.0001) {
                        continue
                    }
                    val transfer = diff * 0.1
                    val clamped = transfer.coerceIn(-maxTransfer, maxTransfer)
                    addDelta(deltas, coordinate, species, -clamped)
                    addDelta(deltas, neighbor, species, clamped)
                }
            }
        }
        return deltas
    }

    private fun stratifyGases(
        entries: List<mconi.common.world.BlockEntryView>,
        config: OniSimulationConfig,
        lookup: (BlockPos) -> OniBlockData?
    ): Map<BlockPos, MutableMap<OniElements.GasSpec, Double>> {
        val maxTransfer = config.gasTransferKgPerStep().coerceAtLeast(0.0)
        if (maxTransfer <= 0.0) {
            return emptyMap()
        }
        val deltas: MutableMap<BlockPos, MutableMap<OniElements.GasSpec, Double>> = HashMap()
        for (entry in entries) {
            val coordinate = entry.pos
            val cell = entry.data
            if (cell.occupancyState() != OccupancyState.GAS) {
                continue
            }

            val below = BlockPos(coordinate.x, coordinate.y - 1, coordinate.z)
            val above = BlockPos(coordinate.x, coordinate.y + 1, coordinate.z)
            val belowCell = lookup(below)
            val aboveCell = lookup(above)

            if (belowCell != null && belowCell.occupancyState() == OccupancyState.GAS) {
                stratifyDown(OniElements.GAS_CARBON_DIOXIDE, cell, belowCell, maxTransfer, coordinate, below, deltas)
                stratifyDown(OniElements.GAS_OXYGEN, cell, belowCell, maxTransfer * 0.5, coordinate, below, deltas)
            }
            if (aboveCell != null && aboveCell.occupancyState() == OccupancyState.GAS) {
                stratifyUp(OniElements.GAS_HYDROGEN, cell, aboveCell, maxTransfer, coordinate, above, deltas)
            }
        }

        return deltas
    }

    private fun stratifyDown(
        species: OniElements.GasSpec,
        cell: OniBlockData,
        belowCell: OniBlockData,
        maxTransfer: Double,
        from: BlockPos,
        to: BlockPos,
        deltas: MutableMap<BlockPos, MutableMap<OniElements.GasSpec, Double>>,
    ) {
        val massA = cell.gasMassKg(species)
        val massB = belowCell.gasMassKg(species)
        if (massA <= massB) {
            return
        }
        val transfer = ((massA - massB) * 0.15).coerceAtMost(maxTransfer)
        if (transfer <= 0.0) {
            return
        }
        addDelta(deltas, from, species, -transfer)
        addDelta(deltas, to, species, transfer)
    }

    private fun stratifyUp(
        species: OniElements.GasSpec,
        cell: OniBlockData,
        aboveCell: OniBlockData,
        maxTransfer: Double,
        from: BlockPos,
        to: BlockPos,
        deltas: MutableMap<BlockPos, MutableMap<OniElements.GasSpec, Double>>,
    ) {
        val massA = cell.gasMassKg(species)
        val massB = aboveCell.gasMassKg(species)
        if (massA <= massB) {
            return
        }
        val transfer = ((massA - massB) * 0.2).coerceAtMost(maxTransfer)
        if (transfer <= 0.0) {
            return
        }
        addDelta(deltas, from, species, -transfer)
        addDelta(deltas, to, species, transfer)
    }

    private fun neighborsOf(coordinate: BlockPos): List<BlockPos> {
        return listOf(
            BlockPos(coordinate.x + 1, coordinate.y, coordinate.z),
            BlockPos(coordinate.x - 1, coordinate.y, coordinate.z),
            BlockPos(coordinate.x, coordinate.y + 1, coordinate.z),
            BlockPos(coordinate.x, coordinate.y - 1, coordinate.z),
            BlockPos(coordinate.x, coordinate.y, coordinate.z + 1),
            BlockPos(coordinate.x, coordinate.y, coordinate.z - 1),
        )
    }

    private fun addDelta(
        deltas: MutableMap<BlockPos, MutableMap<OniElements.GasSpec, Double>>,
        coordinate: BlockPos,
        species: OniElements.GasSpec,
        delta: Double,
    ) {
        val map = deltas.computeIfAbsent(coordinate) { LinkedHashMap() }
        map[species] = (map[species] ?: 0.0) + delta
    }

    private fun applyGasDeltas(
        deltas: Map<BlockPos, MutableMap<OniElements.GasSpec, Double>>,
        cellProvider: (BlockPos) -> OniBlockData?,
    ) {
        for ((coordinate, speciesDelta) in deltas) {
            val cell = cellProvider(coordinate) ?: continue
            for ((species, delta) in speciesDelta) {
                cell.setGasMassKg(species, cell.gasMassKg(species) + delta)
            }
        }
    }

    companion object {
        private const val STANDARD_TEMPERATURE_K = 293.15
        private const val STANDARD_PRESSURE_KPA = 101.325
        private const val STANDARD_AIR_DENSITY_KG_PER_M3 = 1.225
    }
}
