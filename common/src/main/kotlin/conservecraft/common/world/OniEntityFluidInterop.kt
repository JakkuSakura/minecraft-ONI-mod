package conservecraft.common.world

import net.minecraft.core.BlockPos
import net.minecraft.tags.FluidTags
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.math.floor

object OniEntityFluidInterop {
    private const val EPSILON = 1.0e-6

    fun isInOniWater(entity: Entity): Boolean = intersectsLiquid(entity, conservecraft.common.element.OniElements.LIQUID_WATER)

    fun isInOniLava(entity: Entity): Boolean = intersectsLiquid(entity, conservecraft.common.element.OniElements.LIQUID_LAVA)

    fun isEyeInOniWater(entity: Entity): Boolean = eyeInLiquid(entity, conservecraft.common.element.OniElements.LIQUID_WATER)

    fun applyContactEffects(entity: Entity) {
        if (isInOniWater(entity)) {
            entity.fallDistance = 0.0
            if (entity.isOnFire) {
                entity.extinguishFire()
            }
        }
        if (isInOniLava(entity)) {
            entity.lavaHurt()
        }
    }

    private fun eyeInLiquid(entity: Entity, liquidId: String): Boolean {
        val eyePos = entity.eyePosition
        return liquidAtPoint(entity, eyePos) == liquidId
    }

    private fun intersectsLiquid(entity: Entity, liquidId: String): Boolean {
        val box = entity.boundingBox.deflate(EPSILON)
        if (box.minX >= box.maxX || box.minY >= box.maxY || box.minZ >= box.maxZ) {
            return false
        }
        val minX = floor(box.minX).toInt()
        val maxX = floor(box.maxX).toInt()
        val minY = floor(box.minY).toInt()
        val maxY = floor(box.maxY).toInt()
        val minZ = floor(box.minZ).toInt()
        val maxZ = floor(box.maxZ).toInt()
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val pos = BlockPos(x, y, z)
                    if (liquidIdAt(entity, pos) != liquidId) {
                        continue
                    }
                    val blockBox = AABB(x.toDouble(), y.toDouble(), z.toDouble(), x + 1.0, y + 1.0, z + 1.0)
                    if (box.intersects(blockBox)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun liquidAtPoint(entity: Entity, point: Vec3): String? {
        val pos = BlockPos.containing(point)
        return liquidIdAt(entity, pos)
    }

    private fun liquidIdAt(entity: Entity, pos: BlockPos): String? {
        val state = entity.level().getBlockState(pos)
        val oniLiquidId = OniMatterAccess.liquidId(state)
        if (oniLiquidId != null) {
            return oniLiquidId
        }
        return OniVanillaFluidInterop.oniLiquidId(state.fluidState)
    }
}
