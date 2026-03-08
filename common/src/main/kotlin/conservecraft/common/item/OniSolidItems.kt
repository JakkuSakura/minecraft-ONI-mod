package conservecraft.common.item

import conservecraft.common.element.ElementContents
import conservecraft.common.element.OniElements
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import kotlin.math.floor

object OniSolidItems {
    private const val ELEMENT_PREFIX = "element_"
    private const val KILOGRAM_SUFFIX = "_kg"
    private const val GRAM_SUFFIX = "_g"
    private const val MAX_STACK_SIZE = 999999
    private const val GRAMS_PER_KILOGRAM = 1000.0

    fun allRegistryPaths(): List<String> {
        val paths = ArrayList<String>(OniItemFactory.ELEMENTS.size * 3)
        for (basePath in OniItemFactory.ELEMENTS) {
            paths.add(basePath)
            paths.add("${basePath}${KILOGRAM_SUFFIX}")
            paths.add("${basePath}${GRAM_SUFFIX}")
        }
        return paths
    }

    fun basePath(elementId: String): String = "${ELEMENT_PREFIX}${elementId}"

    fun createItem(path: String, properties: Item.Properties): Item {
        val variant = variantForPath(path) ?: throw IllegalArgumentException("Unknown solid element item path: ${path}")
        return SolidElementItem(
            properties.stacksTo(MAX_STACK_SIZE),
            variant.elementId,
            variant.unitMassKg,
            variant.unitSuffix,
        )
    }

    fun elementIdOf(item: Item): String? = (item as? SolidElementItem)?.elementId

    fun unitMassKgOf(item: Item): Double? = (item as? SolidElementItem)?.unitMassKg

    fun specificHeatCapacityOf(stack: ItemStack): Double {
        val elementId = elementIdOf(stack.item) ?: return 1.0
        return OniElements.specificHeatCapacityForElementId(elementId) ?: 1.0
    }

    fun elementContentsOf(stack: ItemStack): ElementContents? {
        val elementId = elementIdOf(stack.item) ?: return null
        val mass = stackMassKg(stack)
        if (mass <= 0.0) {
            return null
        }
        return ElementContents(elementId, mass, OniItemThermal.temperatureK(stack))
    }

    fun stackMassKg(stack: ItemStack): Double {
        val unitMassKg = unitMassKgOf(stack.item) ?: return 0.0
        return stack.count.toDouble() * unitMassKg
    }

    fun takeMass(stack: ItemStack, massKg: Double): Double {
        val unitMassKg = unitMassKgOf(stack.item) ?: return 0.0
        if (stack.isEmpty || massKg <= 0.0 || unitMassKg <= 0.0) {
            return 0.0
        }
        val units = floor((massKg + 1e-9) / unitMassKg).toInt().coerceAtLeast(0)
        if (units <= 0) {
            return 0.0
        }
        val takenUnits = minOf(stack.count, units)
        if (takenUnits <= 0) {
            return 0.0
        }
        stack.shrink(takenUnits)
        return takenUnits.toDouble() * unitMassKg
    }

    fun encode(elementId: String, massKg: Double, temperatureK: Double): List<ItemStack> {
        if (massKg <= 0.0) {
            return emptyList()
        }
        val baseUnit = baseUnitMassKg(elementId)
        if (baseUnit <= 0.0) {
            return emptyList()
        }
        var remaining = massKg
        val result = ArrayList<ItemStack>(3)

        val baseCount = floor((remaining + 1e-9) / baseUnit).toInt()
        if (baseCount > 0) {
            createStack(basePath(elementId), baseCount, temperatureK)?.let(result::add)
            remaining -= baseCount.toDouble() * baseUnit
        }

        val kilogramCount = floor((remaining + 1e-9)).toInt()
        if (kilogramCount > 0) {
            createStack("${basePath(elementId)}${KILOGRAM_SUFFIX}", kilogramCount, temperatureK)?.let(result::add)
            remaining -= kilogramCount.toDouble()
        }

        val gramCount = ((remaining.coerceAtLeast(0.0) * GRAMS_PER_KILOGRAM) + 1e-6).toInt()
        if (gramCount > 0) {
            createStack("${basePath(elementId)}${GRAM_SUFFIX}", gramCount, temperatureK)?.let(result::add)
        }

        return result
    }

    private fun createStack(path: String, count: Int, temperatureK: Double): ItemStack? {
        if (count <= 0) {
            return null
        }
        val item = OniItemFactory.itemById(path) ?: return null
        val stack = ItemStack(item, count)
        OniItemThermal.setTemperatureK(stack, temperatureK)
        return stack
    }

    private fun baseUnitMassKg(elementId: String): Double {
        val spec = OniElements.elementSpec(elementId) ?: return 0.0
        return spec.massPerItem ?: 1.0
    }

    private fun variantForPath(path: String): SolidItemVariant? {
        if (!path.startsWith(ELEMENT_PREFIX)) {
            return null
        }
        return when {
            path.endsWith(KILOGRAM_SUFFIX) -> {
                val elementId = path.removePrefix(ELEMENT_PREFIX).removeSuffix(KILOGRAM_SUFFIX)
                SolidItemVariant(elementId, 1.0, "kg")
            }
            path.endsWith(GRAM_SUFFIX) -> {
                val elementId = path.removePrefix(ELEMENT_PREFIX).removeSuffix(GRAM_SUFFIX)
                SolidItemVariant(elementId, 0.001, "g")
            }
            else -> {
                val elementId = path.removePrefix(ELEMENT_PREFIX)
                val unitMassKg = baseUnitMassKg(elementId)
                SolidItemVariant(elementId, unitMassKg, null)
            }
        }
    }

    private data class SolidItemVariant(
        val elementId: String,
        val unitMassKg: Double,
        val unitSuffix: String?,
    )
}
