package conservecraft.common.world

import conservecraft.common.block.OniBlockFactory
import conservecraft.common.config.OniIntegrationConfig
import conservecraft.common.item.OniItemMass
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.item.Items

object OniVanillaDrops {
    fun buildDrop(level: ServerLevel, state: BlockState, pos: net.minecraft.core.BlockPos): ItemStack? {
        if (!OniIntegrationConfig.enableVanillaElements(level)) {
            return null
        }
        if (OniBlockFactory.idOf(state.block) != null) {
            return null
        }
        val defaults = OniVanillaElementBindings.defaultsFor(state) ?: return null
        val elements = OniElementAccess.elements(level, pos).ifEmpty { defaults }
        val totalMass = elements.sumOf { it.mass }
        if (totalMass <= 0.0) {
            return null
        }
        val item = state.block.asItem()
        if (item == Items.AIR) {
            return null
        }
        val stack = ItemStack(item, 1)
        OniItemMass.setStackMass(stack, totalMass)
        return stack
    }
}
