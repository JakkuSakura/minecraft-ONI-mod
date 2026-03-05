package conservecraft.common.world

import net.minecraft.server.level.ServerLevel

object OniWorldType {
    fun isConserveCraftWorld(level: ServerLevel): Boolean {
        val generator = level.chunkSource.generator
        return generator is OniChunkGenerator
    }
}
