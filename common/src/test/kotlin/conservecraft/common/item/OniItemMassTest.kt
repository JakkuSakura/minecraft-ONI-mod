package conservecraft.common.item

import conservecraft.common.TestMinecraftBootstrap
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
    fun usesDefaultMassWhenMissingTag() {
        val stack = ItemStack(Items.APPLE, 3)
        val mass = OniItemMass.stackMass(stack)
        assertEquals(3.0, mass, 1e-6)
    }

    @Test
    fun usesMassTagWhenPresent() {
        val stack = ItemStack(Items.APPLE, 1)
        OniItemMass.setStackMass(stack, 7.5)
        val mass = OniItemMass.stackMass(stack)
        assertEquals(7.5, mass, 1e-6)
    }

    @Test
    fun takeMassReducesTaggedStack() {
        val stack = ItemStack(Items.APPLE, 1)
        OniItemMass.setStackMass(stack, 5.0)
        val taken = OniItemMass.takeMass(stack, 2.0)
        assertEquals(2.0, taken, 1e-6)
        assertEquals(3.0, OniItemMass.stackMass(stack), 1e-6)
        assertEquals(1, stack.count)
    }

    @Test
    fun mergeIntoContainerHonorsCapacity() {
        val container = SimpleContainer(1)
        val existing = ItemStack(Items.APPLE, 1)
        OniItemMass.setStackMass(existing, 60.0)
        container.setItem(0, existing)

        val incoming = ItemStack(Items.APPLE, 1)
        OniItemMass.setStackMass(incoming, 10.0)

        val remainder = OniItemMass.mergeIntoContainer(container, incoming)
        assertEquals(64.0, OniItemMass.stackMass(container.getItem(0)), 1e-6)
        assertTrue(OniItemMass.stackMass(remainder) >= 6.0 - 1e-9)
    }

    @Test
    fun mergeIntoEmptyContainerCreatesMassStack() {
        val container = SimpleContainer(1)
        val incoming = ItemStack(Items.APPLE, 1)
        OniItemMass.setStackMass(incoming, 10.0)

        val remainder = OniItemMass.mergeIntoContainer(container, incoming)
        assertEquals(10.0, OniItemMass.stackMass(container.getItem(0)), 1e-6)
        assertTrue(remainder.isEmpty)
    }

    @Test
    fun takeMassFromUntypedStackUsesWholeItems() {
        val stack = ItemStack(Items.APPLE, 3)
        val taken = OniItemMass.takeMass(stack, 1.7)
        assertEquals(1.0, taken, 1e-6)
        assertEquals(2, stack.count)
    }

    @Test
    fun mergeStacksByMassGroupsItems() {
        val a = ItemStack(Items.APPLE, 1)
        OniItemMass.setStackMass(a, 2.5)
        val b = ItemStack(Items.APPLE, 1)
        OniItemMass.setStackMass(b, 1.5)
        val c = ItemStack(Items.CARROT, 2)
        val merged = OniItemMass.mergeStacksByMass(listOf(a, b, c))

        assertEquals(2, merged.size)
        val apple = merged.first { it.item == Items.APPLE }
        val carrot = merged.first { it.item == Items.CARROT }
        assertEquals(4.0, OniItemMass.stackMass(apple), 1e-6)
        assertEquals(2.0, OniItemMass.stackMass(carrot), 1e-6)
    }
}
