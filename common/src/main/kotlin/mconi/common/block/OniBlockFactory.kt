package mconi.common.block

import mconi.common.AbstractModInitializer
import mconi.common.content.OniBlockIds
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.level.block.state.BlockBehaviour

object OniBlockFactory {
    fun createBlock(id: String, key: ResourceKey<Block>): Block {
        val base = BlockBehaviour.Properties.of().setId(key)
        return when (id) {
            OniBlockIds.REGOLITH -> OniMassBlock(
                id,
                base.mapColor(MapColor.COLOR_BROWN).strength(0.6f).sound(SoundType.GRAVEL)
            )
            OniBlockIds.SEDIMENTARY_ROCK -> OniMassBlock(
                id,
                base.mapColor(MapColor.COLOR_YELLOW).strength(1.2f, 3.0f).sound(SoundType.STONE)
            )
            OniBlockIds.IGNEOUS_ROCK -> OniMassBlock(
                id,
                base.mapColor(MapColor.COLOR_GRAY).strength(1.5f, 4.0f).sound(SoundType.STONE)
            )
            OniBlockIds.GRANITE -> OniMassBlock(
                id,
                base.mapColor(MapColor.COLOR_ORANGE).strength(1.6f, 4.5f).sound(SoundType.STONE)
            )
            OniBlockIds.ABYSSALITE -> OniMassBlock(
                id,
                base.mapColor(MapColor.COLOR_BLACK).strength(50.0f, 1200.0f).sound(SoundType.STONE)
            )
            OniBlockIds.ALGAE -> OniMassBlock(
                id,
                base.mapColor(MapColor.COLOR_GREEN).strength(0.4f).sound(SoundType.GRASS)
            )
            OniBlockIds.POLLUTED_DIRT -> OniMassBlock(
                id,
                base.mapColor(MapColor.COLOR_BROWN).strength(0.6f).sound(SoundType.GRAVEL)
            )
            OniBlockIds.PRINTING_POD -> PrintingPodBlock(
                id,
                base.mapColor(MapColor.COLOR_LIGHT_GRAY).strength(3.0f, 6.0f).sound(SoundType.METAL)
            )
            OniBlockIds.WATER -> OniFluidBlock(
                base.mapColor(MapColor.WATER).noCollision().noOcclusion().strength(0.0f).sound(SoundType.WET_GRASS)
            )
            OniBlockIds.POLLUTED_WATER -> OniFluidBlock(
                base.mapColor(MapColor.COLOR_GREEN).noCollision().noOcclusion().strength(0.0f).sound(SoundType.WET_GRASS)
            )
            OniBlockIds.CRUDE_OIL -> OniFluidBlock(
                base.mapColor(MapColor.COLOR_BROWN).noCollision().noOcclusion().strength(0.0f).sound(SoundType.SLIME_BLOCK)
            )
            OniBlockIds.LAVA -> OniFluidBlock(
                base.mapColor(MapColor.COLOR_ORANGE).noCollision().noOcclusion().strength(0.0f).sound(SoundType.SLIME_BLOCK)
            )
            OniBlockIds.OXYGEN_GAS -> OniGasBlock(
                base.noCollision().noOcclusion().strength(0.0f).sound(SoundType.WOOL)
            )
            OniBlockIds.CARBON_DIOXIDE_GAS -> OniGasBlock(
                base.noCollision().noOcclusion().strength(0.0f).sound(SoundType.WOOL)
            )
            OniBlockIds.HYDROGEN_GAS -> OniGasBlock(
                base.noCollision().noOcclusion().strength(0.0f).sound(SoundType.WOOL)
            )
            else -> throw IllegalArgumentException("Unknown block id: $id")
        }
    }

    fun createBlock(id: String): Block {
        val identifier = Identifier.tryParse("${AbstractModInitializer.MOD_ID}:$id")
            ?: throw IllegalArgumentException("Invalid block id: $id")
        val key = ResourceKey.create(Registries.BLOCK, identifier)
        return createBlock(id, key)
    }
}
