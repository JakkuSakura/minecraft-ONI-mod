package mconi.common.sim;

/**
 * Query helpers for constrained-world topology rules.
 */
public final class OniWorldFoundation
{
	private OniWorldFoundation() { }

	public static boolean isWithinHorizontalBounds(int x, int z, OniSimulationConfig config)
	{
		return x >= config.worldMinX() && x <= config.worldMaxX()
				&& z >= config.worldMinZ() && z <= config.worldMaxZ();
	}

	public static boolean isVoidBand(int y, int maxY, OniSimulationConfig config)
	{
		return y >= (maxY - config.voidBandHeight() + 1);
	}

	public static boolean isLavaBand(int y, int minY, OniSimulationConfig config)
	{
		return y <= (minY + config.lavaBandHeight() - 1);
	}
}
