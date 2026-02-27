package mconi.forge

import mconi.common.AbstractModInitializer
import mconi.common.block.entity.ConstructionSiteBlockEntity
import mconi.common.block.entity.OniBlockEntityTypes
import mconi.common.block.OniBlockFactory
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.eventbus.api.bus.BusGroup
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ForgeBlockEntities {
    private val BLOCK_ENTITIES: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AbstractModInitializer.MOD_ID)

    private val CONSTRUCTION_SITE = BLOCK_ENTITIES.register(OniBlockFactory.CONSTRUCTION_SITE) {
        val block = ForgeBlocks.blockHolder(OniBlockFactory.CONSTRUCTION_SITE).get()
        BlockEntityType(::ConstructionSiteBlockEntity, setOf(block))
    }

    fun register(busGroup: BusGroup) {
        BLOCK_ENTITIES.register(busGroup)
    }

    fun bindTypes() {
        @Suppress("UNCHECKED_CAST")
        OniBlockEntityTypes.CONSTRUCTION_SITE = CONSTRUCTION_SITE.get() as BlockEntityType<ConstructionSiteBlockEntity>
    }
}
