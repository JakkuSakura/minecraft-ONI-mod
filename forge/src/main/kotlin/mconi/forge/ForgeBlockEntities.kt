package mconi.forge

import mconi.common.AbstractModBootstrap
import mconi.common.block.entity.ConstructionSiteBlockEntity
import mconi.common.block.entity.OniBlockEntityTypes
import mconi.common.block.entity.OniMatterBlockEntity
import mconi.common.block.OniBlockFactory
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.eventbus.api.bus.BusGroup
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ForgeBlockEntities {
    private val BLOCK_ENTITIES: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AbstractModBootstrap.MOD_ID)

    private val CONSTRUCTION_SITE = BLOCK_ENTITIES.register(OniBlockFactory.CONSTRUCTION_SITE) {
        val block = ForgeBlocks.blockHolder(OniBlockFactory.CONSTRUCTION_SITE).get()
        BlockEntityType(::ConstructionSiteBlockEntity, setOf(block))
    }
    private val MATTER = BLOCK_ENTITIES.register("matter") {
        val blocks = (OniBlockFactory.GAS_IDS + OniBlockFactory.LIQUID_IDS)
            .map { ForgeBlocks.blockHolder(it).get() }
            .toSet()
        BlockEntityType(::OniMatterBlockEntity, blocks)
    }

    fun register(busGroup: BusGroup) {
        BLOCK_ENTITIES.register(busGroup)
    }

    fun bindTypes() {
        @Suppress("UNCHECKED_CAST")
        OniBlockEntityTypes.CONSTRUCTION_SITE = CONSTRUCTION_SITE.get() as BlockEntityType<ConstructionSiteBlockEntity>
        @Suppress("UNCHECKED_CAST")
        OniBlockEntityTypes.MATTER = MATTER.get() as BlockEntityType<OniMatterBlockEntity>
    }
}
