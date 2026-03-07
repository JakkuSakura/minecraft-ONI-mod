package conservecraft.neoforge

import conservecraft.common.AbstractModBootstrap
import conservecraft.common.block.OniBlockFactory
import conservecraft.common.block.entity.OniBlockEntityTypes
import conservecraft.common.block.entity.OniConduitBlockEntity
import conservecraft.common.block.entity.OniMatterBlockEntity
import conservecraft.common.refining.RefiningMachineBlockEntity
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object NeoforgeBlockEntities {
    private val BLOCK_ENTITIES: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AbstractModBootstrap.MOD_ID)
    // TODO: rename to elements
    private val MATTER = BLOCK_ENTITIES.register("matter", Supplier {
        val blocks = (OniBlockFactory.SOLID_IDS + OniBlockFactory.GAS_IDS + OniBlockFactory.LIQUID_IDS)
            .map { NeoforgeBlocks.blockHolder(it).get() }
            .toSet()
        BlockEntityType(::OniMatterBlockEntity, blocks)
    })
    private val CONDUIT = BLOCK_ENTITIES.register("conduit", Supplier {
        val blocks = listOf(OniBlockFactory.GAS_CONDUIT, OniBlockFactory.LIQUID_CONDUIT)
            .map { NeoforgeBlocks.blockHolder(it).get() }
            .toSet()
        BlockEntityType(::OniConduitBlockEntity, blocks)
    })
    private val REFINING_MACHINE = BLOCK_ENTITIES.register("refining_machine", Supplier {
        val blocks = OniBlockFactory.REFINING_IDS
            .map { NeoforgeBlocks.blockHolder(it).get() }
            .toSet()
        BlockEntityType(::RefiningMachineBlockEntity, blocks)
    })

    fun register(eventBus: IEventBus) {
        BLOCK_ENTITIES.register(eventBus)
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
