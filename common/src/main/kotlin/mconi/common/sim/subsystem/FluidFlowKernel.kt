package mconi.common.sim.subsystem

import mconi.common.element.OniElements
import net.minecraft.core.BlockPos
import kotlin.math.abs

object FluidFlowKernel {
    enum class Phase {
        EMPTY,
        GAS,
        LIQUID,
        SOLID
    }

    data class CellState(
        val phase: Phase,
        val elementId: String?,
        val mass: Double,
        val temperatureK: Double
    )

    data class FlowConfig(
        val maxTransferPerStep: Double,
        val referenceMass: Double,
        val downwardBias: Double
    )

    fun applyGasFlow(
        cells: Map<BlockPos, CellState>,
        config: FlowConfig
    ): Map<BlockPos, CellState> {
        return applyFlow(cells, config, FlowMode.GAS)
    }

    fun applyLiquidFlow(
        cells: Map<BlockPos, CellState>,
        config: FlowConfig
    ): Map<BlockPos, CellState> {
        return applyFlow(cells, config, FlowMode.LIQUID)
    }

    private enum class FlowMode {
        GAS,
        LIQUID
    }

    private fun applyFlow(
        cells: Map<BlockPos, CellState>,
        config: FlowConfig,
        mode: FlowMode
    ): Map<BlockPos, CellState> {
        if (cells.isEmpty()) {
            return cells
        }

        val maxTransfer = config.maxTransferPerStep.coerceAtLeast(0.0)
        if (maxTransfer <= 0.0) {
            return cells
        }

        val massDelta: MutableMap<BlockPos, Double> = HashMap()
        val incomingMass: MutableMap<BlockPos, Double> = HashMap()
        val incomingEnergy: MutableMap<BlockPos, Double> = HashMap()
        val incomingElement: MutableMap<BlockPos, String> = HashMap()
        val swaps: MutableMap<BlockPos, BlockPos> = HashMap()

        val positions = cells.keys.toList()
        for (pos in positions) {
            val cell = cells[pos] ?: continue
            for (neighbor in neighborsOf(pos)) {
                val other = cells[neighbor] ?: continue
                if (!isOrderedPair(pos, neighbor)) {
                    continue
                }
                if (swaps.containsKey(pos) || swaps.containsKey(neighbor)) {
                    continue
                }
                if (cell.phase == Phase.SOLID || other.phase == Phase.SOLID) {
                    continue
                }
                if (mode == FlowMode.GAS && (cell.phase == Phase.LIQUID || other.phase == Phase.LIQUID)) {
                    continue
                }

                val headA = headFor(cell, pos, neighbor, config, mode)
                val headB = headFor(other, neighbor, pos, config, mode)
                val diff = headA - headB
                if (abs(diff) < 0.0001) {
                    continue
                }

                if (diff > 0.0) {
                    handleTransfer(
                        sourcePos = pos,
                        source = cell,
                        targetPos = neighbor,
                        target = other,
                        diff = diff,
                        maxTransfer = maxTransfer,
                        config = config,
                        mode = mode,
                        massDelta = massDelta,
                        incomingMass = incomingMass,
                        incomingEnergy = incomingEnergy,
                        incomingElement = incomingElement,
                        swaps = swaps
                    )
                } else {
                    handleTransfer(
                        sourcePos = neighbor,
                        source = other,
                        targetPos = pos,
                        target = cell,
                        diff = -diff,
                        maxTransfer = maxTransfer,
                        config = config,
                        mode = mode,
                        massDelta = massDelta,
                        incomingMass = incomingMass,
                        incomingEnergy = incomingEnergy,
                        incomingElement = incomingElement,
                        swaps = swaps
                    )
                }
            }
        }

        val updated: MutableMap<BlockPos, CellState> = cells.toMutableMap()
        val swappedPositions: MutableSet<BlockPos> = HashSet()
        for ((pos, other) in swaps) {
            if (swappedPositions.contains(pos) || swappedPositions.contains(other)) {
                continue
            }
            val a = cells[pos] ?: continue
            val b = cells[other] ?: continue
            updated[pos] = b
            updated[other] = a
            swappedPositions.add(pos)
            swappedPositions.add(other)
        }

        for (pos in positions) {
            if (swappedPositions.contains(pos)) {
                continue
            }
            val cell = updated[pos] ?: continue
            if (cell.phase == Phase.SOLID) {
                continue
            }
            if (mode == FlowMode.GAS && cell.phase == Phase.LIQUID) {
                continue
            }
            if (mode == FlowMode.LIQUID && cell.phase == Phase.GAS) {
                continue
            }
            val delta = massDelta[pos] ?: 0.0
            if (delta == 0.0 && incomingEnergy[pos] == null && incomingMass[pos] == null) {
                continue
            }
            val baseMass = cell.mass
            val newMass = (baseMass + delta).coerceAtLeast(0.0)
            if (newMass <= 0.0) {
                updated[pos] = CellState(Phase.EMPTY, null, 0.0, cell.temperatureK)
                continue
            }
            val incoming = incomingMass[pos] ?: 0.0
            val baseEnergy = (newMass - incoming).coerceAtLeast(0.0) * cell.temperatureK
            val addedEnergy = incomingEnergy[pos] ?: 0.0
            val newTemp = (baseEnergy + addedEnergy) / newMass
            val elementId = when (cell.phase) {
                Phase.EMPTY -> incomingElement[pos]
                else -> cell.elementId
            }
            if (elementId == null) {
                updated[pos] = CellState(Phase.EMPTY, null, 0.0, cell.temperatureK)
                continue
            }
            val nextPhase = when (mode) {
                FlowMode.GAS -> Phase.GAS
                FlowMode.LIQUID -> Phase.LIQUID
            }
            updated[pos] = CellState(nextPhase, elementId, newMass, newTemp)
        }

        return updated
    }

    private fun handleTransfer(
        sourcePos: BlockPos,
        source: CellState,
        targetPos: BlockPos,
        target: CellState,
        diff: Double,
        maxTransfer: Double,
        config: FlowConfig,
        mode: FlowMode,
        massDelta: MutableMap<BlockPos, Double>,
        incomingMass: MutableMap<BlockPos, Double>,
        incomingEnergy: MutableMap<BlockPos, Double>,
        incomingElement: MutableMap<BlockPos, String>,
        swaps: MutableMap<BlockPos, BlockPos>
    ) {
        if (source.phase == Phase.EMPTY || source.mass <= 0.0) {
            return
        }
        if (mode == FlowMode.GAS && source.phase != Phase.GAS) {
            return
        }
        if (mode == FlowMode.LIQUID && source.phase != Phase.LIQUID) {
            return
        }

        if (mode == FlowMode.GAS) {
            val sourceSpec = OniElements.parseGas(source.elementId ?: "") ?: return
            if (target.phase == Phase.GAS && target.elementId != source.elementId) {
                val targetSpec = OniElements.parseGas(target.elementId ?: "") ?: return
                if (sourceSpec.molarMass <= targetSpec.molarMass) {
                    return
                }
                swaps[sourcePos] = targetPos
                swaps[targetPos] = sourcePos
                return
            }
            val desired = diff * sourceSpec.flow * config.referenceMass
            val transfer = minOf(desired, maxTransfer, source.mass)
            if (transfer <= 0.0) {
                return
            }
            applyMassTransfer(
                sourcePos,
                targetPos,
                source,
                target,
                transfer,
                massDelta,
                incomingMass,
                incomingEnergy,
                incomingElement
            )
            return
        }

        if (mode == FlowMode.LIQUID) {
            if (target.phase == Phase.LIQUID && target.elementId != source.elementId) {
                return
            }
            if (target.phase == Phase.GAS) {
                swaps[sourcePos] = targetPos
                swaps[targetPos] = sourcePos
                return
            }
            val desired = diff * config.referenceMass
            val transfer = minOf(desired, maxTransfer, source.mass)
            if (transfer <= 0.0) {
                return
            }
            applyMassTransfer(
                sourcePos,
                targetPos,
                source,
                target,
                transfer,
                massDelta,
                incomingMass,
                incomingEnergy,
                incomingElement
            )
        }
    }

    private fun applyMassTransfer(
        sourcePos: BlockPos,
        targetPos: BlockPos,
        source: CellState,
        target: CellState,
        transfer: Double,
        massDelta: MutableMap<BlockPos, Double>,
        incomingMass: MutableMap<BlockPos, Double>,
        incomingEnergy: MutableMap<BlockPos, Double>,
        incomingElement: MutableMap<BlockPos, String>
    ) {
        massDelta[sourcePos] = (massDelta[sourcePos] ?: 0.0) - transfer
        massDelta[targetPos] = (massDelta[targetPos] ?: 0.0) + transfer
        incomingMass[targetPos] = (incomingMass[targetPos] ?: 0.0) + transfer
        incomingEnergy[targetPos] = (incomingEnergy[targetPos] ?: 0.0) + transfer * source.temperatureK
        if (target.phase == Phase.EMPTY) {
            val elementId = source.elementId ?: return
            val existing = incomingElement[targetPos]
            if (existing == null || existing == elementId) {
                incomingElement[targetPos] = elementId
            }
        }
    }

    private fun headFor(
        cell: CellState,
        cellPos: BlockPos,
        neighbor: BlockPos,
        config: FlowConfig,
        mode: FlowMode
    ): Double {
        if (cell.phase == Phase.EMPTY || cell.phase == Phase.SOLID) {
            return 0.0
        }
        if (mode == FlowMode.GAS && cell.phase != Phase.GAS) {
            return 0.0
        }
        if (mode == FlowMode.LIQUID && cell.phase != Phase.LIQUID) {
            return 0.0
        }
        val base = cell.mass / config.referenceMass
        if (mode == FlowMode.LIQUID) {
            val bias = if (neighbor.y < cellPos.y) config.downwardBias else 0.0
            return base + bias
        }
        return base
    }

    private fun neighborsOf(coordinate: BlockPos): List<BlockPos> {
        return listOf(
            BlockPos(coordinate.x + 1, coordinate.y, coordinate.z),
            BlockPos(coordinate.x - 1, coordinate.y, coordinate.z),
            BlockPos(coordinate.x, coordinate.y + 1, coordinate.z),
            BlockPos(coordinate.x, coordinate.y - 1, coordinate.z),
            BlockPos(coordinate.x, coordinate.y, coordinate.z + 1),
            BlockPos(coordinate.x, coordinate.y, coordinate.z - 1)
        )
    }

    private fun isOrderedPair(a: BlockPos, b: BlockPos): Boolean {
        return a.x < b.x ||
            (a.x == b.x && a.y < b.y) ||
            (a.x == b.x && a.y == b.y && a.z < b.z)
    }

}
