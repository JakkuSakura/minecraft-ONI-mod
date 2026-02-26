package mconi.common.world

import mconi.common.sim.OniServices
import mconi.common.sim.model.GasSpecies
import mconi.common.sim.model.OniCellCoordinate
import mconi.common.sim.model.OniCellState
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.storage.DimensionDataStorage
import java.util.concurrent.atomic.AtomicBoolean

object OniSimulationPersistence {
    private val loaded = AtomicBoolean(false)

    @JvmStatic
    fun ensureLoaded(level: ServerLevel) {
        if (!loaded.compareAndSet(false, true)) {
            return
        }
        val storage: DimensionDataStorage = level.dataStorage
        val data = storage.computeIfAbsent(OniSimulationStorage.TYPE)
        applyToRuntime(data)
    }

    @JvmStatic
    fun save(level: ServerLevel) {
        val storage: DimensionDataStorage = level.dataStorage
        val data = storage.computeIfAbsent(OniSimulationStorage.TYPE)
        data.replaceCells(captureFromRuntime())
        storage.set(OniSimulationStorage.TYPE, data)
    }

    private fun applyToRuntime(data: OniSimulationStorage) {
        val grid = OniServices.simulationRuntime().grid()
        grid.clear()
        for (entry in data.cells()) {
            val coord = OniCellCoordinate(entry.x, entry.y, entry.z)
            val cell: OniCellState = grid.getOrCreateCellAtCoordinate(coord)
            cell.setOccupancyState(entry.occupancy)
            cell.setFluidState(entry.fluid, entry.fluidMass)
            cell.setTemperatureK(entry.temperatureK)
            cell.setPressureKpa(entry.pressureKpa)
            cell.setGasMassKg(GasSpecies.O2, entry.o2Mass)
            cell.setGasMassKg(GasSpecies.CO2, entry.co2Mass)
            cell.setGasMassKg(GasSpecies.H2, entry.h2Mass)
            cell.setOverheated(entry.overheated)
        }
    }

    private fun captureFromRuntime(): List<OniSimulationStorage.CellEntry> {
        val grid = OniServices.simulationRuntime().grid()
        val entries = ArrayList<OniSimulationStorage.CellEntry>()
        for ((coord, cell) in grid.cellEntries()) {
            entries.add(
                OniSimulationStorage.CellEntry(
                    coord.cellX(),
                    coord.cellY(),
                    coord.cellZ(),
                    cell.occupancyState(),
                    cell.fluidSpecies(),
                    cell.fluidMassKg(),
                    cell.temperatureK(),
                    cell.pressureKpa(),
                    cell.gasMassKg(GasSpecies.O2),
                    cell.gasMassKg(GasSpecies.CO2),
                    cell.gasMassKg(GasSpecies.H2),
                    cell.overheated()
                )
            )
        }
        return entries
    }
}
