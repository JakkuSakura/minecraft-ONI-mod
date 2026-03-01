package mconi.common.world

import mconi.common.block.OniBlockFactory
import mconi.common.block.OniBlockLookup
import mconi.common.block.entity.OniMatterBlockEntity
import mconi.common.element.OniElements
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

object OniMatterAccess {
    private val gasByBlock: Map<Block, OniElements.GasSpec> = mapOf(
        OniBlockLookup.block(OniBlockFactory.OXYGEN_GAS) to OniElements.GAS_OXYGEN,
        OniBlockLookup.block(OniBlockFactory.CARBON_DIOXIDE_GAS) to OniElements.GAS_CARBON_DIOXIDE,
        OniBlockLookup.block(OniBlockFactory.HYDROGEN_GAS) to OniElements.GAS_HYDROGEN,
        OniBlockLookup.block(OniBlockFactory.METHANE_GAS) to OniElements.GAS_METHANE,
        OniBlockLookup.block(OniBlockFactory.STEAM_GAS) to OniElements.GAS_STEAM,
        OniBlockLookup.block(OniBlockFactory.CHLORINE_GAS) to OniElements.GAS_CHLORINE,
    )
    private val liquidByBlock: Map<Block, String> = mapOf(
        OniBlockLookup.block(OniBlockFactory.WATER) to OniElements.LIQUID_WATER,
        OniBlockLookup.block(OniBlockFactory.POLLUTED_WATER) to OniElements.LIQUID_POLLUTED_WATER,
        OniBlockLookup.block(OniBlockFactory.CRUDE_OIL) to OniElements.LIQUID_CRUDE_OIL,
        OniBlockLookup.block(OniBlockFactory.LAVA) to OniElements.LIQUID_LAVA,
        OniBlockLookup.block(OniBlockFactory.SALT_WATER) to OniElements.LIQUID_SALT_WATER,
        OniBlockLookup.block(OniBlockFactory.BRINE) to OniElements.LIQUID_BRINE,
        OniBlockLookup.block(OniBlockFactory.ETHANOL) to OniElements.LIQUID_ETHANOL,
        OniBlockLookup.block(OniBlockFactory.PETROLEUM) to OniElements.LIQUID_PETROLEUM,
        OniBlockLookup.block(OniBlockFactory.MILK) to OniElements.LIQUID_MILK,
        OniBlockLookup.block(OniBlockFactory.NATURAL_RESIN) to OniElements.LIQUID_NATURAL_RESIN,
        OniBlockLookup.block(OniBlockFactory.PHYTO_OIL) to OniElements.LIQUID_PHYTO_OIL,
        OniBlockLookup.block(OniBlockFactory.MOLTEN_GLASS) to OniElements.LIQUID_MOLTEN_GLASS,
        OniBlockLookup.block(OniBlockFactory.SUPER_COOLANT) to OniElements.LIQUID_SUPER_COOLANT,
        OniBlockLookup.block(OniBlockFactory.VISCO_GEL) to OniElements.LIQUID_VISCO_GEL,
    )

    fun gasSpec(state: BlockState): OniElements.GasSpec? = gasByBlock[state.block]

    fun liquidId(state: BlockState): String? = liquidByBlock[state.block]

    fun matterEntity(level: Level, pos: BlockPos): OniMatterBlockEntity? {
        return level.getBlockEntity(pos) as? OniMatterBlockEntity
    }
}
