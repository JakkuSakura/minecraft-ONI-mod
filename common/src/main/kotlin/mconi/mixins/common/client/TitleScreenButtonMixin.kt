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

package mconi.mixins.common.client

import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.TitleScreen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.TranslatableContents
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(TitleScreen::class)
abstract class TitleScreenButtonMixin(title: Component) : Screen(title) {
    @Inject(method = ["init"], at = [At("TAIL")])
    @Suppress("UNUSED_PARAMETER")
    private fun removeCreateTestWorldButton(ci: CallbackInfo) {
        var target: AbstractWidget? = null

        for (child in children()) {
            if (child is AbstractWidget) {
                if (target == null && isCreateTestWorldLabel(child.message)) {
                    target = child
                }
            }
        }

        if (target == null) {
            return
        }

        target.visible = false
        target.active = false
    }

    private fun isCreateTestWorldLabel(component: Component?): Boolean {
        if (component == null) {
            return false
        }

        val contents = component.contents
        if (contents is TranslatableContents) {
            return contents.key == "menu.create_test_world"
        }

        return component.string == "Create Test World"
    }
}
