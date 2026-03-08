package conservecraft.common.block

import conservecraft.common.element.ElementContents
import conservecraft.common.element.OniElements
import conservecraft.common.item.OniItemFactory
import conservecraft.common.item.OniItemThermal
import conservecraft.common.item.OniItemMass
import conservecraft.common.item.OniSolidItems
import conservecraft.common.world.OniVanillaElementBindings
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Container
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Block
import java.util.Locale

object RecyclingTableLogic {
    data class RecycledElement(
        val elementId: String,
        val mass: Double,
        val temperatureK: Double,
    )

    private val vanillaItemElements: Map<Item, String> = mapOf(
        Items.IRON_INGOT to "iron",
        Items.IRON_NUGGET to "iron",
        Items.RAW_IRON to "iron_ore",
        Items.COPPER_INGOT to "copper",
        Items.RAW_COPPER to "copper_ore",
        Items.GOLD_INGOT to "gold",
        Items.GOLD_NUGGET to "gold",
        Items.RAW_GOLD to "gold_amalgam",
        Items.COAL to "carbon",
        Items.CHARCOAL to "carbon",
        Items.DIAMOND to "diamond",
        Items.CLAY_BALL to "clay",
        Items.BRICK to "clay",
        Items.QUARTZ to "sedimentary_rock",
        Items.FLINT to "regolith",
        Items.STICK to "building_wood",
        Items.BOWL to "building_wood",
        Items.STRING to "plant_fiber",
        Items.PAPER to "plant_fiber"
    )

    fun recycleHeldItem(level: ServerLevel, player: Player, blockPos: net.minecraft.core.BlockPos, stack: ItemStack): Boolean {
        val input = extractSingleInput(stack)
        val outputs = outputsFor(input)
        if (outputs.isEmpty()) {
            return false
        }

        consumeSingleInput(stack)
        val resultStacks = outputs.flatMap(::toItemStacks)
        for (result in resultStacks) {
            var remaining = OniItemMass.mergeIntoContainer(player.inventory, result)
            if (!remaining.isEmpty) {
                remaining = mergeIntoAdjacentContainers(level, blockPos, remaining)
            }
            if (!remaining.isEmpty) {
                level.addFreshEntity(ItemEntity(level, blockPos.x + 0.5, blockPos.y + 1.0, blockPos.z + 0.5, remaining))
            }
        }
        return true
    }

    private fun extractSingleInput(stack: ItemStack): ItemStack {
        val single = ItemStack(stack.item, 1)
        val hasMassTag = hasMassTag(stack)
        if (hasMassTag) {
            OniItemMass.setStackMass(single, OniItemMass.stackMass(stack))
            OniItemThermal.setTemperatureK(single, OniItemThermal.temperatureK(stack))
        }
        return single
    }

    private fun consumeSingleInput(stack: ItemStack) {
        if (hasMassTag(stack)) {
            stack.count = 0
            return
        }
        stack.shrink(1)
    }

    fun outputStacksFor(stack: ItemStack): List<ItemStack> {
        return outputsFor(stack).flatMap(::toItemStacks)
    }

    fun outputsFor(stack: ItemStack): List<RecycledElement> {
        if (stack.isEmpty) {
            return emptyList()
        }

        val blockOutputs = blockOutputs(stack)
        if (blockOutputs.isNotEmpty()) {
            return blockOutputs
        }

        val itemId = BuiltInRegistries.ITEM.getKey(stack.item)?.toString() ?: return emptyList()
        val directElementId = OniSolidItems.elementIdOf(stack.item) ?: OniElements.elementIdForItemId(itemId)
        if (directElementId != null) {
            return listOf(recycledElement(directElementId, stack))
        }

        val mappedElementId = vanillaItemElements[stack.item] ?: heuristicItemElementId(stack.item)
        if (mappedElementId != null) {
            return listOf(recycledElement(mappedElementId, stack))
        }

        return emptyList()
    }

    private fun recycledElement(elementId: String, stack: ItemStack): RecycledElement {
        return RecycledElement(elementId, itemMass(stack), OniItemThermal.temperatureK(stack))
    }

    private fun itemMass(stack: ItemStack): Double {
        return OniItemMass.stackMass(stack)
    }

    private fun hasMassTag(stack: ItemStack): Boolean {
        if (stack.count != 1) {
            return false
        }
        return OniItemThermal.hasMassTag(stack)
    }

    private fun blockOutputs(stack: ItemStack): List<RecycledElement> {
        val block = (stack.item as? BlockItem)?.block ?: return emptyList()
        val oniDefaults = OniBlockFactory.defaultElements(block)
        if (oniDefaults.isNotEmpty()) {
            return scaleDefaults(oniDefaults, stack.count)
        }
        val vanillaDefaults = OniVanillaElementBindings.defaultsFor(block.defaultBlockState()) ?: heuristicDefaults(block)
        return scaleDefaults(vanillaDefaults, stack.count)
    }

    private fun heuristicItemElementId(item: Item): String? {
        val path = BuiltInRegistries.ITEM.getKey(item)?.path?.lowercase(Locale.ROOT) ?: return null
        val exact = heuristicItemExact(path)
        if (exact != null) {
            return exact
        }
        return when {
            path.startsWith("iron_") || path.startsWith("chainmail_") || path == "bucket" || path == "shears" || path == "flint_and_steel" || path.startsWith("minecart") -> "iron"
            path.startsWith("golden_") -> "gold"
            path.startsWith("copper_") -> "copper"
            path.endsWith("_boat") || path.endsWith("_chest_boat") || path.endsWith("_raft") || path.endsWith("_chest_raft") -> "building_wood"
            path.endsWith("_sign") || path.endsWith("_hanging_sign") -> "building_wood"
            path.endsWith("_door") || path.endsWith("_trapdoor") -> "building_wood"
            path.endsWith("_sword") || path.endsWith("_pickaxe") || path.endsWith("_axe") || path.endsWith("_shovel") || path.endsWith("_hoe") -> toolElementId(path)
            path.endsWith("_helmet") || path.endsWith("_chestplate") || path.endsWith("_leggings") || path.endsWith("_boots") || path.endsWith("_horse_armor") -> armorElementId(path)
            path.endsWith("_planks") || path == "crafting_table" -> "building_wood"
            path.endsWith("_log") || path.endsWith("_wood") || path.endsWith("_stem") || path.endsWith("_hyphae") -> "wood_log"
            path == "cobblestone" || path == "cobbled_deepslate" || path == "stone" || path == "blackstone" || path == "basalt" || path == "andesite" || path == "diorite" || path == "tuff" || path == "netherrack" -> "igneous_rock"
            path == "granite" -> "granite"
            path == "end_stone" -> "sedimentary_rock"
            path == "sand" || path == "red_sand" -> "sand"
            path == "gravel" -> "regolith"
            path == "dirt" || path == "grass_block" || path == "coarse_dirt" || path == "podzol" || path == "rooted_dirt" || path == "mycelium" -> "dirt"
            else -> null
        }
    }

    private fun heuristicItemExact(path: String): String? {
        return when (path) {
            "raw_iron" -> "iron_ore"
            "raw_copper" -> "copper_ore"
            "raw_gold" -> "gold_amalgam"
            "bone", "bone_meal" -> "lime"
            "blaze_rod" -> "refined_carbon"
            "clay_ball", "brick" -> "clay"
            "coal", "charcoal" -> "carbon"
            "diamond" -> "diamond"
            "emerald" -> "sedimentary_rock"
            "quartz", "nether_quartz" -> "sedimentary_rock"
            "flint" -> "regolith"
            "stick", "bowl", "ladder" -> "building_wood"
            "paper", "string" -> "plant_fiber"
            else -> null
        }
    }

    private fun toolElementId(path: String): String? {
        return when {
            path.startsWith("iron_") -> "iron"
            path.startsWith("golden_") -> "gold"
            path.startsWith("diamond_") -> "diamond"
            path.startsWith("stone_") -> "igneous_rock"
            path.startsWith("wooden_") -> "building_wood"
            else -> null
        }
    }

    private fun armorElementId(path: String): String? {
        return when {
            path.startsWith("iron_") || path.startsWith("chainmail_") -> "iron"
            path.startsWith("golden_") -> "gold"
            path.startsWith("diamond_") -> "diamond"
            path.startsWith("leather_") -> null
            else -> null
        }
    }

    private fun heuristicDefaults(block: Block): List<ElementContents> {
        val path = BuiltInRegistries.BLOCK.getKey(block)?.path?.lowercase(Locale.ROOT) ?: return emptyList()
        val elementId = when {
            path.endsWith("_planks") || path == "crafting_table" -> "building_wood"
            path.endsWith("_log") || path.endsWith("_wood") || path.endsWith("_stem") || path.endsWith("_hyphae") -> "wood_log"
            else -> return emptyList()
        }
        return listOf(ElementContents(elementId, 1000.0, OniItemThermal.DEFAULT_TEMP_K))
    }

    private fun scaleDefaults(defaults: List<ElementContents>?, multiplier: Int): List<RecycledElement> {
        if (defaults.isNullOrEmpty() || multiplier <= 0) {
            return emptyList()
        }
        return defaults.map { default ->
            RecycledElement(default.elementId, default.mass * multiplier.toDouble(), default.temperatureK)
        }
    }

    private fun toItemStacks(recycled: RecycledElement): List<ItemStack> {
        return OniSolidItems.encode(recycled.elementId, recycled.mass, recycled.temperatureK)
    }

    private fun mergeIntoAdjacentContainers(level: ServerLevel, pos: net.minecraft.core.BlockPos, stack: ItemStack): ItemStack {
        var remaining = stack
        for (dir in net.minecraft.core.Direction.values()) {
            if (remaining.isEmpty) {
                break
            }
            val neighborPos = pos.offset(dir.stepX, dir.stepY, dir.stepZ)
            val container = level.getBlockEntity(neighborPos) as? Container ?: continue
            remaining = OniItemMass.mergeIntoContainer(container, remaining)
        }
        return remaining
    }

}
