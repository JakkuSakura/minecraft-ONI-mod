package conservecraft.common.sim.power

import net.minecraft.world.level.block.state.BlockState

interface PowerCatalog {
    fun wireCapacity(state: BlockState): Double?
    fun generatorOutput(state: BlockState): Double?
    fun consumerDemand(state: BlockState): Double?
    fun batteryCapacity(state: BlockState): Double?
    fun transformerThroughput(state: BlockState): Double?
}
