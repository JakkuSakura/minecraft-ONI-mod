package mconi.common.sim.subsystem

import mconi.common.element.OniElements
import mconi.common.sim.model.OccupancyState
import mconi.common.sim.model.OniBlockData
import mconi.common.world.OniChunkDataAccess
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel

class ThermalSubsystem : SimulationSubsystem {
    override fun id(): String = "thermal"

    override fun run(context: SimulationContext) {
        val level = context.level()
        val entries = OniChunkDataAccess.blockEntries(level)
        val tempByPos: MutableMap<BlockPos, Double> = HashMap(entries.size)
        val occupancyByPos: MutableMap<BlockPos, OccupancyState> = HashMap(entries.size)
        val lavaByPos: MutableSet<BlockPos> = HashSet(entries.size)
        val conductivityByPos: MutableMap<BlockPos, Double> = HashMap(entries.size)

        for (entry in entries) {
            val cell = entry.data
            tempByPos[entry.pos] = cell.temperatureK()
            occupancyByPos[entry.pos] = cell.occupancyState()
            conductivityByPos[entry.pos] = conductivityFor(level, entry.pos, cell)
            if (cell.liquidId() == OniElements.LIQUID_LAVA && cell.liquidMassKg() > 0.0) {
                lavaByPos.add(entry.pos)
            }
        }

        val deltas: MutableMap<BlockPos, Double> = HashMap(entries.size)
        for (entry in entries) {
            val coordinate = entry.pos
            val temp = tempByPos.getValue(coordinate)
            val occupancy = occupancyByPos.getValue(coordinate)

            if (occupancy == OccupancyState.VACUUM || occupancy == OccupancyState.VOID) {
                val ambient = (260.0 - temp) * 0.02
                deltas[coordinate] = (deltas[coordinate] ?: 0.0) + ambient
            }

            if (lavaByPos.contains(coordinate)) {
                deltas[coordinate] = (deltas[coordinate] ?: 0.0) + 0.8
            }
        }

        for (entry in entries) {
            val coordinate = entry.pos
            val temp = tempByPos.getValue(coordinate)
            val occupancy = occupancyByPos.getValue(coordinate)
            val conductivity = conductivityByPos.getValue(coordinate)
            if (conductivity <= 0.0) {
                continue
            }

            for (neighbor in neighborsOf(coordinate)) {
                val neighborTemp = tempByPos[neighbor] ?: continue
                val neighborOccupancy = occupancyByPos[neighbor] ?: continue
                if (coordinate.x > neighbor.x ||
                    (coordinate.x == neighbor.x && coordinate.y > neighbor.y) ||
                    (coordinate.x == neighbor.x && coordinate.y == neighbor.y && coordinate.z > neighbor.z)
                ) {
                    continue
                }
                val neighborConductivity = conductivityByPos[neighbor] ?: conductivityFor(neighborOccupancy)
                val coupling = minOf(conductivity, neighborConductivity)
                if (coupling <= 0.0) {
                    continue
                }
                val delta = (neighborTemp - temp) * coupling
                deltas[coordinate] = (deltas[coordinate] ?: 0.0) + delta
                deltas[neighbor] = (deltas[neighbor] ?: 0.0) - delta
            }
        }

        for (entry in entries) {
            val coordinate = entry.pos
            val baseTemp = tempByPos.getValue(coordinate)
            val nextTemp = baseTemp + (deltas[coordinate] ?: 0.0)
            entry.data.setTemperatureK(nextTemp)
        }

        for (entry in entries) {
            val cell = entry.data
            val liquidId = cell.liquidId()
            if ((liquidId == OniElements.LIQUID_WATER || liquidId == OniElements.LIQUID_POLLUTED_WATER) &&
                cell.liquidMassKg() > 0.0 &&
                cell.temperatureK() > BOIL_TEMPERATURE_K
            ) {
                val nextMass = cell.liquidMassKg() - BOIL_OFF_KG_PER_STEP
                if (nextMass <= 0.0) {
                    cell.setLiquidState(OniElements.LIQUID_NONE, 0.0)
                } else {
                    cell.setLiquidState(liquidId, nextMass)
                }
            }
        }

        for (entry in OniChunkDataAccess.blockEntries(level)) {
            val cell = entry.data
            val overheated = cell.temperatureK() >= OVERHEAT_THRESHOLD_K && cell.occupancyState() == OccupancyState.SOLID
            cell.setOverheated(overheated)
        }
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
        private const val OVERHEAT_THRESHOLD_K = 450.0
    }

    private fun conductivityFor(level: ServerLevel, pos: BlockPos, cell: OniBlockData): Double {
        return when (cell.occupancyState()) {
            OccupancyState.LIQUID -> liquidConductivity(cell)
            OccupancyState.GAS -> gasConductivity(cell)
            OccupancyState.SOLID -> solidConductivity(level, pos)
            OccupancyState.VACUUM -> DEFAULT_VACUUM_CONDUCTIVITY
            OccupancyState.VOID -> 0.0
        }
    }

    private fun conductivityFor(occupancy: OccupancyState): Double {
        return when (occupancy) {
            OccupancyState.LIQUID -> DEFAULT_LIQUID_CONDUCTIVITY
            OccupancyState.GAS -> DEFAULT_GAS_CONDUCTIVITY
            OccupancyState.SOLID -> DEFAULT_SOLID_CONDUCTIVITY
            OccupancyState.VACUUM -> DEFAULT_VACUUM_CONDUCTIVITY
            OccupancyState.VOID -> 0.0
        }
    }

    private fun solidConductivity(level: ServerLevel, pos: BlockPos): Double {
        val block = level.getBlockState(pos).block
        return DEFAULT_SOLID_CONDUCTIVITY
    }

    private fun liquidConductivity(cell: OniBlockData): Double {
        val spec = OniElements.liquidSpec(cell.liquidId())
        val conductivity = spec?.thermalConductivityWmK ?: return DEFAULT_LIQUID_CONDUCTIVITY
        return (conductivity * CONDUCTIVITY_SCALE).coerceIn(0.0, MAX_CONDUCTIVITY)
    }

    private fun gasConductivity(cell: OniBlockData): Double {
        val total = cell.totalGasMassKg()
        if (total <= 0.0) {
            return DEFAULT_GAS_CONDUCTIVITY
        }
        var sum = 0.0
        for (species in OniElements.GASES) {
            val mass = cell.gasMassKg(species)
            if (mass <= 0.0) {
                continue
            }
            sum += mass * species.thermalConductivity
        }
        val average = sum / total
        return (average * CONDUCTIVITY_SCALE).coerceIn(0.0, MAX_CONDUCTIVITY)
    }

    companion object {
        private const val OVERHEAT_THRESHOLD_K = 450.0
        private const val BOIL_TEMPERATURE_K = 373.15
        private const val BOIL_OFF_KG_PER_STEP = 1.0
        private const val CONDUCTIVITY_SCALE = 0.01
        private const val MAX_CONDUCTIVITY = 0.2
        private const val DEFAULT_SOLID_CONDUCTIVITY = 0.02
        private const val DEFAULT_LIQUID_CONDUCTIVITY = 0.08
        private const val DEFAULT_GAS_CONDUCTIVITY = 0.04
        private const val DEFAULT_VACUUM_CONDUCTIVITY = 0.01
    }
}
