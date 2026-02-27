package mconi.mixins.common.client

import mconi.common.sim.OniServices
import mconi.common.sim.model.OniCellCoordinate
import net.minecraft.client.Minecraft
import net.minecraft.client.DeltaTracker
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.resources.language.I18n
import net.minecraft.world.level.Level
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(Gui::class)
class HudOverlayMixin {
    @Inject(method = ["render"], at = [At("TAIL")])
    @Suppress("UNUSED_PARAMETER")
    private fun `mconi$renderOniHud`(guiGraphics: GuiGraphics, deltaTracker: DeltaTracker, ci: CallbackInfo) {
        val minecraft = Minecraft.getInstance()
        if (minecraft.options.hideGui) {
            return
        }
        val player = minecraft.player ?: return
        if (player.level().dimension() != Level.OVERWORLD) {
            return
        }
        val runtime = OniServices.simulationRuntime()
        val config = runtime.config()
        val pos = player.blockPosition()
        val cell = runtime.grid().getCellAtCoordinate(
            OniCellCoordinate.fromBlockPosition(pos.x, pos.y, pos.z, config.cellSize())
        )

        val font = minecraft.font
        var y = 6
        val x = 6
        val color = 0xE0E0E0
        val lineHeight = 10

        if (cell == null) {
            guiGraphics.drawString(font, "${I18n.get("hud.mconi.oxygen")}: --", x, y, color, false)
            return
        }

        val o2Pct = cell.o2Fraction() * 100.0
        val co2Pct = cell.co2Fraction() * 100.0
        val pressure = cell.pressureKpa()
        val tempC = cell.temperatureK() - 273.15
        val band = cell.breathingBand().name
        val stress = runtime.stressState().score(player)

        val lines = listOf(
            "${I18n.get("hud.mconi.oxygen")}: ${"%.1f".format(o2Pct)}%",
            "${I18n.get("hud.mconi.co2")}: ${"%.1f".format(co2Pct)}%",
            "${I18n.get("hud.mconi.pressure")}: ${"%.1f".format(pressure)} kPa",
            "${I18n.get("hud.mconi.temperature")}: ${"%.1f".format(tempC)} C",
            "${I18n.get("hud.mconi.breathing")}: $band",
            "${I18n.get("hud.mconi.stress")}: ${"%.2f".format(stress)}"
        )

        for (line in lines) {
            guiGraphics.drawString(font, line, x, y, color, false)
            y += lineHeight
        }
    }
}
