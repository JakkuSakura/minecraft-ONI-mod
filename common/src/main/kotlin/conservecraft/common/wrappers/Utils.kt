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

package conservecraft.common.wrappers

import com.mojang.brigadier.context.CommandContext
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style

/**
 * @author Leander Knuttel
 * @version 22.05.2024
 */
object Utils {
    @JvmStatic
    fun sendToClientChat(text: Component) {
        Minecraft.getInstance().gui.chat.addMessage(text)
    }

    @JvmStatic
    fun sendToClientChat(text: String) {
        sendToClientChat(Text.literal(text))
    }

    @JvmStatic
    fun sendErrorToClientChat(text: Component) {
        Minecraft.getInstance().gui.chat.addMessage(text.copy().withStyle(Style.EMPTY.withColor(ChatFormatting.RED)))
    }

    @JvmStatic
    fun sendErrorToClientChat(text: String) {
        sendErrorToClientChat(Text.literal(text))
    }

    @JvmStatic
    fun SendFeedback(context: CommandContext<CommandSourceStack>, text: Component, allowLogging: Boolean) {
        context.source.sendSuccess({ text }, allowLogging)
    }

    @JvmStatic
    fun SendFeedback(context: CommandContext<CommandSourceStack>, text: String, allowLogging: Boolean) {
        SendFeedback(context, Text.literal(text), allowLogging)
    }

    @JvmStatic
    fun SendError(context: CommandContext<CommandSourceStack>, text: Component, allowLogging: Boolean) {
        context.source.sendSuccess({ text.copy().withStyle(Style.EMPTY.withColor(ChatFormatting.RED)) }, allowLogging)
    }

    @JvmStatic
    fun SendError(context: CommandContext<CommandSourceStack>, text: String, allowLogging: Boolean) {
        SendError(context, Text.literal(text), allowLogging)
    }
}
