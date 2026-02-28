package mconi.common.world

import mconi.common.block.OniBlockFactory
import mconi.common.block.OniBlockLookup
import mconi.common.block.entity.OniMatterBlockEntity
import mconi.common.element.OniElements
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

object OniMatterAccess {
    private val gasByBlock: Map<Block, OniElements.GasSpec> = mapOf(
        OniBlockLookup.block(OniBlockFactory.OXYGEN_GAS) to OniElements.GAS_OXYGEN,
        OniBlockLookup.block(OniBlockFactory.CARBON_DIOXIDE_GAS) to OniElements.GAS_CARBON_DIOXIDE,
        OniBlockLookup.block(OniBlockFactory.HYDROGEN_GAS) to OniElements.GAS_HYDROGEN,
    )
    private val liquidByBlock: Map<Block, String> = mapOf(
        OniBlockLookup.block(OniBlockFactory.WATER) to OniElements.LIQUID_WATER,
        OniBlockLookup.block(OniBlockFactory.POLLUTED_WATER) to OniElements.LIQUID_POLLUTED_WATER,
        OniBlockLookup.block(OniBlockFactory.CRUDE_OIL) to OniElements.LIQUID_CRUDE_OIL,
        OniBlockLookup.block(OniBlockFactory.LAVA) to OniElements.LIQUID_LAVA,
    )

    fun gasSpec(state: BlockState): OniElements.GasSpec? = gasByBlock[state.block]

    fun liquidId(state: BlockState): String? = liquidByBlock[state.block]

    fun matterEntity(level: ServerLevel, pos: BlockPos): OniMatterBlockEntity? {
        return level.getBlockEntity(pos) as? OniMatterBlockEntity
    }
}
