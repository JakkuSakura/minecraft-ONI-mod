package mconi.common.block.entity

import mconi.common.element.ElementContents
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class OniMatterBlockEntity(pos: BlockPos, state: BlockState) :
    OniElementBlockEntity(OniBlockEntityTypes.MATTER, pos, state) {

    fun mass(): Double = totalMass()

    fun temperatureK(): Double = averageTemperatureK()

    fun setMass(value: Double) {
        scaleMassTo(value.coerceAtLeast(0.0))
    }

    fun setContents(elementId: String, mass: Double, temperatureK: Double) {
        setElements(listOf(ElementContents(elementId, mass.coerceAtLeast(0.0), temperatureK)))
    }

    fun ensureContents(elementId: String, mass: Double, temperatureK: Double) {
        val current = elements().firstOrNull()
        if (current == null || current.elementId != elementId) {
            setContents(elementId, mass, temperatureK)
            return
        }
        setMass(mass)
        setTemperatureK(temperatureK)
    }
}
