package mconi.spigot;

import mconi.common.AbstractModInitializer;
import mconi.common.sim.OniServices;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * WorldEdit compatibility guard:
 * pauses simulation briefly when bulk-edit commands are issued.
 */
public class SpigotWorldEditCompatListener implements Listener
{
	private static final int RESUME_DELAY_TICKS = 40;
	private BukkitTask resumeTask;

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event)
	{
		handleCommand(event.getMessage());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onServerCommand(ServerCommandEvent event)
	{
		handleCommand(event.getCommand());
	}

	private void handleCommand(String rawCommand)
	{
		if (!isWorldEditCommand(rawCommand))
		{
			return;
		}

		OniServices.simulationRuntime().setRunning(false);
		AbstractModInitializer.LOGGER.info("Detected WorldEdit command, pausing ONI simulation for compatibility.");

		if (resumeTask != null)
		{
			resumeTask.cancel();
		}

		Plugin plugin = SpigotPlugin.getInstance();
		if (plugin == null)
		{
			return;
		}

		resumeTask = Bukkit.getScheduler().runTaskLater(plugin, () ->
		{
			OniServices.simulationRuntime().setRunning(true);
			AbstractModInitializer.LOGGER.info("Resumed ONI simulation after WorldEdit compatibility pause.");
		}, RESUME_DELAY_TICKS);
	}

	private boolean isWorldEditCommand(String rawCommand)
	{
		if (rawCommand == null)
		{
			return false;
		}

		String command = rawCommand.trim().toLowerCase();
		return command.startsWith("//")
				|| command.startsWith("/worldedit")
				|| command.startsWith("worldedit")
				|| command.startsWith("/we ")
				|| command.equals("/we")
				|| command.startsWith("we ");
	}
}
