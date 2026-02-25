package mconi.common.sim;

/**
 * Runtime configuration for the ONI simulation kernel.
 * Values are currently in-memory and intentionally conservative.
 */
public class OniSimulationConfig
{
	private int tickInterval;
	private int cellSize;
	private int worldMinX;
	private int worldMaxX;
	private int worldMinZ;
	private int worldMaxZ;
	private int voidBandHeight;
	private int lavaBandHeight;

	public OniSimulationConfig()
	{
		this.tickInterval = 10;
		this.cellSize = 4;
		this.worldMinX = -1024;
		this.worldMaxX = 1024;
		this.worldMinZ = -1024;
		this.worldMaxZ = 1024;
		this.voidBandHeight = 32;
		this.lavaBandHeight = 32;
	}

	public int tickInterval() { return tickInterval; }
	public int cellSize() { return cellSize; }
	public int worldMinX() { return worldMinX; }
	public int worldMaxX() { return worldMaxX; }
	public int worldMinZ() { return worldMinZ; }
	public int worldMaxZ() { return worldMaxZ; }
	public int voidBandHeight() { return voidBandHeight; }
	public int lavaBandHeight() { return lavaBandHeight; }

	public void setTickInterval(int tickInterval)
	{
		if (tickInterval < 1) { throw new IllegalArgumentException("tickInterval must be >= 1"); }
		this.tickInterval = tickInterval;
	}

	public void setCellSize(int cellSize)
	{
		if (cellSize < 1) { throw new IllegalArgumentException("cellSize must be >= 1"); }
		this.cellSize = cellSize;
	}
}
