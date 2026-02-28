package mconi.neoforge

import mconi.common.AbstractModBootstrap
import mconi.common.block.entity.ConstructionSiteBlockEntity
import mconi.common.block.OniBlockFactory
import mconi.common.block.entity.OniBlockEntityTypes
import mconi.common.block.entity.OniConduitBlockEntity
import mconi.common.block.entity.OniMatterBlockEntity
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object NeoforgeBlockEntities {
    private val BLOCK_ENTITIES: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AbstractModBootstrap.MOD_ID)

    private val CONSTRUCTION_SITE = BLOCK_ENTITIES.register(OniBlockFactory.CONSTRUCTION_SITE, Supplier {
        val block = NeoforgeBlocks.blockHolder(OniBlockFactory.CONSTRUCTION_SITE).get()
        BlockEntityType(::ConstructionSiteBlockEntity, setOf(block))
    })
    private val MATTER = BLOCK_ENTITIES.register("matter", Supplier {
        val blocks = (OniBlockFactory.GAS_IDS + OniBlockFactory.LIQUID_IDS)
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

    fun register(eventBus: IEventBus) {
        BLOCK_ENTITIES.register(eventBus)
    }

    fun bindTypes() {
        @Suppress("UNCHECKED_CAST")
        OniBlockEntityTypes.CONSTRUCTION_SITE = CONSTRUCTION_SITE.get() as BlockEntityType<ConstructionSiteBlockEntity>
        @Suppress("UNCHECKED_CAST")
        OniBlockEntityTypes.MATTER = MATTER.get() as BlockEntityType<OniMatterBlockEntity>
        @Suppress("UNCHECKED_CAST")
        OniBlockEntityTypes.CONDUIT = CONDUIT.get() as BlockEntityType<OniConduitBlockEntity>
    }
}
