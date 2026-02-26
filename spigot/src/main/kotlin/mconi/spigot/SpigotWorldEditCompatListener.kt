package mconi.spigot

import mconi.common.sim.OniServices
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.server.ServerCommandEvent
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask

/**
 * WorldEdit compatibility guard:
 * pauses simulation briefly when bulk-edit commands are issued.
 */
class SpigotWorldEditCompatListener : Listener {
    private var resumeTask: BukkitTask? = null

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onPlayerCommand(event: PlayerCommandPreprocessEvent) {
        handleCommand(event.message)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onServerCommand(event: ServerCommandEvent) {
        handleCommand(event.command)
    }

    private fun handleCommand(rawCommand: String?) {
        if (!isWorldEditCommand(rawCommand)) {
            return
        }

        OniServices.simulationRuntime().setRunning(false)
        Bukkit.getLogger().info("Detected WorldEdit command, pausing ONI simulation for compatibility.")

        resumeTask?.cancel()

        val plugin: Plugin = SpigotPlugin.getInstance() ?: return
        resumeTask = Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            OniServices.simulationRuntime().setRunning(true)
            Bukkit.getLogger().info("Resumed ONI simulation after WorldEdit compatibility pause.")
        }, RESUME_DELAY_TICKS)
    }

    private fun isWorldEditCommand(rawCommand: String?): Boolean {
        val command = rawCommand?.trim()?.lowercase() ?: return false
        return command.startsWith("//")
                || command.startsWith("/worldedit")
                || command.startsWith("worldedit")
                || command.startsWith("/we ")
                || command == "/we"
                || command.startsWith("we ")
    }

    companion object {
        private const val RESUME_DELAY_TICKS = 40L
    }
}
