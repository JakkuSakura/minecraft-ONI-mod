package mconi.spigot

import mconi.common.AbstractModInitializer
import org.bukkit.Bukkit
import org.bukkit.plugin.PluginManager

/**
 * Spigot server event proxy.
 */
class SpigotServerProxy : AbstractModInitializer.IEventProxy {
    override fun registerEvents() {
        val pluginManager: PluginManager = Bukkit.getPluginManager()
        val plugin = SpigotPlugin.getInstance() ?: return
        if (pluginManager.getPlugin("WorldEdit") != null) {
            pluginManager.registerEvents(SpigotWorldEditCompatListener(), plugin)
            Bukkit.getLogger().info("Registered WorldEdit compatibility listener for ONI simulation.")
        }
    }
}
