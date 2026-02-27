package mconi.common.sim

import mconi.common.element.OniElements
import mconi.common.sim.model.OccupancyState
import mconi.common.sim.model.OniCellCoordinate
import mconi.common.sim.model.OniCellState
import java.util.LinkedHashMap

/**
 * Initial atmosphere kernel pass: resolves occupancy and computes pressure from mass.
 */
class OniAtmosphereKernel {
    fun run(grid: OniSimulationGrid, config: OniSimulationConfig) {
        val cellVolume = Math.pow(config.cellSize().toDouble(), 3.0)
        for (cell in grid.cells()) {
            updateCell(cell, config)
        }
        diffuseGases(grid, config)
        stratifyGases(grid, config)
        for (cell in grid.cells()) {
            updatePressure(cell, cellVolume)
        }
    }

    private fun updateCell(cell: OniCellState, config: OniSimulationConfig) {
        if (cell.occupancyState() == OccupancyState.SOLID) {
            cell.setFluidState(OniElements.LIQUID_NONE, 0.0)
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
        if (cell.fluidId() != OniElements.LIQUID_NONE && cell.fluidMassKg() > 0.0) {
            cell.setOccupancyState(OccupancyState.FLUID)
            return
        }

        if (totalGasMassKg > 0.0) {
            cell.setOccupancyState(OccupancyState.GAS)
            return
        }

        cell.setOccupancyState(OccupancyState.VACUUM)
    }

    private fun updatePressure(cell: OniCellState, cellVolumeM3: Double) {
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

    private fun drainVoid(cell: OniCellState, config: OniSimulationConfig) {
        val drainFraction = config.voidGasDrainFraction().coerceIn(0.0, 1.0)
        for (species in OniElements.GASES) {
            val next = cell.gasMassKg(species) * (1.0 - drainFraction)
            cell.setGasMassKg(species, next)
        }
        val nextFluid = cell.fluidMassKg() * (1.0 - config.voidFluidDrainFraction().coerceIn(0.0, 1.0))
        if (nextFluid <= 0.0) {
            cell.setFluidState(OniElements.LIQUID_NONE, 0.0)
        } else {
            cell.setFluidState(cell.fluidId(), nextFluid)
        }
    }

    private fun diffuseGases(grid: OniSimulationGrid, config: OniSimulationConfig) {
        val maxTransfer = config.gasTransferKgPerStep().coerceAtLeast(0.0)
        if (maxTransfer <= 0.0) {
            return
        }

        val deltas: MutableMap<OniCellCoordinate, MutableMap<OniElements.GasSpec, Double>> = HashMap()
        for ((coordinate, cell) in grid.cellEntries()) {
            if (cell.occupancyState() != OccupancyState.GAS) {
                continue
            }
            for (neighbor in neighborsOf(coordinate)) {
                val other = grid.getCellAtCoordinate(neighbor) ?: continue
                if (other.occupancyState() != OccupancyState.GAS) {
                    continue
                }
                if (coordinate.cellX() > neighbor.cellX() ||
                    (coordinate.cellX() == neighbor.cellX() && coordinate.cellY() > neighbor.cellY()) ||
                    (coordinate.cellX() == neighbor.cellX() && coordinate.cellY() == neighbor.cellY() && coordinate.cellZ() > neighbor.cellZ())
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

        for ((coordinate, speciesDelta) in deltas) {
            val cell = grid.getOrCreateCellAtCoordinate(coordinate)
            for ((species, delta) in speciesDelta) {
                cell.setGasMassKg(species, cell.gasMassKg(species) + delta)
            }
        }
    }

    private fun stratifyGases(grid: OniSimulationGrid, config: OniSimulationConfig) {
        val maxTransfer = config.gasTransferKgPerStep().coerceAtLeast(0.0)
        if (maxTransfer <= 0.0) {
            return
        }
        val deltas: MutableMap<OniCellCoordinate, MutableMap<OniElements.GasSpec, Double>> = HashMap()
        for ((coordinate, cell) in grid.cellEntries()) {
            if (cell.occupancyState() != OccupancyState.GAS) {
                continue
            }

            val below = OniCellCoordinate(coordinate.cellX(), coordinate.cellY() - 1, coordinate.cellZ())
            val above = OniCellCoordinate(coordinate.cellX(), coordinate.cellY() + 1, coordinate.cellZ())
            val belowCell = grid.getCellAtCoordinate(below)
            val aboveCell = grid.getCellAtCoordinate(above)

            if (belowCell != null && belowCell.occupancyState() == OccupancyState.GAS) {
                stratifyDown(OniElements.GAS_CARBON_DIOXIDE, cell, belowCell, maxTransfer, coordinate, below, deltas)
                stratifyDown(OniElements.GAS_OXYGEN, cell, belowCell, maxTransfer * 0.5, coordinate, below, deltas)
            }
            if (aboveCell != null && aboveCell.occupancyState() == OccupancyState.GAS) {
                stratifyUp(OniElements.GAS_HYDROGEN, cell, aboveCell, maxTransfer, coordinate, above, deltas)
            }
        }

        for ((coordinate, speciesDelta) in deltas) {
            val cell = grid.getOrCreateCellAtCoordinate(coordinate)
            for ((species, delta) in speciesDelta) {
                cell.setGasMassKg(species, cell.gasMassKg(species) + delta)
            }
        }
    }

    private fun stratifyDown(
        species: OniElements.GasSpec,
        cell: OniCellState,
        belowCell: OniCellState,
        maxTransfer: Double,
        from: OniCellCoordinate,
        to: OniCellCoordinate,
        deltas: MutableMap<OniCellCoordinate, MutableMap<OniElements.GasSpec, Double>>,
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
        cell: OniCellState,
        aboveCell: OniCellState,
        maxTransfer: Double,
        from: OniCellCoordinate,
        to: OniCellCoordinate,
        deltas: MutableMap<OniCellCoordinate, MutableMap<OniElements.GasSpec, Double>>,
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

    private fun neighborsOf(coordinate: OniCellCoordinate): List<OniCellCoordinate> {
        return listOf(
            OniCellCoordinate(coordinate.cellX() + 1, coordinate.cellY(), coordinate.cellZ()),
            OniCellCoordinate(coordinate.cellX() - 1, coordinate.cellY(), coordinate.cellZ()),
            OniCellCoordinate(coordinate.cellX(), coordinate.cellY() + 1, coordinate.cellZ()),
            OniCellCoordinate(coordinate.cellX(), coordinate.cellY() - 1, coordinate.cellZ()),
            OniCellCoordinate(coordinate.cellX(), coordinate.cellY(), coordinate.cellZ() + 1),
            OniCellCoordinate(coordinate.cellX(), coordinate.cellY(), coordinate.cellZ() - 1),
        )
    }

    private fun addDelta(
        deltas: MutableMap<OniCellCoordinate, MutableMap<OniElements.GasSpec, Double>>,
        coordinate: OniCellCoordinate,
        species: OniElements.GasSpec,
        delta: Double,
    ) {
        val map = deltas.computeIfAbsent(coordinate) { LinkedHashMap() }
        map[species] = (map[species] ?: 0.0) + delta
    }

    companion object {
        private const val STANDARD_TEMPERATURE_K = 293.15
        private const val STANDARD_PRESSURE_KPA = 101.325
        private const val STANDARD_AIR_DENSITY_KG_PER_M3 = 1.225
    }
}
