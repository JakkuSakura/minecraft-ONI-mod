package mconi.neoforge;

import mconi.common.AbstractModInitializer;
import mconi.common.item.OniItems;
import mconi.common.sim.model.SystemLens;
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
			ITEMS.register(path, () -> OniItems.createGlassesItem(lens));
		}
	}

	private NeoforgeItems() { }

	public static void register(IEventBus eventBus)
	{
		ITEMS.register(eventBus);
	}
}
