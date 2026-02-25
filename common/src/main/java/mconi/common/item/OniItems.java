package mconi.common.item;

import mconi.common.sim.model.SystemLens;
import net.minecraft.world.item.Item;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * Shared item definitions used by all loaders.
 */
public final class OniItems
{
	private static final EnumMap<SystemLens, String> GLASSES_ITEM_PATHS = new EnumMap<>(SystemLens.class);

	static
	{
		GLASSES_ITEM_PATHS.put(SystemLens.ATMOSPHERE, "atmosphere_glasses");
		GLASSES_ITEM_PATHS.put(SystemLens.FLUID, "fluid_glasses");
		GLASSES_ITEM_PATHS.put(SystemLens.THERMAL, "thermal_glasses");
		GLASSES_ITEM_PATHS.put(SystemLens.OXYGEN, "oxygen_glasses");
		GLASSES_ITEM_PATHS.put(SystemLens.POWER, "power_glasses");
		GLASSES_ITEM_PATHS.put(SystemLens.STRESS, "stress_glasses");
		GLASSES_ITEM_PATHS.put(SystemLens.RESEARCH, "research_glasses");
		GLASSES_ITEM_PATHS.put(SystemLens.CONSTRUCTION, "construction_glasses");
	}

	private OniItems() { }

	public static Map<SystemLens, String> glassesItemPaths()
	{
		return Collections.unmodifiableMap(GLASSES_ITEM_PATHS);
	}

	public static String pathForLens(SystemLens lens)
	{
		return GLASSES_ITEM_PATHS.get(lens);
	}

	public static Item createGlassesItem(SystemLens lens)
	{
		return createGlassesItem(lens, new Item.Properties().stacksTo(1));
	}

	public static Item createGlassesItem(SystemLens lens, Item.Properties properties)
	{
		return new SystemGlassesItem(properties, lens);
	}
}
