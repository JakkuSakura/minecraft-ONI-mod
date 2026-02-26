package mconi.common.sim

import mconi.common.sim.model.GasSpecies
import mconi.common.sim.model.LayerProperty
import mconi.common.sim.model.OniCellState
import mconi.common.sim.model.PressureBand
import mconi.common.sim.model.SystemLens

object OniSystemInspector {
    @JvmStatic
    fun inspect(runtime: OniSimulationRuntime, systemLens: SystemLens, cell: OniCellState): List<LayerProperty> {
        return when (systemLens) {
            SystemLens.ATMOSPHERE -> atmosphereLayers(cell)
            SystemLens.FLUID -> fluidLayers(cell)
            SystemLens.THERMAL -> thermalLayers(cell)
            SystemLens.OXYGEN -> oxygenLayers(cell)
            SystemLens.POWER -> powerLayers(runtime)
            SystemLens.STRESS -> stressLayers(runtime)
            SystemLens.RESEARCH -> researchLayers(runtime)
            SystemLens.CONSTRUCTION -> constructionLayers(runtime)
        }
    }

    private fun atmosphereLayers(cell: OniCellState): List<LayerProperty> {
        return listOf(
            LayerProperty("matter", "occupancy", cell.occupancyState().name),
            LayerProperty("pressure", "kPa", "%.3f".format(cell.pressureKpa())),
            LayerProperty("pressure", "band", PressureBand.fromKpa(cell.pressureKpa()).name),
            LayerProperty("gas", "O2_kg", "%.3f".format(cell.gasMassKg(GasSpecies.O2))),
            LayerProperty("gas", "CO2_kg", "%.3f".format(cell.gasMassKg(GasSpecies.CO2))),
            LayerProperty("gas", "H2_kg", "%.3f".format(cell.gasMassKg(GasSpecies.H2))),
            LayerProperty("gas", "total_kg", "%.3f".format(cell.totalGasMassKg())),
        )
    }

    private fun fluidLayers(cell: OniCellState): List<LayerProperty> {
        return listOf(
            LayerProperty("fluid", "species", cell.fluidSpecies().name),
            LayerProperty("fluid", "mass_kg", "%.3f".format(cell.fluidMassKg())),
            LayerProperty("fluid", "boiling_candidate", (cell.temperatureK() > 373.15).toString()),
        )
    }

    private fun thermalLayers(cell: OniCellState): List<LayerProperty> {
        val zone = when {
            cell.temperatureK() >= 350.0 -> "HOT"
            cell.temperatureK() <= 265.0 -> "COLD"
            else -> "STABLE"
        }

        return listOf(
            LayerProperty("thermal", "temperature_k", "%.3f".format(cell.temperatureK())),
            LayerProperty("thermal", "zone", zone),
            LayerProperty("thermal", "overheated", cell.overheated().toString()),
        )
    }

    private fun oxygenLayers(cell: OniCellState): List<LayerProperty> {
        return listOf(
            LayerProperty("oxygen", "o2_fraction", "%.4f".format(cell.o2Fraction())),
            LayerProperty("oxygen", "co2_fraction", "%.4f".format(cell.co2Fraction())),
            LayerProperty("oxygen", "breathing_band", cell.breathingBand().name),
        )
    }

    private fun powerLayers(runtime: OniSimulationRuntime): List<LayerProperty> {
        val power = runtime.powerState()
        return listOf(
            LayerProperty("power", "generation_w", "%.2f".format(power.generationW())),
            LayerProperty("power", "demand_w", "%.2f".format(power.demandW())),
            LayerProperty("power", "stored_j", "%.2f".format(power.storedEnergyJ())),
            LayerProperty("power", "tripped", power.tripped().toString()),
        )
    }

    private fun stressLayers(runtime: OniSimulationRuntime): List<LayerProperty> {
        return listOf(
            LayerProperty("stress", "score", "%.2f".format(runtime.stressState().score())),
        )
    }

    private fun researchLayers(runtime: OniSimulationRuntime): List<LayerProperty> {
        return listOf(
            LayerProperty("research", "unlocked_count", runtime.researchState().unlockedCount().toString()),
            LayerProperty("research", "nodes", runtime.researchState().unlockedNodes().toString()),
        )
    }

    private fun constructionLayers(runtime: OniSimulationRuntime): List<LayerProperty> {
        return listOf(
            LayerProperty("construction", "queue_size", runtime.constructionState().activeCount().toString()),
        )
    }
}
