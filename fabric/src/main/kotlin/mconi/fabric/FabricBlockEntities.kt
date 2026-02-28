package mconi.fabric

import mconi.common.AbstractModBootstrap
import mconi.common.block.OniBlockLookup
import mconi.common.block.OniBlockFactory
import mconi.common.block.entity.ConstructionSiteBlockEntity
import mconi.common.block.entity.OniBlockEntityTypes
import mconi.common.block.entity.OniConduitBlockEntity
import mconi.common.block.entity.OniMatterBlockEntity
import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier

object FabricBlockEntities {
    private var registered = false

    fun register() {
        if (registered) {
            return
        }
        registered = true

        val id = Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:${OniBlockFactory.CONSTRUCTION_SITE}")
            ?: throw IllegalArgumentException("Invalid block entity id")
        val block = OniBlockLookup.block(OniBlockFactory.CONSTRUCTION_SITE)
        val type = FabricBlockEntityTypeBuilder.create(::ConstructionSiteBlockEntity, block).build()
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, type)
        OniBlockEntityTypes.CONSTRUCTION_SITE = type

        val matterId = Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:matter")
            ?: throw IllegalArgumentException("Invalid block entity id")
        val matterBlocks = (OniBlockFactory.GAS_IDS + OniBlockFactory.LIQUID_IDS)
            .map { OniBlockLookup.block(it) }
            .toTypedArray()
        val matterType = FabricBlockEntityTypeBuilder.create(::OniMatterBlockEntity, *matterBlocks).build()
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, matterId, matterType)
        OniBlockEntityTypes.MATTER = matterType

        val conduitId = Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:conduit")
            ?: throw IllegalArgumentException("Invalid block entity id")
        val conduitBlocks = arrayOf(
            OniBlockLookup.block(OniBlockFactory.GAS_CONDUIT),
            OniBlockLookup.block(OniBlockFactory.LIQUID_CONDUIT)
        )
        val conduitType = FabricBlockEntityTypeBuilder.create(::OniConduitBlockEntity, *conduitBlocks).build()
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, conduitId, conduitType)
        OniBlockEntityTypes.CONDUIT = conduitType
    }
}
