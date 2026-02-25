package mconi.fabric;

import mconi.common.AbstractModInitializer;
import mconi.common.item.OniItems;
import mconi.common.sim.model.SystemLens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class FabricItems
{
	private static boolean registered;

	private FabricItems() { }

	public static void register()
	{
		if (registered)
		{
			return;
		}
		registered = true;

		for (SystemLens lens : SystemLens.values())
		{
			String path = OniItems.pathForLens(lens);
			Item item = OniItems.createGlassesItem(lens);
			Registry.register(BuiltInRegistries.ITEM, id(path), item);
		}
	}

	private static ResourceLocation id(String path)
	{
		ResourceLocation location = ResourceLocation.tryParse(AbstractModInitializer.MOD_ID + ":" + path);
		if (location == null)
		{
			throw new IllegalArgumentException("Invalid item id path: " + path);
		}
		return location;
	}
}
