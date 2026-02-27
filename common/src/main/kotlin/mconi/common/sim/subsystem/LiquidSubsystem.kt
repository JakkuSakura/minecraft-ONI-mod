package mconi.common.sim.subsystem

import mconi.common.element.OniElements
import mconi.common.sim.model.OccupancyState
import mconi.common.sim.model.OniCellCoordinate

class LiquidSubsystem : SimulationSubsystem {
    override fun id(): String = "liquid"

    override fun run(context: SimulationContext) {
        val grid = context.grid()
        val maxTransfer = context.config().liquidTransferKgPerStep().coerceAtLeast(0.0)
        val voidDrain = context.config().voidLiquidDrainFraction().coerceIn(0.0, 1.0)

        val deltas: MutableMap<OniCellCoordinate, MutableMap<String, Double>> = HashMap()
        for ((coordinate, cell) in grid.cellEntries()) {
            if (cell.occupancyState() == OccupancyState.SOLID) {
                continue
            }

            val mass = cell.liquidMassKg()
            val species = cell.liquidId()
            if (species == OniElements.LIQUID_NONE || mass <= 0.0) {
                cell.setLiquidState(OniElements.LIQUID_NONE, 0.0)
                continue
            }

            if (cell.occupancyState() == OccupancyState.VOID) {
                val drained = mass * (1.0 - voidDrain)
                cell.setLiquidState(species, drained.coerceAtLeast(0.0))
                continue
            }

            var nextMass = mass.coerceAtMost(MAX_LIQUID_MASS_PER_CELL_KG)

            if ((species == OniElements.LIQUID_WATER || species == OniElements.LIQUID_POLLUTED_WATER) && cell.temperatureK() > 373.15) {
                nextMass -= BOIL_OFF_KG_PER_STEP
            }

            if (nextMass <= 0.0) {
                cell.setLiquidState(OniElements.LIQUID_NONE, 0.0)
                continue
            }

            val below = OniCellCoordinate(coordinate.cellX(), coordinate.cellY() - 1, coordinate.cellZ())
            val belowCell = grid.getCellAtCoordinate(below)
            if (belowCell != null && belowCell.occupancyState() != OccupancyState.SOLID) {
                if (belowCell.liquidId() != OniElements.LIQUID_NONE && belowCell.liquidId() != species) {
                    // Do not mix liquids yet.
                } else {
                    val belowMass = belowCell.liquidMassKg()
                    val capacity = MAX_LIQUID_MASS_PER_CELL_KG - belowMass
                    if (capacity > 0.0) {
                        val transfer = minOf(maxTransfer, nextMass, capacity)
                        if (transfer > 0.0) {
                            addDelta(deltas, coordinate, species, -transfer)
                            addDelta(deltas, below, species, transfer)
                            continue
                        }
                    }
                }
            }

            for (neighbor in lateralNeighbors(coordinate)) {
                val other = grid.getCellAtCoordinate(neighbor) ?: continue
                if (other.occupancyState() == OccupancyState.SOLID) {
                    continue
                }
                if (other.liquidId() != OniElements.LIQUID_NONE && other.liquidId() != species) {
                    continue
                }
                val otherMass = other.liquidMassKg()
                val diff = nextMass - otherMass
                if (diff <= 1.0) {
                    continue
                }
                val transfer = minOf(maxTransfer, diff * 0.25, MAX_LIQUID_MASS_PER_CELL_KG - otherMass)
                if (transfer <= 0.0) {
                    continue
                }
                addDelta(deltas, coordinate, species, -transfer)
                addDelta(deltas, neighbor, species, transfer)
            }

            cell.setLiquidState(species, nextMass)
        }

        for ((coordinate, speciesDelta) in deltas) {
            val cell = grid.getOrCreateCellAtCoordinate(coordinate)
            for ((species, delta) in speciesDelta) {
                if (delta == 0.0) {
                    continue
                }
                val currentSpecies = cell.liquidId()
                if (currentSpecies == OniElements.LIQUID_NONE && delta > 0.0) {
                    cell.setLiquidState(species, delta)
                } else if (currentSpecies == species) {
                    cell.setLiquidState(species, cell.liquidMassKg() + delta)
                }
            }
        }
    }

    companion object {
        private const val MAX_LIQUID_MASS_PER_CELL_KG = 4000.0
        private const val BOIL_OFF_KG_PER_STEP = 1.0
    }

    private fun lateralNeighbors(coordinate: OniCellCoordinate): List<OniCellCoordinate> {
        return listOf(
            OniCellCoordinate(coordinate.cellX() + 1, coordinate.cellY(), coordinate.cellZ()),
            OniCellCoordinate(coordinate.cellX() - 1, coordinate.cellY(), coordinate.cellZ()),
            OniCellCoordinate(coordinate.cellX(), coordinate.cellY(), coordinate.cellZ() + 1),
            OniCellCoordinate(coordinate.cellX(), coordinate.cellY(), coordinate.cellZ() - 1),
        )
    }

    private fun addDelta(
        deltas: MutableMap<OniCellCoordinate, MutableMap<String, Double>>,
        coordinate: OniCellCoordinate,
        species: String,
        delta: Double,
    ) {
        val map = deltas.computeIfAbsent(coordinate) { HashMap() }
        map[species] = (map[species] ?: 0.0) + delta
    }
}
