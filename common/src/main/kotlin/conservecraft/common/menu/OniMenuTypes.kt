package conservecraft.common.menu

import net.minecraft.world.inventory.MenuType

object OniMenuTypes {
    const val ADVANCED_CRAFTING_TABLE = "advanced_crafting_table"
    const val RECYCLING_TABLE = "recycling_table"

    private lateinit var advancedCraftingTableType: MenuType<AdvancedCraftingTableMenu>
    private lateinit var recyclingTableType: MenuType<RecyclingTableMenu>

    fun bindAdvancedCraftingTable(type: MenuType<AdvancedCraftingTableMenu>) {
        advancedCraftingTableType = type
    }

    fun bindRecyclingTable(type: MenuType<RecyclingTableMenu>) {
        recyclingTableType = type
    }

    fun advancedCraftingTable(): MenuType<AdvancedCraftingTableMenu> = advancedCraftingTableType

    fun recyclingTable(): MenuType<RecyclingTableMenu> = recyclingTableType
}
