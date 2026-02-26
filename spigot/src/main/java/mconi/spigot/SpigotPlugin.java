/*
 *    This file is part of the minecraft-ONI-mod
 *    licensed under the GNU GPL v3 License.

 *    Copyright (C) 2024  Leander Knüttel
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package mconi.spigot;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import mconi.spigot.world.OniChunkGenerator;

/**
 * @author Leander Knüttel
 * @version 26.05.2024
 */
public class SpigotPlugin extends JavaPlugin {
    private static SpigotPlugin instance;

    public static SpigotPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        SpigotMain bukkitMain = new SpigotMain();
        bukkitMain.onInitializeServer();
        Bukkit.getLogger().info(ChatColor.GREEN + "Enabled " + "Oxygen Not Included");
    }

    @Override
    public void onDisable() {
        instance = null;
        //TODO

        Bukkit.getLogger().info(ChatColor.RED + "Disabled " + "Oxygen Not Included");
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        if (id == null || id.isEmpty() || "oni".equalsIgnoreCase(id)) {
            return new OniChunkGenerator();
        }
        return null;
    }
}
