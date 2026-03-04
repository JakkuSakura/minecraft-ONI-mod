package conservecraft.common.block

import conservecraft.common.AbstractModBootstrap
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

object OniBlockLookup {
    fun block(id: String): Block {
        val identifier = Identifier.tryParse("${AbstractModBootstrap.MOD_ID}:$id")
            ?: throw IllegalArgumentException("Invalid block id: $id")
        return BuiltInRegistries.BLOCK.getOptional(identifier)
            .orElseThrow { IllegalStateException("Block not found: $identifier") }
    }

    fun state(id: String): BlockState = block(id).stateDefinition.any()
}
