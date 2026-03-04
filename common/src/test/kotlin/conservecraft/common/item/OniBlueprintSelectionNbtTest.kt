package conservecraft.common.item

import conservecraft.common.TestMinecraftBootstrap
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class OniBlueprintSelectionNbtTest {
    @BeforeTest
    fun bootstrapMinecraft() {
        TestMinecraftBootstrap.ensureBootstrapped()
    }

    @Test
    fun roundTripsSelection() {
        val blueprintId = OniBlueprintRegistry.allIds().firstOrNull()
        assertNotNull(blueprintId)
        val blueprint = OniBlueprintRegistry.get(blueprintId)
        assertNotNull(blueprint)

        val selection = OniBlueprintSelectionNbt.starterSelection(blueprint)
        val stack = ItemStack(Items.STONE)

        OniBlueprintSelectionNbt.writeTo(stack, selection)
        val read = OniBlueprintSelectionNbt.readFrom(stack)

        assertNotNull(read)
        assertEquals(selection.blueprintId, read.blueprintId)
        assertEquals(selection.materials, read.materials)
    }
}
