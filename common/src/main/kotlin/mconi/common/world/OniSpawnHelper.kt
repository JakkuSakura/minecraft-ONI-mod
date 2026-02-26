package mconi.common.world

import net.minecraft.core.BlockPos

object OniSpawnHelper {
    @JvmStatic
    fun spawnPos(): BlockPos {
        return BlockPos(OniWorldLayout.POD_X, OniWorldLayout.POD_Y + 1, OniWorldLayout.POD_Z)
    }
}
