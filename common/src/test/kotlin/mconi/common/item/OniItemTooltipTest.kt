package mconi.common.item

import mconi.common.TestMinecraftBootstrap
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.CustomData
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OniItemTooltipTest {
    @BeforeAll
    fun bootstrapMinecraft() {
        TestMinecraftBootstrap.ensureBootstrapped()
    }

    @Test
    fun showsMassAndTemperatureFromTags() {
        val stack = ItemStack(Items.APPLE, 1)
        OniItemMass.setStackWeightKg(stack, 3.5)
        CustomData.update(DataComponents.CUSTOM_DATA, stack) { root ->
            root.putDouble(BottledMatterItem.TAG_TEMP_K, 310.0)
        }

        val lines = mutableListOf<Component>()
        val consumer = java.util.function.Consumer<Component> { component ->
            lines.add(component)
        }
        OniItemTooltip.appendDetails(stack, consumer)

        assertEquals(2, lines.size)
    }

    @Test
    fun skipsTooltipWhenNoDataAvailable() {
        val stack = ItemStack(Items.APPLE, 1)
        val lines = mutableListOf<Component>()
        val consumer = java.util.function.Consumer<Component> { component ->
            lines.add(component)
        }
        OniItemTooltip.appendDetails(stack, consumer)
        assertEquals(0, lines.size)
    }
}
