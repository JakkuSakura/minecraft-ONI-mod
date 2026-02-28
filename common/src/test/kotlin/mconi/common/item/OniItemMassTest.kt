package mconi.common.item

import mconi.common.TestMinecraftBootstrap
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OniItemMassTest {
    @BeforeAll
    fun bootstrapMinecraft() {
        TestMinecraftBootstrap.ensureBootstrapped()
    }

    @Test
    fun usesDefaultWeightWhenMissingTag() {
        val stack = ItemStack(Items.APPLE, 3)
        val weight = OniItemMass.stackWeightKg(stack)
        assertEquals(3.0, weight, 1e-6)
    }

    @Test
    fun usesWeightTagWhenPresent() {
        val stack = ItemStack(Items.APPLE, 1)
        OniItemMass.setStackWeightKg(stack, 7.5)
        val weight = OniItemMass.stackWeightKg(stack)
        assertEquals(7.5, weight, 1e-6)
    }

    @Test
    fun takeWeightReducesTaggedStack() {
        val stack = ItemStack(Items.APPLE, 1)
        OniItemMass.setStackWeightKg(stack, 5.0)
        val taken = OniItemMass.takeWeightKg(stack, 2.0)
        assertEquals(2.0, taken, 1e-6)
        assertEquals(3.0, OniItemMass.stackWeightKg(stack), 1e-6)
        assertEquals(1, stack.count)
    }

    @Test
    fun mergeIntoContainerHonorsCapacity() {
        val container = SimpleContainer(1)
        val existing = ItemStack(Items.APPLE, 1)
        OniItemMass.setStackWeightKg(existing, 60.0)
        container.setItem(0, existing)

        val incoming = ItemStack(Items.APPLE, 1)
        OniItemMass.setStackWeightKg(incoming, 10.0)

        val remainder = OniItemMass.mergeIntoContainer(container, incoming)
        assertEquals(64.0, OniItemMass.stackWeightKg(container.getItem(0)), 1e-6)
        assertTrue(OniItemMass.stackWeightKg(remainder) >= 6.0 - 1e-9)
    }
}
