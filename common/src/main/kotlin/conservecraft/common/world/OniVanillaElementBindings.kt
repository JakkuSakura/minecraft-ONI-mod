package conservecraft.common.world

import conservecraft.common.element.ElementContents
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState

object OniVanillaElementBindings {
    private const val DEFAULT_SOLID_MASS = 1000.0
    private const val DEFAULT_SOLID_TEMP_K = 293.15

    private val bindings: Map<Block, List<ElementContents>> = buildBindings()

    fun defaultsFor(state: BlockState): List<ElementContents>? = bindings[state.block]?.map { it.copy() }

    fun isMapped(block: Block): Boolean = bindings.containsKey(block)

    private fun buildBindings(): Map<Block, List<ElementContents>> {
        val map = LinkedHashMap<Block, List<ElementContents>>()
        fun bind(block: Block, elementId: String, mass: Double = DEFAULT_SOLID_MASS, tempK: Double = DEFAULT_SOLID_TEMP_K) {
            map[block] = listOf(ElementContents(elementId, mass, tempK))
        }

        // Dirt and common soil blocks.
        bind(Blocks.DIRT, "dirt")
        bind(Blocks.GRASS_BLOCK, "dirt")
        bind(Blocks.COARSE_DIRT, "dirt")
        bind(Blocks.PODZOL, "dirt")
        bind(Blocks.MYCELIUM, "dirt")
        bind(Blocks.ROOTED_DIRT, "dirt")

        // Sand and loose blocks.
        bind(Blocks.SAND, "sand")
        bind(Blocks.RED_SAND, "sand")
        bind(Blocks.GRAVEL, "regolith")

        // Stone families.
        bind(Blocks.STONE, "igneous_rock")
        bind(Blocks.COBBLESTONE, "igneous_rock")
        bind(Blocks.DEEPSLATE, "igneous_rock")
        bind(Blocks.COBBLED_DEEPSLATE, "igneous_rock")
        bind(Blocks.ANDESITE, "igneous_rock")
        bind(Blocks.DIORITE, "igneous_rock")
        bind(Blocks.BASALT, "igneous_rock")
        bind(Blocks.BLACKSTONE, "igneous_rock")
        bind(Blocks.GRANITE, "granite")
        bind(Blocks.NETHERRACK, "igneous_rock")
        bind(Blocks.END_STONE, "sedimentary_rock")
        bind(Blocks.TUFF, "igneous_rock")

        // Clay and similar.
        bind(Blocks.CLAY, "clay")

        // Simple ores mapped to closest ONI solids.
        bind(Blocks.COAL_ORE, "carbon")
        bind(Blocks.DEEPSLATE_COAL_ORE, "carbon")
        bind(Blocks.IRON_ORE, "iron_ore")
        bind(Blocks.DEEPSLATE_IRON_ORE, "iron_ore")
        bind(Blocks.COPPER_ORE, "copper_ore")
        bind(Blocks.DEEPSLATE_COPPER_ORE, "copper_ore")
        bind(Blocks.GOLD_ORE, "gold_amalgam")
        bind(Blocks.DEEPSLATE_GOLD_ORE, "gold_amalgam")

        return map
    }
}
