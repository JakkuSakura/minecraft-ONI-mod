package conservecraft.common.block.entity

import net.minecraft.world.level.block.entity.BlockEntityType

object OniBlockEntityTypes {
    lateinit var MATTER: BlockEntityType<OniMatterBlockEntity>
    lateinit var CONDUIT: BlockEntityType<OniConduitBlockEntity>
    lateinit var REFINING_MACHINE: BlockEntityType<conservecraft.common.refining.RefiningMachineBlockEntity>
}
