package mconi.neoforge;

import mconi.common.AbstractModInitializer;
import mconi.common.item.OniItems;
import mconi.common.sim.model.SystemLens;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class NeoforgeItems
{
	private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AbstractModInitializer.MOD_ID);

	static
	{
		for (SystemLens lens : SystemLens.values())
		{
			String path = OniItems.pathForLens(lens);
			Identifier id = Identifier.tryParse(AbstractModInitializer.MOD_ID + ":" + path);
			if (id == null)
			{
				throw new IllegalArgumentException("Invalid item id path: " + path);
			}
			ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
			ITEMS.register(path, () -> OniItems.createGlassesItem(lens, new Item.Properties().setId(key).stacksTo(1)));
		}
	}

	private NeoforgeItems() { }

	public static void register(IEventBus eventBus)
	{
		ITEMS.register(eventBus);
	}
}
