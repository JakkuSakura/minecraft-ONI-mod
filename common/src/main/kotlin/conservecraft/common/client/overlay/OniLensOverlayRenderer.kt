package conservecraft.common.client.overlay

import com.mojang.blaze3d.vertex.PoseStack
import conservecraft.common.block.entity.OniElementBlockEntity
import conservecraft.common.item.SystemGlassesItem
import conservecraft.common.sim.OniServices
import conservecraft.common.sim.model.SystemLens
import conservecraft.common.world.OniMatterAccess
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import org.joml.Matrix4f

object OniLensOverlayRenderer {
    fun render(
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        level: Level,
        player: Player
    ) {
        val lens = activeLens(player) ?: return
        val config = OniServices.systemRuntime().config()
        val radius = config.worldSampleRadiusBlocks()
        if (radius <= 0) {
            return
        }
        val step = config.cellSize().coerceAtLeast(1)
        val center = player.blockPosition()
        val cameraPos = player.getEyePosition()
        val lineBuffer = bufferSource.getBuffer(RenderTypes.LINES)
        val pos = BlockPos.MutableBlockPos()

        val minX = center.x - radius
        val maxX = center.x + radius
        val minY = center.y - radius
        val maxY = center.y + radius
        val minZ = center.z - radius
        val maxZ = center.z + radius

        var x = minX
        while (x <= maxX) {
            var y = minY
            while (y <= maxY) {
                var z = minZ
                while (z <= maxZ) {
                    pos.set(x, y, z)
                    val state = level.getBlockState(pos)
                    val color = colorForLens(lens, level, pos, state)
                    if (color == null) {
                        z += step
                        continue
                    }
                    val box = AABB(pos).inflate(0.002).move(
                        -cameraPos.x,
                        -cameraPos.y,
                        -cameraPos.z
                    )
                    renderLineBox(
                        poseStack,
                        lineBuffer,
                        box,
                        color.r,
                        color.g,
                        color.b,
                        color.a
                    )
                    z += step
                }
                y += step
            }
            x += step
        }
    }

    private fun activeLens(player: Player): SystemLens? {
        val mainItem = player.mainHandItem.item
        if (mainItem is SystemGlassesItem) {
            return mainItem.lens()
        }
        val offItem = player.offhandItem.item
        if (offItem is SystemGlassesItem) {
            return offItem.lens()
        }
        return null
    }

    private fun colorForLens(
        lens: SystemLens,
        level: Level,
        pos: BlockPos,
        state: BlockState
    ): OniLensOverlayPalette.Color? {
        return when (lens) {
            SystemLens.THERMAL -> thermalColor(level, pos)
            SystemLens.GAS, SystemLens.ATMOSPHERE -> gasColor(level, pos, state)
            SystemLens.LIQUID -> liquidColor(level, pos, state)
            SystemLens.POWER,
            SystemLens.STRESS,
            SystemLens.RESEARCH,
            SystemLens.CONSTRUCTION -> null
        }
    }

    private fun thermalColor(level: Level, pos: BlockPos): OniLensOverlayPalette.Color? {
        val entity = level.getBlockEntity(pos) as? OniElementBlockEntity ?: return null
        if (entity.totalMass() <= 0.0) {
            return null
        }
        return OniLensOverlayPalette.thermalColor(entity.averageTemperatureK())
    }

    private fun gasColor(
        level: Level,
        pos: BlockPos,
        state: BlockState
    ): OniLensOverlayPalette.Color? {
        val gas = OniMatterAccess.gasSpec(state) ?: return null
        val alpha = lensAlpha(level, pos)
        return OniLensOverlayPalette.gasColor(gas.id, alpha)
    }

    private fun liquidColor(
        level: Level,
        pos: BlockPos,
        state: BlockState
    ): OniLensOverlayPalette.Color? {
        val liquidId = OniMatterAccess.liquidId(state) ?: return null
        val alpha = lensAlpha(level, pos)
        return OniLensOverlayPalette.liquidColor(liquidId, alpha)
    }

    private fun lensAlpha(level: Level, pos: BlockPos): Float {
        val entity = level.getBlockEntity(pos) as? OniElementBlockEntity ?: return 0.35f
        val mass = entity.totalMass()
        if (mass <= 0.0) {
            return 0.2f
        }
        val normalized = (mass / 1000.0).coerceIn(0.0, 1.0)
        return (0.2 + normalized * 0.6).toFloat()
    }

    private fun renderLineBox(
        poseStack: PoseStack,
        buffer: com.mojang.blaze3d.vertex.VertexConsumer,
        box: AABB,
        r: Float,
        g: Float,
        b: Float,
        a: Float
    ) {
        val pose = poseStack.last().pose()
        line(buffer, pose, box.minX, box.minY, box.minZ, box.maxX, box.minY, box.minZ, r, g, b, a)
        line(buffer, pose, box.minX, box.minY, box.minZ, box.minX, box.maxY, box.minZ, r, g, b, a)
        line(buffer, pose, box.minX, box.minY, box.minZ, box.minX, box.minY, box.maxZ, r, g, b, a)

        line(buffer, pose, box.maxX, box.maxY, box.maxZ, box.minX, box.maxY, box.maxZ, r, g, b, a)
        line(buffer, pose, box.maxX, box.maxY, box.maxZ, box.maxX, box.minY, box.maxZ, r, g, b, a)
        line(buffer, pose, box.maxX, box.maxY, box.maxZ, box.maxX, box.maxY, box.minZ, r, g, b, a)

        line(buffer, pose, box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.minZ, r, g, b, a)
        line(buffer, pose, box.minX, box.maxY, box.minZ, box.minX, box.maxY, box.maxZ, r, g, b, a)

        line(buffer, pose, box.maxX, box.minY, box.minZ, box.maxX, box.maxY, box.minZ, r, g, b, a)
        line(buffer, pose, box.maxX, box.minY, box.minZ, box.maxX, box.minY, box.maxZ, r, g, b, a)

        line(buffer, pose, box.minX, box.minY, box.maxZ, box.maxX, box.minY, box.maxZ, r, g, b, a)
        line(buffer, pose, box.minX, box.minY, box.maxZ, box.minX, box.maxY, box.maxZ, r, g, b, a)
    }

    private fun line(
        buffer: com.mojang.blaze3d.vertex.VertexConsumer,
        pose: Matrix4f,
        x1: Double,
        y1: Double,
        z1: Double,
        x2: Double,
        y2: Double,
        z2: Double,
        r: Float,
        g: Float,
        b: Float,
        a: Float
    ) {
        val nx = (x2 - x1).toFloat()
        val ny = (y2 - y1).toFloat()
        val nz = (z2 - z1).toFloat()
        val len = kotlin.math.sqrt(nx * nx + ny * ny + nz * nz).coerceAtLeast(1.0e-6f)
        val nnx = nx / len
        val nny = ny / len
        val nnz = nz / len
        buffer.addVertex(pose, x1.toFloat(), y1.toFloat(), z1.toFloat())
            .setColor(r, g, b, a)
            .setNormal(nnx, nny, nnz)
        buffer.addVertex(pose, x2.toFloat(), y2.toFloat(), z2.toFloat())
            .setColor(r, g, b, a)
            .setNormal(nnx, nny, nnz)
    }
}
