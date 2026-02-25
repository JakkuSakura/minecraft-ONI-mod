package mconi.common.sim.model;

import java.util.Objects;

public final class OniCellCoordinate
{
	private final int cellX;
	private final int cellY;
	private final int cellZ;

	public OniCellCoordinate(int cellX, int cellY, int cellZ)
	{
		this.cellX = cellX;
		this.cellY = cellY;
		this.cellZ = cellZ;
	}

	public static OniCellCoordinate fromBlockPosition(int x, int y, int z, int cellSize)
	{
		return new OniCellCoordinate(
				Math.floorDiv(x, cellSize),
				Math.floorDiv(y, cellSize),
				Math.floorDiv(z, cellSize));
	}

	public int cellX() { return cellX; }
	public int cellY() { return cellY; }
	public int cellZ() { return cellZ; }

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) { return true; }
		if (!(obj instanceof OniCellCoordinate other)) { return false; }
		return cellX == other.cellX && cellY == other.cellY && cellZ == other.cellZ;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(cellX, cellY, cellZ);
	}
}
