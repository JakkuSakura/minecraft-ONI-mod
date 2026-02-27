package mconi.fabric

import mconi.common.AbstractModInitializer
import mconi.common.block.OniBlockLookup
import mconi.common.block.entity.ConstructionSiteBlockEntity
import mconi.common.block.entity.OniBlockEntityTypes
import mconi.common.block.OniBlockFactory
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

        val id = Identifier.tryParse("${AbstractModInitializer.MOD_ID}:${OniBlockFactory.CONSTRUCTION_SITE}")
            ?: throw IllegalArgumentException("Invalid block entity id")
        val block = OniBlockLookup.block(OniBlockFactory.CONSTRUCTION_SITE)
        val type = FabricBlockEntityTypeBuilder.create(::ConstructionSiteBlockEntity, block).build()
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, type)
        OniBlockEntityTypes.CONSTRUCTION_SITE = type
    }
}
