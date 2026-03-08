package conservecraft.common.menu

import conservecraft.common.block.OniBlockFactory
import conservecraft.common.block.OniBlockLookup
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.CraftingMenu

class AdvancedCraftingTableMenu(
    containerId: Int,
    inventory: Inventory,
    private val access: ContainerLevelAccess = ContainerLevelAccess.NULL,
) : CraftingMenu(containerId, inventory, access) {
    override fun stillValid(player: Player): Boolean {
        val block = OniBlockLookup.block(OniBlockFactory.ADVANCED_CRAFTING_TABLE)
        return stillValid(access, player, block)
    }
}
