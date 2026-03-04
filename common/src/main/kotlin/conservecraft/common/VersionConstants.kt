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

package conservecraft.common

import net.minecraft.SharedConstants

/**
 * has constants for MC versions
 *
 * @author James Seibel
 * @author Leander Knuttel
 * @version 17.05.2024
 */
class VersionConstants private constructor() {
    fun getMinecraftVersion(): String {
        return SharedConstants.getCurrentVersion().id()
    }

    companion object {
        @JvmField
        val INSTANCE: VersionConstants = VersionConstants()
    }
}
