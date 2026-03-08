package conservecraft.common.item

import conservecraft.common.element.OniElements
import conservecraft.common.thermal.OniThermalMath
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import kotlin.math.abs
import kotlin.math.round

object OniItemThermal {
    const val DEFAULT_TEMP_K: Double = 293.15
    private const val TEMPERATURE_QUANTUM_K: Double = 1.0
    private const val TEMPERATURE_EPSILON: Double = 1e-6

    fun temperatureK(stack: ItemStack): Double {
        val data = stack.get(DataComponents.CUSTOM_DATA) ?: return DEFAULT_TEMP_K
        val tag = data.copyTag()
        if (!tag.contains(BottledMatterItem.TAG_TEMP_K)) {
            return DEFAULT_TEMP_K
        }
        return tag.getDouble(BottledMatterItem.TAG_TEMP_K).orElse(DEFAULT_TEMP_K)
    }

    fun setTemperatureK(stack: ItemStack, temperatureK: Double) {
        if (stack.isEmpty) {
            return
        }
        val quantized = quantizeTemperatureK(temperatureK)
        CustomData.update(DataComponents.CUSTOM_DATA, stack) { root ->
            if (abs(quantized - DEFAULT_TEMP_K) <= TEMPERATURE_EPSILON) {
                root.remove(BottledMatterItem.TAG_TEMP_K)
            } else {
                root.putDouble(BottledMatterItem.TAG_TEMP_K, quantized)
            }
        }
    }

    fun quantizeTemperatureK(temperatureK: Double): Double {
        return round(temperatureK / TEMPERATURE_QUANTUM_K) * TEMPERATURE_QUANTUM_K
    }

    fun specificHeatCapacity(stack: ItemStack): Double {
        val solidElementId = OniSolidItems.elementIdOf(stack.item)
        if (solidElementId != null) {
            return OniElements.specificHeatCapacityForElementId(solidElementId) ?: 1.0
        }
        val itemId = BuiltInRegistries.ITEM.getKey(stack.item)?.toString()
        val elementId = itemId?.let(OniElements::elementIdForItemId)
        if (elementId != null) {
            return OniElements.specificHeatCapacityForElementId(elementId) ?: 1.0
        }
        return 1.0
    }

    fun conservedCraftingTemperatureK(container: Container): Double {
        return OniThermalMath.stateOfContainer(container).temperatureK(DEFAULT_TEMP_K)
    }

    fun equalizeContainerTemperature(container: Container, extraSamples: List<OniThermalMath.Sample> = emptyList()) {
        val baseState = OniThermalMath.stateOfContainer(container)
        val combinedState = if (extraSamples.isEmpty()) {
            baseState
        } else {
            OniThermalMath.stateOf(extraSamples).let { extra ->
                OniThermalMath.ThermalState(
                    totalHeatCapacity = baseState.totalHeatCapacity + extra.totalHeatCapacity,
                    totalHeatEnergy = baseState.totalHeatEnergy + extra.totalHeatEnergy,
                )
            }
        }
        val mixedTemperatureK = combinedState.temperatureK(DEFAULT_TEMP_K)
        for (index in 0 until container.containerSize) {
            val stack = container.getItem(index)
            if (!stack.isEmpty && OniItemMass.stackMass(stack) > 0.0) {
                setTemperatureK(stack, mixedTemperatureK)
            }
        }
        container.setChanged()
    }

    fun hasMassTag(stack: ItemStack): Boolean {
        if (stack.count != 1) {
            return false
        }
        val data = stack.get(DataComponents.CUSTOM_DATA) ?: return false
        return data.copyTag().contains(OniItemMass.TAG_MASS)
    }
}
