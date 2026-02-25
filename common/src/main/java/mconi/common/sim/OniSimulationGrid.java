package mconi.common.sim;

import mconi.common.sim.model.OniCellCoordinate;
import mconi.common.sim.model.OniCellState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Active-cell storage for the simulation kernel.
 */
public class OniSimulationGrid
{
	private final Map<OniCellCoordinate, OniCellState> cells = new ConcurrentHashMap<>();

	public OniCellState getOrCreateCellAtBlock(int x, int y, int z, int cellSize)
	{
		OniCellCoordinate coordinate = OniCellCoordinate.fromBlockPosition(x, y, z, cellSize);
		return cells.computeIfAbsent(coordinate, ignored -> new OniCellState());
	}

	public int activeCellCount()
	{
		return cells.size();
	}

	public void clear()
	{
		cells.clear();
	}
}
