package conservecraft.common.sim.conduit

import net.minecraft.core.BlockPos
import kotlin.math.abs

object OniConduitFlow {
    data class Contents(
        val elementId: String?,
        val mass: Double,
        val temperatureK: Double
    )

    data class FlowConfig(
        val maxMass: Double,
        val movePerStep: Double
    )

    data class FlowResult(
        val contentsByPos: Map<BlockPos, Contents>
    )

    fun step(
        network: List<BlockPos>,
        contentsByPos: Map<BlockPos, Contents>,
        config: FlowConfig
    ): FlowResult {
        if (network.isEmpty()) {
            return FlowResult(contentsByPos)
        }
        val massDelta: MutableMap<BlockPos, Double> = HashMap()
        val incomingMass: MutableMap<BlockPos, Double> = HashMap()
        val incomingEnergy: MutableMap<BlockPos, Double> = HashMap()
        val incomingElement: MutableMap<BlockPos, String?> = HashMap()

        val networkSet = network.toHashSet()
        for (pos in network) {
            val source = contentsByPos[pos] ?: continue
            if (source.mass <= 0.0 || source.elementId == null) {
                continue
            }
            for (neighbor in neighborsOf(pos)) {
                if (!networkSet.contains(neighbor)) {
                    continue
                }
                if (!isOrderedPair(pos, neighbor)) {
                    continue
                }
                val target = contentsByPos[neighbor] ?: continue
                if (target.elementId != null && target.elementId != source.elementId) {
                    continue
                }
                val headA = source.mass / config.maxMass
                val headB = target.mass / config.maxMass
                val diff = headA - headB
                if (abs(diff) < 0.0001) {
                    continue
                }
                val desired = diff * config.movePerStep
                val transfer = when {
                    desired > 0.0 -> minOf(desired, source.mass, config.maxMass - target.mass)
                    else -> -minOf(-desired, target.mass, config.maxMass - source.mass)
                }
                if (transfer == 0.0) {
                    continue
                }
                if (transfer > 0.0) {
                    applyTransfer(pos, neighbor, source, transfer, massDelta, incomingMass, incomingEnergy, incomingElement)
                } else {
                    applyTransfer(neighbor, pos, target, -transfer, massDelta, incomingMass, incomingEnergy, incomingElement)
                }
            }
        }

        val updated: MutableMap<BlockPos, Contents> = HashMap(contentsByPos.size)
        for (pos in network) {
            val base = contentsByPos[pos] ?: Contents(null, 0.0, 293.15)
            val delta = massDelta[pos] ?: 0.0
            val newMass = (base.mass + delta).coerceAtLeast(0.0)
            if (newMass <= 0.0) {
                updated[pos] = Contents(null, 0.0, base.temperatureK)
                continue
            }
            val incoming = incomingMass[pos] ?: 0.0
            val baseEnergy = (newMass - incoming).coerceAtLeast(0.0) * base.temperatureK
            val addedEnergy = incomingEnergy[pos] ?: 0.0
            val newTemp = (baseEnergy + addedEnergy) / newMass
            val elementId = base.elementId ?: incomingElement[pos]
            if (elementId == null) {
                updated[pos] = Contents(null, 0.0, base.temperatureK)
                continue
            }
            updated[pos] = Contents(elementId, newMass, newTemp)
        }
        return FlowResult(updated)
    }

    private fun applyTransfer(
        sourcePos: BlockPos,
        targetPos: BlockPos,
        source: Contents,
        transfer: Double,
        massDelta: MutableMap<BlockPos, Double>,
        incomingMass: MutableMap<BlockPos, Double>,
        incomingEnergy: MutableMap<BlockPos, Double>,
        incomingElement: MutableMap<BlockPos, String?>
    ) {
        massDelta[sourcePos] = (massDelta[sourcePos] ?: 0.0) - transfer
        massDelta[targetPos] = (massDelta[targetPos] ?: 0.0) + transfer
        incomingMass[targetPos] = (incomingMass[targetPos] ?: 0.0) + transfer
        incomingEnergy[targetPos] = (incomingEnergy[targetPos] ?: 0.0) + transfer * source.temperatureK
        if (incomingElement[targetPos] == null) {
            incomingElement[targetPos] = source.elementId
        }
    }

    private fun neighborsOf(pos: BlockPos): List<BlockPos> {
        return listOf(
            BlockPos(pos.x + 1, pos.y, pos.z),
            BlockPos(pos.x - 1, pos.y, pos.z),
            BlockPos(pos.x, pos.y + 1, pos.z),
            BlockPos(pos.x, pos.y - 1, pos.z),
            BlockPos(pos.x, pos.y, pos.z + 1),
            BlockPos(pos.x, pos.y, pos.z - 1)
        )
    }

    private fun isOrderedPair(a: BlockPos, b: BlockPos): Boolean {
        return a.x < b.x ||
            (a.x == b.x && a.y < b.y) ||
            (a.x == b.x && a.y == b.y && a.z < b.z)
    }
}
