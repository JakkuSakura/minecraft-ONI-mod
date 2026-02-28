package mconi.common.sim.subsystem

import mconi.common.block.OniBlockFactory
import mconi.common.element.OniElements
import mconi.common.world.OniMatterAccess
import mconi.common.world.OniWorldScan
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Blocks

class ThermalSystem : OniSystem {
    override fun id(): String = "thermal"

    override fun run(context: SystemContext) {
        val level = context.level()
        val config = context.config()
        val radius = config.worldSampleRadiusBlocks()
        val cellSize = config.cellSize()
        val positions = OniWorldScan.positionsAroundPlayers(level, radius, cellSize)

        val tempByPos: MutableMap<BlockPos, Double> = HashMap()
        val conductivityByPos: MutableMap<BlockPos, Double> = HashMap()
        val liquidIdByPos: MutableMap<BlockPos, String> = HashMap()

        for (pos in positions) {
            val state = level.getBlockState(pos)
            val gas = OniMatterAccess.gasSpec(state)
            val liquid = OniMatterAccess.liquidId(state)
            val entity = OniMatterAccess.matterEntity(level, pos) ?: continue
            tempByPos[pos] = entity.temperatureK()
            conductivityByPos[pos] = conductivityFor(state, gas, liquid, entity)
            if (liquid != null) {
                liquidIdByPos[pos] = liquid
            }
        }

        val deltas: MutableMap<BlockPos, Double> = HashMap()
        for (pos in tempByPos.keys) {
            val temp = tempByPos.getValue(pos)
            val conductivity = conductivityByPos[pos] ?: 0.0
            if (conductivity <= 0.0) {
                continue
            }
            for (neighbor in neighborsOf(pos)) {
                val neighborTemp = tempByPos[neighbor] ?: continue
                if (pos.x > neighbor.x ||
                    (pos.x == neighbor.x && pos.y > neighbor.y) ||
                    (pos.x == neighbor.x && pos.y == neighbor.y && pos.z > neighbor.z)
                ) {
                    continue
                }
                val neighborConductivity = conductivityByPos[neighbor] ?: 0.0
                val coupling = minOf(conductivity, neighborConductivity)
                if (coupling <= 0.0) {
                    continue
                }
                val delta = (neighborTemp - temp) * coupling
                deltas[pos] = (deltas[pos] ?: 0.0) + delta
                deltas[neighbor] = (deltas[neighbor] ?: 0.0) - delta
            }
        }

        for ((pos, delta) in deltas) {
            val entity = OniMatterAccess.matterEntity(level, pos) ?: continue
            val baseTemp = tempByPos[pos] ?: entity.temperatureK()
            entity.setTemperatureK(baseTemp + delta)
        }

        for (pos in liquidIdByPos.keys) {
            val entity = OniMatterAccess.matterEntity(level, pos) ?: continue
            val liquidId = liquidIdByPos[pos] ?: continue
            if ((liquidId == OniElements.LIQUID_WATER || liquidId == OniElements.LIQUID_POLLUTED_WATER) &&
                entity.mass() > 0.0 &&
                entity.temperatureK() > BOIL_TEMPERATURE_K
            ) {
                val nextMass = entity.mass() - BOIL_OFF_PER_STEP
                if (nextMass <= 0.0) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2)
                } else {
                    entity.setMass(nextMass)
                }
            }
        }
    }

    private fun conductivityFor(
        state: net.minecraft.world.level.block.state.BlockState,
        gas: OniElements.GasSpec?,
        liquidId: String?,
        entity: mconi.common.block.entity.OniMatterBlockEntity
    ): Double {
        val blockCoefficient = OniBlockFactory.blockConductivityCoefficient(state)
        val base = when {
            liquidId != null -> liquidConductivity(liquidId)
            gas != null -> gasConductivity(gas, entity.mass())
            else -> DEFAULT_VACUUM_CONDUCTIVITY
        }
        return (base * blockCoefficient).coerceIn(0.0, MAX_CONDUCTIVITY)
    }

    private fun liquidConductivity(liquidId: String): Double {
        val spec = OniElements.liquidSpec(liquidId)
        val conductivity = spec?.thermalConductivityWmK ?: DEFAULT_LIQUID_CONDUCTIVITY
        return (conductivity * CONDUCTIVITY_SCALE).coerceIn(0.0, MAX_CONDUCTIVITY)
    }

    private fun gasConductivity(spec: OniElements.GasSpec, mass: Double): Double {
        if (mass <= 0.0) {
            return DEFAULT_GAS_CONDUCTIVITY
        }
        return (spec.thermalConductivity * CONDUCTIVITY_SCALE).coerceIn(0.0, MAX_CONDUCTIVITY)
    }

    private fun neighborsOf(coordinate: BlockPos): List<BlockPos> {
        return listOf(
            BlockPos(coordinate.x + 1, coordinate.y, coordinate.z),
            BlockPos(coordinate.x - 1, coordinate.y, coordinate.z),
            BlockPos(coordinate.x, coordinate.y + 1, coordinate.z),
            BlockPos(coordinate.x, coordinate.y - 1, coordinate.z),
            BlockPos(coordinate.x, coordinate.y, coordinate.z + 1),
            BlockPos(coordinate.x, coordinate.y, coordinate.z - 1),
        )
    }

    companion object {
        private const val BOIL_TEMPERATURE_K = 373.15
        private const val BOIL_OFF_PER_STEP = 1.0
        private const val CONDUCTIVITY_SCALE = 0.01
        private const val MAX_CONDUCTIVITY = 0.2
        private const val DEFAULT_LIQUID_CONDUCTIVITY = 0.08
        private const val DEFAULT_GAS_CONDUCTIVITY = 0.04
        private const val DEFAULT_VACUUM_CONDUCTIVITY = 0.01
    }
}
