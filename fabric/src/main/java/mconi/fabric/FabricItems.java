package mconi.fabric;

import mconi.common.AbstractModInitializer;
import mconi.common.item.OniItems;
import mconi.common.sim.model.SystemLens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
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
			Identifier id = id(path);
			ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
			Item item = OniItems.createGlassesItem(lens, new Item.Properties().setId(key).stacksTo(1));
			Registry.register(BuiltInRegistries.ITEM, id, item);
		}
	}

	private static Identifier id(String path)
	{
		Identifier location = Identifier.tryParse(AbstractModInitializer.MOD_ID + ":" + path);
		if (location == null)
		{
			throw new IllegalArgumentException("Invalid item id path: " + path);
		}
		return location;
	}
}
