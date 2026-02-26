package mconi.common.sim

import mconi.common.sim.model.OniCellCoordinate
import mconi.common.sim.model.OniCellState
import java.util.concurrent.ConcurrentHashMap

/**
 * Active-cell storage for the simulation kernel.
 */
class OniSimulationGrid {
    private val cells: MutableMap<OniCellCoordinate, OniCellState> = ConcurrentHashMap()

    fun getOrCreateCellAtBlock(x: Int, y: Int, z: Int, cellSize: Int): OniCellState {
        val coordinate = OniCellCoordinate.fromBlockPosition(x, y, z, cellSize)
        return cells.computeIfAbsent(coordinate) { OniCellState() }
    }

    fun getOrCreateCellAtCoordinate(coordinate: OniCellCoordinate): OniCellState {
        return cells.computeIfAbsent(coordinate) { OniCellState() }
    }

    fun getCellAtCoordinate(coordinate: OniCellCoordinate): OniCellState? = cells[coordinate]

    fun cellEntries(): Set<Map.Entry<OniCellCoordinate, OniCellState>> = cells.entries

    fun activeCellCount(): Int = cells.size

    fun cells(): Collection<OniCellState> = cells.values

    fun clear() {
        cells.clear()
    }
}
