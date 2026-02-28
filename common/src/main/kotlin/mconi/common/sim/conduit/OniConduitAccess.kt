package mconi.common.sim.conduit

import mconi.common.block.OniBlockFactory
import mconi.common.block.OniBlockLookup
import mconi.common.block.entity.OniConduitBlockEntity
import mconi.common.element.OniElements
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

object OniConduitAccess {
    private val gasConduit: Block = OniBlockLookup.block(OniBlockFactory.GAS_CONDUIT)
    private val liquidConduit: Block = OniBlockLookup.block(OniBlockFactory.LIQUID_CONDUIT)

    fun isGasConduit(state: BlockState): Boolean = state.block == gasConduit

    fun isLiquidConduit(state: BlockState): Boolean = state.block == liquidConduit

    fun conduitEntity(level: ServerLevel, pos: BlockPos): OniConduitBlockEntity? {
        return level.getBlockEntity(pos) as? OniConduitBlockEntity
    }

    fun resolveElementIdForGas(input: String?): String? {
        if (input == null) {
            return null
        }
        return OniElements.parseGas(input)?.id
    }

    fun resolveElementIdForLiquid(input: String?): String? {
        if (input == null) {
            return null
        }
        val resolved = OniElements.liquidSpec(input)?.id ?: OniElements.parseLiquidId(input)
        return if (resolved == OniElements.LIQUID_NONE) null else resolved
    }
}
