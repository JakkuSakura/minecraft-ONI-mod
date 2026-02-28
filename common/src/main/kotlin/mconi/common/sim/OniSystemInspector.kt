package mconi.common.sim

import mconi.common.element.OniElements
import mconi.common.sim.model.LayerProperty
import mconi.common.sim.model.OniBlockData
import mconi.common.sim.model.PressureBand
import mconi.common.sim.model.SystemLens
import net.minecraft.world.entity.player.Player

object OniSystemInspector {
    @JvmStatic
    fun inspect(
        runtime: OniSimulationRuntime,
        systemLens: SystemLens,
        cell: OniBlockData,
        player: Player?
    ): List<LayerProperty> {
        return when (systemLens) {
            SystemLens.ATMOSPHERE -> atmosphereLayers(cell)
            SystemLens.LIQUID -> liquidLayers(cell)
            SystemLens.THERMAL -> thermalLayers(cell)
            SystemLens.GAS -> gasLayers(cell)
            SystemLens.POWER -> powerLayers(runtime)
            SystemLens.STRESS -> stressLayers(runtime, player)
            SystemLens.RESEARCH -> researchLayers(runtime)
            SystemLens.CONSTRUCTION -> constructionLayers(runtime)
        }
    }

    private fun atmosphereLayers(cell: OniBlockData): List<LayerProperty> {
        return listOf(
            LayerProperty("matter", "occupancy", cell.occupancyState().name),
            LayerProperty("pressure", "kPa", "%.3f".format(cell.pressureKpa())),
            LayerProperty("pressure", "band", PressureBand.fromKpa(cell.pressureKpa()).name),
            LayerProperty("gas", "O2_kg", "%.3f".format(cell.gasMassKg(OniElements.GAS_OXYGEN))),
            LayerProperty("gas", "CO2_kg", "%.3f".format(cell.gasMassKg(OniElements.GAS_CARBON_DIOXIDE))),
            LayerProperty("gas", "H2_kg", "%.3f".format(cell.gasMassKg(OniElements.GAS_HYDROGEN))),
            LayerProperty("gas", "total_kg", "%.3f".format(cell.totalGasMassKg())),
        )
    }

    private fun liquidLayers(cell: OniBlockData): List<LayerProperty> {
        return listOf(
            LayerProperty("liquid", "id", cell.liquidId()),
            LayerProperty("liquid", "mass_kg", "%.3f".format(cell.liquidMassKg())),
            LayerProperty("liquid", "boiling_candidate", (cell.temperatureK() > 373.15).toString()),
        )
    }

    private fun thermalLayers(cell: OniBlockData): List<LayerProperty> {
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

    private fun gasLayers(cell: OniBlockData): List<LayerProperty> {
        val layers: MutableList<LayerProperty> = ArrayList()
        for (species in OniElements.GASES) {
            layers.add(
                LayerProperty(
                    "gas",
                    "${species.symbol.lowercase()}_fraction",
                    "%.4f".format(cell.gasFraction(species))
                )
            )
        }
        layers.add(LayerProperty("gas", "breathing_band", cell.breathingBand().name))
        return layers
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

    private fun stressLayers(runtime: OniSimulationRuntime, player: Player?): List<LayerProperty> {
        val score = if (player != null) {
            runtime.stressState().score(player)
        } else {
            runtime.stressState().score()
        }
        return listOf(
            LayerProperty("stress", "score", "%.2f".format(score)),
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
