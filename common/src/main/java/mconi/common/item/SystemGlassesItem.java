package mconi.common.item;

import mconi.common.sim.OniServices;
import mconi.common.sim.OniSystemInspector;
import mconi.common.sim.model.LayerProperty;
import mconi.common.sim.model.OniCellState;
import mconi.common.sim.model.SystemLens;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SystemGlassesItem extends Item
{
	private final SystemLens systemLens;

	public SystemGlassesItem(Properties properties, SystemLens systemLens)
	{
		super(properties);
		this.systemLens = systemLens;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
	{
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide())
		{
			BlockPos pos = player.blockPosition();
			OniCellState cell = OniServices.simulationRuntime().grid().getOrCreateCellAtBlock(
					pos.getX(),
					pos.getY(),
					pos.getZ(),
					OniServices.simulationRuntime().config().cellSize());
			player.displayClientMessage(
					Component.literal("System glasses [" + this.systemLens.name() + "] at (" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + "):"),
					false);
			for (LayerProperty property : OniSystemInspector.inspect(OniServices.simulationRuntime(), this.systemLens, cell))
			{
				player.displayClientMessage(
						Component.literal("[" + property.layer() + "] " + property.key() + "=" + property.value()),
						false);
			}
		}
		return InteractionResultHolder.success(stack);
	}
}
