package mconi.common.sim.subsystem

import mconi.common.element.OniElements
import mconi.common.sim.model.OccupancyState
import mconi.common.world.OniChunkDataAccess
import net.minecraft.core.BlockPos

class ThermalSubsystem : SimulationSubsystem {
    override fun id(): String = "thermal"

    override fun run(context: SimulationContext) {
        val level = context.level()
        val deltas: MutableMap<BlockPos, Double> = HashMap()
        for (entry in OniChunkDataAccess.blockEntries(level)) {
            val coordinate = entry.pos
            val cell = entry.data
            val temp = cell.temperatureK()
            val occupancy = cell.occupancyState()
            var next = temp

            if (occupancy == OccupancyState.VACUUM || occupancy == OccupancyState.VOID) {
                next += (260.0 - temp) * 0.02
            }

            if (cell.liquidId() == OniElements.LIQUID_LAVA && cell.liquidMassKg() > 0.0) {
                next += 0.8
            }

            val conduction = when (occupancy) {
                OccupancyState.LIQUID -> 0.08
                OccupancyState.GAS -> 0.04
                OccupancyState.SOLID -> 0.02
                OccupancyState.VACUUM -> 0.01
                OccupancyState.VOID -> 0.0
            }

            if (conduction > 0.0) {
                for (neighbor in neighborsOf(coordinate)) {
                    val other = OniChunkDataAccess.get(level, neighbor) ?: continue
                    val delta = (other.temperatureK() - temp) * conduction
                    deltas[coordinate] = (deltas[coordinate] ?: 0.0) + delta
                }
            }

            cell.setTemperatureK(next)
        }

        for ((coordinate, delta) in deltas) {
            val cell = OniChunkDataAccess.getOrCreate(level, coordinate)
            cell.setTemperatureK(cell.temperatureK() + delta)
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
}
