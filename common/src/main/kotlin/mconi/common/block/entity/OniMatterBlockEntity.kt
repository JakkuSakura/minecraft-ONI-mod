package mconi.common.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class OniMatterBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(OniBlockEntityTypes.MATTER, pos, state) {

    private var massKg: Double = 0.0
    private var temperatureK: Double = 293.15
    fun massKg(): Double = massKg
    fun temperatureK(): Double = temperatureK

    fun setMassKg(value: Double) {
        massKg = value.coerceAtLeast(0.0)
        setChanged()
    }

    fun setTemperatureK(value: Double) {
        temperatureK = value
        setChanged()
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.putDouble("Weight", massKg)
        output.putDouble("TemperatureK", temperatureK)
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        massKg = input.getDoubleOr("Weight", 0.0)
        temperatureK = input.getDoubleOr("TemperatureK", 293.15)
    }
}
