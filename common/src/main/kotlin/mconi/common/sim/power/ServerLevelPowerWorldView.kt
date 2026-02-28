package mconi.common.sim.power

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState

class ServerLevelPowerWorldView(private val level: ServerLevel) : PowerWorldView {
    override val minY: Int = level.minY
    override val maxY: Int = level.maxY - 1

    override fun players(): Iterable<BlockPos> {
        return level.players().map { it.blockPosition() }
    }

    override fun getBlockState(pos: BlockPos): BlockState {
        return level.getBlockState(pos)
    }
}
