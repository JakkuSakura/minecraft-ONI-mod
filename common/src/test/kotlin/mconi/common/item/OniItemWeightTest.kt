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
class OniItemWeightTest {
    @BeforeAll
    fun bootstrapMinecraft() {
        TestMinecraftBootstrap.ensureBootstrapped()
    }

    @Test
    fun usesDefaultWeightWhenMissingTag() {
        val stack = ItemStack(Items.APPLE, 3)
        val weight = OniItemWeight.stackWeight(stack)
        assertEquals(3.0, weight, 1e-6)
    }

    @Test
    fun usesWeightTagWhenPresent() {
        val stack = ItemStack(Items.APPLE, 1)
        OniItemWeight.setStackWeight(stack, 7.5)
        val weight = OniItemWeight.stackWeight(stack)
        assertEquals(7.5, weight, 1e-6)
    }

    @Test
    fun takeWeightReducesTaggedStack() {
        val stack = ItemStack(Items.APPLE, 1)
        OniItemWeight.setStackWeight(stack, 5.0)
        val taken = OniItemWeight.takeWeight(stack, 2.0)
        assertEquals(2.0, taken, 1e-6)
        assertEquals(3.0, OniItemWeight.stackWeight(stack), 1e-6)
        assertEquals(1, stack.count)
    }

    @Test
    fun mergeIntoContainerHonorsCapacity() {
        val container = SimpleContainer(1)
        val existing = ItemStack(Items.APPLE, 1)
        OniItemWeight.setStackWeight(existing, 60.0)
        container.setItem(0, existing)

        val incoming = ItemStack(Items.APPLE, 1)
        OniItemWeight.setStackWeight(incoming, 10.0)

        val remainder = OniItemWeight.mergeIntoContainer(container, incoming)
        assertEquals(64.0, OniItemWeight.stackWeight(container.getItem(0)), 1e-6)
        assertTrue(OniItemWeight.stackWeight(remainder) >= 6.0 - 1e-9)
    }

    @Test
    fun mergeIntoEmptyContainerCreatesWeightStack() {
        val container = SimpleContainer(1)
        val incoming = ItemStack(Items.APPLE, 1)
        OniItemWeight.setStackWeight(incoming, 10.0)

        val remainder = OniItemWeight.mergeIntoContainer(container, incoming)
        assertEquals(10.0, OniItemWeight.stackWeight(container.getItem(0)), 1e-6)
        assertTrue(remainder.isEmpty)
    }

    @Test
    fun takeWeightFromUntypedStackUsesWholeItems() {
        val stack = ItemStack(Items.APPLE, 3)
        val taken = OniItemWeight.takeWeight(stack, 1.7)
        assertEquals(1.0, taken, 1e-6)
        assertEquals(2, stack.count)
    }

    @Test
    fun mergeStacksByWeightGroupsItems() {
        val a = ItemStack(Items.APPLE, 1)
        OniItemWeight.setStackWeight(a, 2.5)
        val b = ItemStack(Items.APPLE, 1)
        OniItemWeight.setStackWeight(b, 1.5)
        val c = ItemStack(Items.CARROT, 2)
        val merged = OniItemWeight.mergeStacksByWeight(listOf(a, b, c))

        assertEquals(2, merged.size)
        val apple = merged.first { it.item == Items.APPLE }
        val carrot = merged.first { it.item == Items.CARROT }
        assertEquals(4.0, OniItemWeight.stackWeight(apple), 1e-6)
        assertEquals(2.0, OniItemWeight.stackWeight(carrot), 1e-6)
    }
}
