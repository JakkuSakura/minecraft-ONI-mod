package mconi.common.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class OniMatterBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(OniBlockEntityTypes.MATTER, pos, state) {

    private var mass: Double = 0.0
    private var temperatureK: Double = 293.15
    fun mass(): Double = mass
    fun temperatureK(): Double = temperatureK

    fun setMass(value: Double) {
        mass = value.coerceAtLeast(0.0)
        setChanged()
    }

    fun setTemperatureK(value: Double) {
        temperatureK = value
        setChanged()
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.putDouble("Mass", mass)
        output.putDouble("TemperatureK", temperatureK)
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        mass = input.getDoubleOr("Mass", 0.0)
        temperatureK = input.getDoubleOr("TemperatureK", 293.15)
    }
}
