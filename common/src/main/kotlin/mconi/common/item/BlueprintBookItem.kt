package mconi.common.item

import mconi.common.menu.BlueprintBookMenu
import mconi.common.menu.OniMenuTypes
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import mconi.common.sim.OniConstructionSitePlacer

class BlueprintBookItem(properties: Item.Properties) : Item(properties) {
    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        }

        ensureBlueprintStack(player)
        player.openMenu(createMenuProvider())

        return InteractionResult.CONSUME
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player ?: return InteractionResult.PASS
        val level = context.level
        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        }
        val stack = ensureBlueprintStack(player)
        val selection = stack?.let { OniBlueprintSelectionNbt.readFrom(it) }
        if (selection == null) {
            player.openMenu(createMenuProvider())
            return InteractionResult.CONSUME
        }
        val placed = OniConstructionSitePlacer.place(level, context.clickedPos, context.clickedFace, selection, player)
        if (!placed) {
            player.displayClientMessage(Component.literal("Cannot place construction site here."), true)
        }
        return InteractionResult.CONSUME
    }

    private fun createMenuProvider(): MenuProvider {
        return SimpleMenuProvider(
            { id, inventory, _ -> BlueprintBookMenu(id, inventory, OniMenuTypes.BLUEPRINT_BOOK) },
            Component.translatable("screen.mconi.blueprint_book.title")
        )
    }

    private fun ensureBlueprintStack(player: Player): ItemStack? {
        val existing = findBlueprintStack(player)
        if (existing != null) {
            if (OniBlueprintSelectionNbt.readFrom(existing) == null) {
                val firstId = OniBlueprintRegistry.allIds().firstOrNull()
                val blueprint = firstId?.let { OniBlueprintRegistry.get(it) }
                if (blueprint != null) {
                    OniBlueprintSelectionNbt.writeTo(existing, OniBlueprintSelectionNbt.starterSelection(blueprint))
                }
            }
            return existing
        }

        val blueprintItem = OniItemFactory.itemById(OniItemFactory.BLUEPRINT) ?: return null
        val stack = ItemStack(blueprintItem)
        val firstId = OniBlueprintRegistry.allIds().firstOrNull()
        val blueprint = firstId?.let { OniBlueprintRegistry.get(it) }
        if (blueprint != null) {
            OniBlueprintSelectionNbt.writeTo(stack, OniBlueprintSelectionNbt.starterSelection(blueprint))
        }
        player.inventory.add(stack)
        return stack
    }

    private fun findBlueprintStack(player: Player): ItemStack? {
        val main = player.mainHandItem
        if (main.item is BlueprintItem) {
            return main
        }
        val off = player.offhandItem
        if (off.item is BlueprintItem) {
            return off
        }
        val inv = player.inventory
        for (i in 0 until inv.containerSize) {
            val stack = inv.getItem(i)
            if (stack.item is BlueprintItem) {
                return stack
            }
        }
        return null
    }
}
