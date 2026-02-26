/*
 *    This file is part of the minecraft-ONI-mod
 *    licensed under the GNU GPL v3 License.
 *    (some parts of this file are originally from the Distant Horizons mod by James Seibel)
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

package mconi.common

import java.io.File

/**
 * Checks if a mod is loaded
 *
 * @author coolGi
 * @author Leander Knuttel
 * @version 27.05.2024
 */
abstract class ModChecker {
    init {
        INSTANCE = this
    }

    /**
     * Checks if a mod is loaded
     */
    abstract fun isModLoaded(modid: String): Boolean

    abstract fun modLocation(modid: String): File

    fun classExists(className: String): Boolean {
        return try {
            Class.forName(className)
            true
        } catch (_: ClassNotFoundException) {
            false
        }
    }

    companion object {
        @JvmField
        var INSTANCE: ModChecker? = null
    }
}
