package mconi.common.item

import mconi.common.TestMinecraftBootstrap
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OniInventoryWeightTest {
    @BeforeAll
    fun bootstrapMinecraft() {
        TestMinecraftBootstrap.ensureBootstrapped()
    }

    @Test
    fun stackWeightUsesDefaultWeightForNonOniItems() {
        val stack = ItemStack(Items.APPLE, 10)
        val weight = OniInventoryWeight.stackWeight(stack)
        assertEquals(10.0, weight, 1e-6)
    }

    @Test
    fun capacityUsesSlotCount() {
        val container = SimpleContainer(2)
        val capacity = OniInventoryWeight.capacity(container)
        assertEquals(128.0, capacity, 1e-6)
    }
}
