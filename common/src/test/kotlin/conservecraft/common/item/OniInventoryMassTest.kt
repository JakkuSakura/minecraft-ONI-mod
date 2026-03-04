package conservecraft.common.item

import conservecraft.common.TestMinecraftBootstrap
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OniInventoryMassTest {
    @BeforeAll
    fun bootstrapMinecraft() {
        TestMinecraftBootstrap.ensureBootstrapped()
    }

    @Test
    fun stackMassUsesDefaultMassForNonOniItems() {
        val stack = ItemStack(Items.APPLE, 10)
        val mass = OniInventoryMass.stackMass(stack)
        assertEquals(10.0, mass, 1e-6)
    }

    @Test
    fun capacityUsesSlotCount() {
        val container = SimpleContainer(2)
        val capacity = OniInventoryMass.capacity(container)
        assertEquals(128.0, capacity, 1e-6)
    }
}
