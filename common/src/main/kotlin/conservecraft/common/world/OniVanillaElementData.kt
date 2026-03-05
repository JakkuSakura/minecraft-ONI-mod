package conservecraft.common.world

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import conservecraft.common.element.ElementContents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType
import net.minecraft.util.datafix.DataFixTypes

class OniVanillaElementData : SavedData() {
    private val elementsByPos: MutableMap<Long, List<ElementContents>> = HashMap()

    fun elementsAt(posLong: Long, defaults: List<ElementContents>): List<ElementContents> {
        val existing = elementsByPos[posLong]
        if (existing != null) {
            return existing.map { it.copy() }
        }
        val stored = defaults.map { it.copy() }
        elementsByPos[posLong] = stored
        setDirty()
        return stored.map { it.copy() }
    }

    fun setElements(posLong: Long, elements: List<ElementContents>) {
        elementsByPos[posLong] = elements.map { it.copy() }
        setDirty()
    }

    fun remove(posLong: Long) {
        if (elementsByPos.remove(posLong) != null) {
            setDirty()
        }
    }

    override fun isDirty(): Boolean {
        return super.isDirty()
    }

    private fun toTag(): CompoundTag {
        val tag = CompoundTag()
        val list = ListTag()
        for ((posLong, elements) in elementsByPos) {
            val entry = CompoundTag()
            entry.putLong("Pos", posLong)
            entry.putInt("ElementCount", elements.size)
            for ((index, element) in elements.withIndex()) {
                entry.putString("Element_${index}_Id", element.elementId)
                entry.putDouble("Element_${index}_Mass", element.mass)
                entry.putDouble("Element_${index}_TempK", element.temperatureK)
            }
            list.add(entry)
        }
        tag.put("Entries", list)
        return tag
    }

    private fun loadFromTag(tag: CompoundTag) {
        elementsByPos.clear()
        val entries = tag.getList("Entries").orElse(ListTag())
        for (i in 0 until entries.size) {
            val entry = entries.getCompoundOrEmpty(i)
            val posLong = entry.getLong("Pos").orElse(0L)
            val count = entry.getInt("ElementCount").orElse(0)
            if (count <= 0) {
                continue
            }
            val elements: MutableList<ElementContents> = ArrayList(count)
            for (index in 0 until count) {
                val id = entry.getString("Element_${index}_Id").orElse("")
                if (id.isBlank()) {
                    continue
                }
                val mass = entry.getDouble("Element_${index}_Mass").orElse(0.0)
                val tempK = entry.getDouble("Element_${index}_TempK").orElse(293.15)
                elements.add(ElementContents(id, mass, tempK))
            }
            if (elements.isNotEmpty()) {
                elementsByPos[posLong] = elements
            }
        }
    }

    companion object {
        val TYPE: SavedDataType<OniVanillaElementData> = SavedDataType(
            "conservecraft_vanilla_elements",
            java.util.function.Supplier { OniVanillaElementData() },
            makeCodec(),
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE
        )

        fun get(level: net.minecraft.server.level.ServerLevel): OniVanillaElementData {
            return level.dataStorage.computeIfAbsent(TYPE)
        }

        private fun makeCodec(): Codec<OniVanillaElementData> {
            return CompoundTag.CODEC.flatXmap(
                { tag ->
                    val data = OniVanillaElementData()
                    data.loadFromTag(tag)
                    DataResult.success(data)
                },
                { data ->
                    DataResult.success(data.toTag())
                }
            )
        }
    }
}
