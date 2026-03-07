package conservecraft.forge

import conservecraft.common.AbstractModBootstrap
import conservecraft.common.block.OniBlockFactory
import conservecraft.common.block.entity.OniBlockEntityTypes
import conservecraft.common.block.entity.OniConduitBlockEntity
import conservecraft.common.block.entity.OniMatterBlockEntity
import conservecraft.common.refining.RefiningMachineBlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.eventbus.api.bus.BusGroup
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ForgeBlockEntities {
    private val BLOCK_ENTITIES: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AbstractModBootstrap.MOD_ID)

    private val MATTER = BLOCK_ENTITIES.register("matter") {
        val blocks = (OniBlockFactory.SOLID_IDS + OniBlockFactory.GAS_IDS + OniBlockFactory.LIQUID_IDS)
            .map { ForgeBlocks.blockHolder(it).get() }
            .toSet()
        BlockEntityType(::OniMatterBlockEntity, blocks)
    }
    private val CONDUIT = BLOCK_ENTITIES.register("conduit") {
        val blocks = listOf(OniBlockFactory.GAS_CONDUIT, OniBlockFactory.LIQUID_CONDUIT)
            .map { ForgeBlocks.blockHolder(it).get() }
            .toSet()
        BlockEntityType(::OniConduitBlockEntity, blocks)
    }
    private val REFINING_MACHINE = BLOCK_ENTITIES.register("refining_machine") {
        val blocks = OniBlockFactory.REFINING_IDS
            .map { ForgeBlocks.blockHolder(it).get() }
            .toSet()
        BlockEntityType(::RefiningMachineBlockEntity, blocks)
    }

    fun register(busGroup: BusGroup) {
        BLOCK_ENTITIES.register(busGroup)
    }

    fun bindTypes() {
        @Suppress("UNCHECKED_CAST")
        OniBlockEntityTypes.MATTER = MATTER.get() as BlockEntityType<OniMatterBlockEntity>
        @Suppress("UNCHECKED_CAST")
        OniBlockEntityTypes.CONDUIT = CONDUIT.get() as BlockEntityType<OniConduitBlockEntity>
        @Suppress("UNCHECKED_CAST")
        OniBlockEntityTypes.REFINING_MACHINE = REFINING_MACHINE.get() as BlockEntityType<RefiningMachineBlockEntity>
    }
}
