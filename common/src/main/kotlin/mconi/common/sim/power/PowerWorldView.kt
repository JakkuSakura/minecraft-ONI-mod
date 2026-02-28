package mconi.common.sim.power

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

interface PowerWorldView {
    val minY: Int
    val maxY: Int
    fun players(): Iterable<BlockPos>
    fun getBlockState(pos: BlockPos): BlockState
}
