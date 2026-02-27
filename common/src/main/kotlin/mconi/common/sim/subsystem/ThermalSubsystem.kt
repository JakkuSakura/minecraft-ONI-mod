package mconi.common.sim.subsystem

import mconi.common.element.OniElements
import mconi.common.sim.model.OccupancyState
import mconi.common.sim.model.OniCellCoordinate

class ThermalSubsystem : SimulationSubsystem {
    override fun id(): String = "thermal"

    override fun run(context: SimulationContext) {
        val grid = context.grid()
        val deltas: MutableMap<OniCellCoordinate, Double> = HashMap()
        for ((coordinate, cell) in grid.cellEntries()) {
            val temp = cell.temperatureK()
            val occupancy = cell.occupancyState()
            var next = temp

            if (occupancy == OccupancyState.VACUUM || occupancy == OccupancyState.VOID) {
                next += (260.0 - temp) * 0.02
            }

            if (cell.liquidId() == OniElements.LIQUID_LAVA && cell.liquidMassKg() > 0.0) {
                next += 0.8
            }

            val conduction = when (occupancy) {
                OccupancyState.LIQUID -> 0.08
                OccupancyState.GAS -> 0.04
                OccupancyState.SOLID -> 0.02
                OccupancyState.VACUUM -> 0.01
                OccupancyState.VOID -> 0.0
            }

            if (conduction > 0.0) {
                for (neighbor in neighborsOf(coordinate)) {
                    val other = grid.getCellAtCoordinate(neighbor) ?: continue
                    val delta = (other.temperatureK() - temp) * conduction
                    deltas[coordinate] = (deltas[coordinate] ?: 0.0) + delta
                }
            }

            cell.setTemperatureK(next)
        }

        for ((coordinate, delta) in deltas) {
            val cell = grid.getOrCreateCellAtCoordinate(coordinate)
            cell.setTemperatureK(cell.temperatureK() + delta)
        }

        for (cell in grid.cells()) {
            val overheated = cell.temperatureK() >= OVERHEAT_THRESHOLD_K && cell.occupancyState() == OccupancyState.SOLID
            cell.setOverheated(overheated)
        }
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

    companion object {
        private const val OVERHEAT_THRESHOLD_K = 450.0
    }
}
