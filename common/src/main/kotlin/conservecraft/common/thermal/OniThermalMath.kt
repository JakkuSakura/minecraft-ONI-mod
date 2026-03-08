package conservecraft.common.thermal

import conservecraft.common.element.ElementContents
import conservecraft.common.element.OniElements
import conservecraft.common.item.OniItemThermal
import conservecraft.common.item.OniSolidItems
import net.minecraft.world.item.ItemStack

object OniThermalMath {
    private const val DEFAULT_SPECIFIC_HEAT_CAPACITY = 1.0

    data class Sample(
        val massKg: Double,
        val temperatureK: Double,
        val specificHeatCapacity: Double,
    )

    fun averageTemperatureK(samples: Iterable<Sample>, defaultTemperatureK: Double = OniItemThermal.DEFAULT_TEMP_K): Double {
        var totalHeatCapacity = 0.0
        var totalEnergy = 0.0
        for (sample in samples) {
            if (sample.massKg <= 0.0 || sample.specificHeatCapacity <= 0.0) {
                continue
            }
            val heatCapacity = sample.massKg * sample.specificHeatCapacity
            totalHeatCapacity += heatCapacity
            totalEnergy += heatCapacity * sample.temperatureK
        }
        if (totalHeatCapacity <= 0.0) {
            return defaultTemperatureK
        }
        return totalEnergy / totalHeatCapacity
    }

    fun averageElementTemperatureK(elements: Iterable<ElementContents>, defaultTemperatureK: Double = OniItemThermal.DEFAULT_TEMP_K): Double {
        return averageTemperatureK(elements.map { element ->
            Sample(
                massKg = element.mass,
                temperatureK = element.temperatureK,
                specificHeatCapacity = OniElements.specificHeatCapacityForElementId(element.elementId)
                    ?: DEFAULT_SPECIFIC_HEAT_CAPACITY,
            )
        }, defaultTemperatureK)
    }

    fun averageItemTemperatureK(stacks: Iterable<ItemStack>, defaultTemperatureK: Double = OniItemThermal.DEFAULT_TEMP_K): Double {
        return averageTemperatureK(stacks.mapNotNull { stack ->
            if (stack.isEmpty) {
                null
            } else {
                Sample(
                    massKg = conservecraft.common.item.OniItemMass.stackMass(stack),
                    temperatureK = OniItemThermal.temperatureK(stack),
                    specificHeatCapacity = OniSolidItems.specificHeatCapacityOf(stack),
                )
            }
        }, defaultTemperatureK)
    }

    // Immediate equilibration is temporary until per-container thermal exchange is modeled.
}
