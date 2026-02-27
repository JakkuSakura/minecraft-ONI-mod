package mconi.common.world

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import mconi.common.element.OniElements
import mconi.common.sim.model.OccupancyState
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType
import java.util.Optional

class OniSimulationStorage : SavedData {
    private val cells: MutableList<CellEntry>

    constructor() {
        cells = ArrayList()
    }

    constructor(entries: List<CellEntry>) {
        cells = ArrayList(entries)
    }

    fun cells(): List<CellEntry> = cells

    fun replaceCells(entries: List<CellEntry>) {
        cells.clear()
        cells.addAll(entries)
        setDirty()
    }

    data class CellEntry(
        val x: Int,
        val y: Int,
        val z: Int,
        val occupancy: OccupancyState,
        val liquidId: String,
        val liquidMass: Double,
        val temperatureK: Double,
        val pressureKpa: Double,
        val o2Mass: Double,
        val co2Mass: Double,
        val h2Mass: Double,
        val overheated: Boolean
    ) {
        companion object {
            val CODEC: Codec<CellEntry> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("x").forGetter(CellEntry::x),
                    Codec.INT.fieldOf("y").forGetter(CellEntry::y),
                    Codec.INT.fieldOf("z").forGetter(CellEntry::z),
                    Codec.STRING.xmap({ parseOccupancy(it) }, { it.name }).fieldOf("occupancy")
                        .forGetter(CellEntry::occupancy),
                    Codec.STRING.optionalFieldOf("liquid")
                        .forGetter { Optional.of(it.liquidId) },
                    Codec.STRING.optionalFieldOf("fluid")
                        .forGetter { Optional.empty() },
                    Codec.DOUBLE.optionalFieldOf("liquidMass")
                        .forGetter { Optional.of(it.liquidMass) },
                    Codec.DOUBLE.optionalFieldOf("fluidMass")
                        .forGetter { Optional.empty() },
                    Codec.DOUBLE.fieldOf("temperatureK").forGetter(CellEntry::temperatureK),
                    Codec.DOUBLE.fieldOf("pressureKpa").forGetter(CellEntry::pressureKpa),
                    Codec.DOUBLE.fieldOf("o2Mass").forGetter(CellEntry::o2Mass),
                    Codec.DOUBLE.fieldOf("co2Mass").forGetter(CellEntry::co2Mass),
                    Codec.DOUBLE.fieldOf("h2Mass").forGetter(CellEntry::h2Mass),
                    Codec.BOOL.fieldOf("overheated").forGetter(CellEntry::overheated)
                ).apply(instance) { x, y, z, occupancy, liquidId, fluidId, liquidMass, fluidMass, temperatureK, pressureKpa, o2Mass, co2Mass, h2Mass, overheated ->
                    val resolvedId = liquidId.orElseGet { fluidId.orElse(OniElements.LIQUID_NONE) }
                    val resolvedMass = liquidMass.orElseGet { fluidMass.orElse(0.0) }
                    CellEntry(x, y, z, occupancy, resolvedId, resolvedMass, temperatureK, pressureKpa, o2Mass, co2Mass, h2Mass, overheated)
                }
            }

            private fun parseOccupancy(value: String): OccupancyState {
                val normalized = value.trim().uppercase()
                if (normalized == "FLUID") {
                    return OccupancyState.LIQUID
                }
                return OccupancyState.valueOf(normalized)
            }
        }
    }

    companion object {
        val CODEC: Codec<OniSimulationStorage> = RecordCodecBuilder.create { instance ->
            instance.group(
                CellEntry.CODEC.listOf().fieldOf("cells").forGetter { storage -> storage.cells }
            ).apply(instance) { entries -> OniSimulationStorage(entries) }
        }

        val TYPE: SavedDataType<OniSimulationStorage> =
            SavedDataType("mconi_simulation", { OniSimulationStorage() }, CODEC, DataFixTypes.LEVEL)
    }
}
