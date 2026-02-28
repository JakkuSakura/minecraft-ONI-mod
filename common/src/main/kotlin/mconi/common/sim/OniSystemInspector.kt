package mconi.common.sim

import mconi.common.element.OniElements
import mconi.common.sim.model.LayerProperty
import mconi.common.sim.model.OccupancyState
import mconi.common.sim.model.PressureBand
import mconi.common.sim.model.SystemLens
import mconi.common.world.OniMatterAccess
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

object OniSystemInspector {
    private const val STANDARD_TEMPERATURE_K = 293.15
    private const val STANDARD_PRESSURE_KPA = 101.325
    private const val STANDARD_AIR_DENSITY_KG_PER_M3 = 1.225
    @JvmStatic
    fun inspect(
        runtime: OniSystemRuntime,
        systemLens: SystemLens,
        level: ServerLevel,
        pos: BlockPos,
        player: Player?
    ): List<LayerProperty> {
        return when (systemLens) {
            SystemLens.ATMOSPHERE -> atmosphereLayers(level, pos, runtime)
            SystemLens.LIQUID -> liquidLayers(level, pos)
            SystemLens.THERMAL -> thermalLayers(level, pos)
            SystemLens.GAS -> gasLayers(level, pos)
            SystemLens.POWER -> powerLayers(runtime)
            SystemLens.STRESS -> stressLayers(runtime, player)
            SystemLens.RESEARCH -> researchLayers(runtime)
            SystemLens.CONSTRUCTION -> constructionLayers(runtime)
        }
    }

    private fun atmosphereLayers(level: ServerLevel, pos: BlockPos, runtime: OniSystemRuntime): List<LayerProperty> {
        val state = level.getBlockState(pos)
        val gasSpec = OniMatterAccess.gasSpec(state)
        val entity = OniMatterAccess.matterEntity(level, pos)
        val pressure = if (gasSpec != null && entity != null) {
            gasPressureKpa(entity, runtime.config().cellSize())
        } else {
            0.0
        }
        val occupancy = when {
            gasSpec != null -> OccupancyState.GAS
            OniMatterAccess.liquidId(state) != null -> OccupancyState.LIQUID
            state.isAir -> OccupancyState.VACUUM
            else -> OccupancyState.SOLID
        }
        val o2 = if (gasSpec == OniElements.GAS_OXYGEN) entity?.massKg() ?: 0.0 else 0.0
        val co2 = if (gasSpec == OniElements.GAS_CARBON_DIOXIDE) entity?.massKg() ?: 0.0 else 0.0
        val h2 = if (gasSpec == OniElements.GAS_HYDROGEN) entity?.massKg() ?: 0.0 else 0.0
        val total = o2 + co2 + h2
        return listOf(
            LayerProperty("matter", "occupancy", occupancy.name),
            LayerProperty("pressure", "kPa", "%.3f".format(pressure)),
            LayerProperty("pressure", "band", PressureBand.fromKpa(pressure).name),
            LayerProperty("gas", "O2_kg", "%.3f".format(o2)),
            LayerProperty("gas", "CO2_kg", "%.3f".format(co2)),
            LayerProperty("gas", "H2_kg", "%.3f".format(h2)),
            LayerProperty("gas", "total_kg", "%.3f".format(total)),
        )
    }

    private fun liquidLayers(level: ServerLevel, pos: BlockPos): List<LayerProperty> {
        val state = level.getBlockState(pos)
        val liquidId = OniMatterAccess.liquidId(state) ?: OniElements.LIQUID_NONE
        val entity = OniMatterAccess.matterEntity(level, pos)
        val mass = entity?.massKg() ?: 0.0
        val temp = entity?.temperatureK() ?: 0.0
        return listOf(
            LayerProperty("liquid", "id", liquidId),
            LayerProperty("liquid", "mass_kg", "%.3f".format(mass)),
            LayerProperty("liquid", "boiling_candidate", (temp > 373.15).toString()),
        )
    }

    private fun thermalLayers(level: ServerLevel, pos: BlockPos): List<LayerProperty> {
        val entity = OniMatterAccess.matterEntity(level, pos)
        val temp = entity?.temperatureK() ?: 0.0
        val zone = when {
            temp >= 350.0 -> "HOT"
            temp <= 265.0 -> "COLD"
            else -> "STABLE"
        }

        return listOf(
            LayerProperty("thermal", "temperature_k", "%.3f".format(temp)),
            LayerProperty("thermal", "zone", zone),
            LayerProperty("thermal", "overheated", "false"),
        )
    }

    private fun gasLayers(level: ServerLevel, pos: BlockPos): List<LayerProperty> {
        val state = level.getBlockState(pos)
        val gas = OniMatterAccess.gasSpec(state)
        val layers: MutableList<LayerProperty> = ArrayList()
        for (species in OniElements.GASES) {
            val fraction = if (gas == species) 1.0 else 0.0
            layers.add(
                LayerProperty(
                    "gas",
                    "${species.symbol.lowercase()}_fraction",
                    "%.4f".format(fraction)
                )
            )
        }
        val breathing = if (gas == OniElements.GAS_OXYGEN) "HEALTHY" else "CRITICAL"
        layers.add(LayerProperty("gas", "breathing_band", breathing))
        return layers
    }

    private fun powerLayers(runtime: OniSystemRuntime): List<LayerProperty> {
        val power = runtime.powerState()
        return listOf(
            LayerProperty("power", "generation_w", "%.2f".format(power.generationW())),
            LayerProperty("power", "demand_w", "%.2f".format(power.demandW())),
            LayerProperty("power", "stored_j", "%.2f".format(power.storedEnergyJ())),
            LayerProperty("power", "tripped", power.tripped().toString()),
        )
    }

    private fun stressLayers(runtime: OniSystemRuntime, player: Player?): List<LayerProperty> {
        val score = if (player != null) {
            runtime.stressState().score(player)
        } else {
            runtime.stressState().score()
        }
        return listOf(
            LayerProperty("stress", "score", "%.2f".format(score)),
        )
    }

    private fun researchLayers(runtime: OniSystemRuntime): List<LayerProperty> {
        return listOf(
            LayerProperty("research", "unlocked_count", runtime.researchState().unlockedCount().toString()),
            LayerProperty("research", "nodes", runtime.researchState().unlockedNodes().toString()),
        )
    }

    private fun constructionLayers(runtime: OniSystemRuntime): List<LayerProperty> {
        return listOf(
            LayerProperty("construction", "queue_size", runtime.constructionState().activeCount().toString()),
        )
    }

    private fun gasPressureKpa(entity: mconi.common.block.entity.OniMatterBlockEntity, cellSize: Int): Double {
        val cellVolumeM3 = Math.pow(cellSize.toDouble(), 3.0)
        val scaledDensity = entity.massKg() / maxOf(0.0001, cellVolumeM3)
        return (scaledDensity / STANDARD_AIR_DENSITY_KG_PER_M3) *
            (entity.temperatureK() / STANDARD_TEMPERATURE_K) *
            STANDARD_PRESSURE_KPA
    }

}
