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
class OniInventoryWeightsTest {
    @BeforeAll
    fun bootstrapMinecraft() {
        TestMinecraftBootstrap.ensureBootstrapped()
    }

    @Test
    fun amountUnitsUsesRawCountForNonOniItems() {
        val stack = ItemStack(Items.APPLE, 12)
        val units = invokeOniItemAmounts(stack)
        assertEquals(12.0, units, 1e-6)
    }

    @Test
    fun stackWeightUsesDefaultMassForNonOniItems() {
        val stack = ItemStack(Items.APPLE, 10)
        val weight = invokeOniInventoryWeights(stack)
        assertEquals(10.0, weight, 1e-6)
    }

    @Test
    fun capacityUsesSlotCount() {
        val container = SimpleContainer(2)
        val capacity = invokeOniCapacity(container)
        assertEquals(128.0, capacity, 1e-6)
    }

    private fun invokeOniItemAmounts(stack: ItemStack): Double {
        val clazz = Class.forName("mconi.common.item.OniItemAmounts")
        val instance = clazz.getField("INSTANCE").get(null)
        val method = clazz.getMethod("amountUnits", ItemStack::class.java)
        return method.invoke(instance, stack) as Double
    }

    private fun invokeOniInventoryWeights(stack: ItemStack): Double {
        val clazz = Class.forName("mconi.common.item.OniInventoryWeights")
        val instance = clazz.getField("INSTANCE").get(null)
        val method = clazz.getMethod("stackWeightKg", ItemStack::class.java)
        return method.invoke(instance, stack) as Double
    }

    private fun invokeOniCapacity(container: SimpleContainer): Double {
        val clazz = Class.forName("mconi.common.item.OniInventoryWeights")
        val instance = clazz.getField("INSTANCE").get(null)
        val method = clazz.getMethod("capacityKg", net.minecraft.world.Container::class.java)
        return method.invoke(instance, container) as Double
    }
}
