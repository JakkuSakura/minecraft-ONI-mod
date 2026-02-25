package mconi.forge;

import mconi.common.AbstractModInitializer;
import mconi.common.item.OniItems;
import mconi.common.sim.model.SystemLens;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class ForgeItems
{
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AbstractModInitializer.MOD_ID);

	static
	{
		for (SystemLens lens : SystemLens.values())
		{
			String path = OniItems.pathForLens(lens);
			ITEMS.register(path, () -> OniItems.createGlassesItem(lens));
		}
	}

	private ForgeItems() { }

	public static void register(IEventBus eventBus)
	{
		ITEMS.register(eventBus);
	}
}
