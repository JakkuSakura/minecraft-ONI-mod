package conservecraft.common.sim

import conservecraft.common.block.OniBlockLookup
import conservecraft.common.block.entity.ConstructionSiteBlockEntity
import conservecraft.common.block.OniBlockFactory
import conservecraft.common.item.OniBlueprintSelection
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

object OniConstructionSitePlacer {
    fun place(
        level: Level,
        clickedPos: BlockPos,
        face: Direction,
        selection: OniBlueprintSelection,
        player: Player?
    ): Boolean {
        val targetPos = resolvePlacementPos(level, clickedPos, face) ?: return false
        val state = OniBlockLookup.state(OniBlockFactory.CONSTRUCTION_SITE)
        if (!level.setBlock(targetPos, state, 3)) {
            return false
        }
        val blockEntity = level.getBlockEntity(targetPos) as? ConstructionSiteBlockEntity ?: return false
        blockEntity.applySelection(selection, player)
        return true
    }

    private fun resolvePlacementPos(level: Level, clickedPos: BlockPos, face: Direction): BlockPos? {
        val currentState = level.getBlockState(clickedPos)
        val targetPos = if (currentState.canBeReplaced()) clickedPos else clickedPos.relative(face)
        val targetState = level.getBlockState(targetPos)
        if (!targetState.canBeReplaced()) {
            return null
        }
        return targetPos
    }
}
