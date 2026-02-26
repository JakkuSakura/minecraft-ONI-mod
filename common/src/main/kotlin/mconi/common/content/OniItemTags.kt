package mconi.common.content

import mconi.common.AbstractModInitializer
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

object OniItemTags {
    val BUILD_MATERIALS: TagKey<Item> = TagKey.create(
        Registries.ITEM,
        Identifier.tryParse("${AbstractModInitializer.MOD_ID}:build_materials")
            ?: throw IllegalStateException("Failed to create build_materials tag key")
    )
}
