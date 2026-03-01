package mconi.common.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class OniConduitBlockEntity(pos: BlockPos, state: BlockState) :
    OniElementBlockEntity(OniBlockEntityTypes.CONDUIT, pos, state) {

    private var elementId: String? = null
    private var mass: Double = 0.0
    private var temperatureK: Double = 293.15

    fun elementId(): String? = elementId

    fun mass(): Double = mass

    fun temperatureK(): Double = temperatureK

    fun setElementId(value: String?) {
        elementId = value
        setChanged()
    }

    fun setMass(value: Double) {
        mass = value.coerceAtLeast(0.0)
        if (mass == 0.0) {
            elementId = null
        }
        setChanged()
    }

    fun setContentsTemperatureK(value: Double) {
        temperatureK = value
        setChanged()
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        if (elementId != null) {
            output.putString("ElementId", elementId ?: "")
        }
        output.putDouble("Mass", mass)
        output.putDouble("TemperatureK", temperatureK)
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        elementId = input.getStringOr("ElementId", "").ifBlank { null }
        mass = input.getDoubleOr("Mass", 0.0)
        temperatureK = input.getDoubleOr("TemperatureK", 293.15)
    }
}
