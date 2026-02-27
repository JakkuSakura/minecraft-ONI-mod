package mconi.spigot

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.generator.ChunkGenerator
import org.bukkit.plugin.java.JavaPlugin
import mconi.spigot.world.OniChunkGenerator

/**
 * Bukkit plugin entry.
 */
class SpigotPlugin : JavaPlugin() {
    override fun onEnable() {
        instance = this
        val bukkitMain = SpigotMain()
        bukkitMain.onSetupServer()
        Bukkit.getLogger().info(ChatColor.GREEN.toString() + "Enabled Oxygen Not Included")
    }

    override fun onDisable() {
        instance = null
        Bukkit.getLogger().info(ChatColor.RED.toString() + "Disabled Oxygen Not Included")
    }

    override fun getDefaultWorldGenerator(worldName: String, id: String?): ChunkGenerator? {
        if (id.isNullOrEmpty() || id.equals("oni", ignoreCase = true)) {
            return OniChunkGenerator()
        }
        return null
    }

    companion object {
        private var instance: SpigotPlugin? = null

        @JvmStatic
        fun getInstance(): SpigotPlugin? = instance
    }
}
