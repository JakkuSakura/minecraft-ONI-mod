package conservecraft.common.thermal

import conservecraft.common.element.ElementContents
import conservecraft.common.element.OniElements
import conservecraft.common.item.OniItemMass
import conservecraft.common.item.OniItemThermal
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack

object OniThermalMath {
    private const val DEFAULT_SPECIFIC_HEAT_CAPACITY = 1.0

    data class Sample(
        val massKg: Double,
        val temperatureK: Double,
        val specificHeatCapacity: Double,
    ) {
        fun heatCapacity(): Double = massKg * specificHeatCapacity
        fun heatEnergy(): Double = heatCapacity() * temperatureK
    }

    data class ThermalState(
        val totalHeatCapacity: Double,
        val totalHeatEnergy: Double,
    ) {
        fun temperatureK(defaultTemperatureK: Double = OniItemThermal.DEFAULT_TEMP_K): Double {
            if (totalHeatCapacity <= 0.0) {
                return defaultTemperatureK
            }
            return totalHeatEnergy / totalHeatCapacity
        }

        fun withAdded(sample: Sample): ThermalState {
            return ThermalState(
                totalHeatCapacity = totalHeatCapacity + sample.heatCapacity(),
                totalHeatEnergy = totalHeatEnergy + sample.heatEnergy(),
            )
        }

        fun withRemoved(sample: Sample): ThermalState {
            return ThermalState(
                totalHeatCapacity = (totalHeatCapacity - sample.heatCapacity()).coerceAtLeast(0.0),
                totalHeatEnergy = (totalHeatEnergy - sample.heatEnergy()).coerceAtLeast(0.0),
            )
        }
    }

    fun stateOf(samples: Iterable<Sample>): ThermalState {
        var totalHeatCapacity = 0.0
        var totalHeatEnergy = 0.0
        for (sample in samples) {
            if (sample.massKg <= 0.0 || sample.specificHeatCapacity <= 0.0) {
                continue
            }
            totalHeatCapacity += sample.heatCapacity()
            totalHeatEnergy += sample.heatEnergy()
        }
        return ThermalState(totalHeatCapacity, totalHeatEnergy)
    }

    fun sampleOf(stack: ItemStack): Sample? {
        if (stack.isEmpty) {
            return null
        }
        val massKg = OniItemMass.stackMass(stack)
        if (massKg <= 0.0) {
            return null
        }
        return Sample(
            massKg = massKg,
            temperatureK = OniItemThermal.temperatureK(stack),
            specificHeatCapacity = OniItemThermal.specificHeatCapacity(stack),
        )
    }

    fun sampleOf(element: ElementContents): Sample {
        return Sample(
            massKg = element.mass,
            temperatureK = element.temperatureK,
            specificHeatCapacity = OniElements.specificHeatCapacityForElementId(element.elementId)
                ?: DEFAULT_SPECIFIC_HEAT_CAPACITY,
        )
    }

    fun stateOfContainer(container: Container): ThermalState {
        val samples = ArrayList<Sample>(container.containerSize)
        for (index in 0 until container.containerSize) {
            sampleOf(container.getItem(index))?.let(samples::add)
        }
        return stateOf(samples)
    }

    fun averageTemperatureK(samples: Iterable<Sample>, defaultTemperatureK: Double = OniItemThermal.DEFAULT_TEMP_K): Double {
        return stateOf(samples).temperatureK(defaultTemperatureK)
    }

    fun averageElementTemperatureK(elements: Iterable<ElementContents>, defaultTemperatureK: Double = OniItemThermal.DEFAULT_TEMP_K): Double {
        return stateOf(elements.map(::sampleOf)).temperatureK(defaultTemperatureK)
    }

    fun averageItemTemperatureK(stacks: Iterable<ItemStack>, defaultTemperatureK: Double = OniItemThermal.DEFAULT_TEMP_K): Double {
        return stateOf(stacks.mapNotNull(::sampleOf)).temperatureK(defaultTemperatureK)
    }

    // Immediate equilibration is temporary until per-container thermal exchange is modeled.
}
