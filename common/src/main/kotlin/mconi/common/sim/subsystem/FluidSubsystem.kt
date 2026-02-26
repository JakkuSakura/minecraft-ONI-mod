package mconi.common.sim.subsystem

import mconi.common.sim.model.FluidSpecies
import mconi.common.sim.model.OccupancyState
import mconi.common.sim.model.OniCellCoordinate

class FluidSubsystem : SimulationSubsystem {
    override fun id(): String = "fluid"

    override fun run(context: SimulationContext) {
        val grid = context.grid()
        val maxTransfer = context.config().fluidTransferKgPerStep().coerceAtLeast(0.0)
        val voidDrain = context.config().voidFluidDrainFraction().coerceIn(0.0, 1.0)

        val deltas: MutableMap<OniCellCoordinate, MutableMap<FluidSpecies, Double>> = HashMap()
        for ((coordinate, cell) in grid.cellEntries()) {
            if (cell.occupancyState() == OccupancyState.SOLID) {
                continue
            }

            val mass = cell.fluidMassKg()
            val species = cell.fluidSpecies()
            if (species == FluidSpecies.NONE || mass <= 0.0) {
                cell.setFluidState(FluidSpecies.NONE, 0.0)
                continue
            }

            if (cell.occupancyState() == OccupancyState.VOID) {
                val drained = mass * (1.0 - voidDrain)
                cell.setFluidState(species, drained.coerceAtLeast(0.0))
                continue
            }

            var nextMass = mass.coerceAtMost(MAX_FLUID_MASS_PER_CELL_KG)

            if ((species == FluidSpecies.WATER || species == FluidSpecies.POLLUTED_WATER) && cell.temperatureK() > 373.15) {
                nextMass -= BOIL_OFF_KG_PER_STEP
            }

            if (nextMass <= 0.0) {
                cell.setFluidState(FluidSpecies.NONE, 0.0)
                continue
            }

            val below = OniCellCoordinate(coordinate.cellX(), coordinate.cellY() - 1, coordinate.cellZ())
            val belowCell = grid.getCellAtCoordinate(below)
            if (belowCell != null && belowCell.occupancyState() != OccupancyState.SOLID) {
                if (belowCell.fluidSpecies() != FluidSpecies.NONE && belowCell.fluidSpecies() != species) {
                    // Do not mix fluids yet.
                } else {
                    val belowMass = belowCell.fluidMassKg()
                    val capacity = MAX_FLUID_MASS_PER_CELL_KG - belowMass
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
                if (other.fluidSpecies() != FluidSpecies.NONE && other.fluidSpecies() != species) {
                    continue
                }
                val otherMass = other.fluidMassKg()
                val diff = nextMass - otherMass
                if (diff <= 1.0) {
                    continue
                }
                val transfer = minOf(maxTransfer, diff * 0.25, MAX_FLUID_MASS_PER_CELL_KG - otherMass)
                if (transfer <= 0.0) {
                    continue
                }
                addDelta(deltas, coordinate, species, -transfer)
                addDelta(deltas, neighbor, species, transfer)
            }

            cell.setFluidState(species, nextMass)
        }

        for ((coordinate, speciesDelta) in deltas) {
            val cell = grid.getOrCreateCellAtCoordinate(coordinate)
            for ((species, delta) in speciesDelta) {
                if (delta == 0.0) {
                    continue
                }
                val currentSpecies = cell.fluidSpecies()
                if (currentSpecies == FluidSpecies.NONE && delta > 0.0) {
                    cell.setFluidState(species, delta)
                } else if (currentSpecies == species) {
                    cell.setFluidState(species, cell.fluidMassKg() + delta)
                }
            }
        }
    }

    companion object {
        private const val MAX_FLUID_MASS_PER_CELL_KG = 4000.0
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
        deltas: MutableMap<OniCellCoordinate, MutableMap<FluidSpecies, Double>>,
        coordinate: OniCellCoordinate,
        species: FluidSpecies,
        delta: Double,
    ) {
        val map = deltas.computeIfAbsent(coordinate) { HashMap() }
        map[species] = (map[species] ?: 0.0) + delta
    }
}
