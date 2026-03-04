package conservecraft.common.block.entity

import conservecraft.common.element.ElementContents
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

open class OniElementBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState
) : BlockEntity(type, pos, state) {
    private val elements: MutableList<ElementContents> = ArrayList()

    fun elements(): List<ElementContents> = elements.toList()

    fun setElements(contents: List<ElementContents>) {
        elements.clear()
        elements.addAll(contents)
        setChanged()
    }

    fun totalMass(): Double = elements.sumOf { it.mass }

    fun averageTemperatureK(): Double {
        val total = totalMass()
        if (total <= 0.0) {
            return 293.15
        }
        val weighted = elements.sumOf { it.mass * it.temperatureK }
        return weighted / total
    }

    fun setTemperatureK(value: Double) {
        if (elements.isEmpty()) {
            return
        }
        for (i in elements.indices) {
            val entry = elements[i]
            elements[i] = entry.copy(temperatureK = value)
        }
        setChanged()
    }

    fun scaleMassTo(total: Double) {
        if (elements.isEmpty()) {
            return
        }
        val current = totalMass()
        if (current <= 0.0) {
            val entry = elements.first()
            elements[0] = entry.copy(mass = total.coerceAtLeast(0.0))
            for (i in 1 until elements.size) {
                val rest = elements[i]
                elements[i] = rest.copy(mass = 0.0)
            }
            setChanged()
            return
        }
        val scale = (total / current).coerceAtLeast(0.0)
        for (i in elements.indices) {
            val entry = elements[i]
            elements[i] = entry.copy(mass = entry.mass * scale)
        }
        setChanged()
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.putInt("ElementCount", elements.size)
        for ((index, entry) in elements.withIndex()) {
            output.putString("Element_${index}_Id", entry.elementId)
            output.putDouble("Element_${index}_Mass", entry.mass)
            output.putDouble("Element_${index}_TempK", entry.temperatureK)
        }
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        elements.clear()
        val count = input.getIntOr("ElementCount", 0)
        for (i in 0 until count) {
            val id = input.getStringOr("Element_${i}_Id", "")
            if (id.isBlank()) {
                continue
            }
            val mass = input.getDoubleOr("Element_${i}_Mass", 0.0)
            val tempK = input.getDoubleOr("Element_${i}_TempK", 293.15)
            elements.add(ElementContents(id, mass, tempK))
        }
    }
}
