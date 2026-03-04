/*
 *    This file is part of the minecraft-ONI-mod
 *    licensed under the GNU GPL v3 License.
 *
 *    Copyright (C) 2024  Leander Knuttel
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

package conservecraft.common

/**
 * @author Leander Knuttel
 * @version 26.05.2024
 */
enum class LoaderType {
    Fabric {
        override fun isFabricLike(): Boolean = true
        override fun isForgeLike(): Boolean = false
        override fun isBukkitFork(): Boolean = false
    },
    Quilt {
        override fun isFabricLike(): Boolean = true
        override fun isForgeLike(): Boolean = false
        override fun isBukkitFork(): Boolean = false
    },
    Forge {
        override fun isFabricLike(): Boolean = false
        override fun isForgeLike(): Boolean = true
        override fun isBukkitFork(): Boolean = false
    },
    NeoForge {
        override fun isFabricLike(): Boolean = false
        override fun isForgeLike(): Boolean = true
        override fun isBukkitFork(): Boolean = false
    },
    Bukkit {
        override fun isFabricLike(): Boolean = false
        override fun isForgeLike(): Boolean = false
        override fun isBukkitFork(): Boolean = true
    },
    Spigot {
        override fun isFabricLike(): Boolean = false
        override fun isForgeLike(): Boolean = false
        override fun isBukkitFork(): Boolean = true
    },
    Paper {
        override fun isFabricLike(): Boolean = false
        override fun isForgeLike(): Boolean = false
        override fun isBukkitFork(): Boolean = true
    },
    Folia {
        override fun isFabricLike(): Boolean = false
        override fun isForgeLike(): Boolean = false
        override fun isBukkitFork(): Boolean = true
    },
    Sponge {
        override fun isFabricLike(): Boolean = false
        override fun isForgeLike(): Boolean = false
        override fun isBukkitFork(): Boolean = false
    },
    Purpur {
        override fun isFabricLike(): Boolean = false
        override fun isForgeLike(): Boolean = false
        override fun isBukkitFork(): Boolean = true
    };

    abstract fun isFabricLike(): Boolean
    abstract fun isForgeLike(): Boolean
    abstract fun isBukkitFork(): Boolean
}
