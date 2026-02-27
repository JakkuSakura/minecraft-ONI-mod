package mconi.common.element

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType

class OniElementStore : SavedData {
    private val entries: MutableMap<Long, List<ElementStack>>

    constructor() {
        entries = LinkedHashMap()
    }

    constructor(entries: List<Entry>) {
        this.entries = LinkedHashMap()
        for (entry in entries) {
            val key = BlockPos(entry.x, entry.y, entry.z).asLong()
            this.entries[key] = entry.elements
        }
    }

    fun setElements(pos: BlockPos, elements: List<ElementStack>) {
        entries[pos.asLong()] = elements
        setDirty()
    }

    fun elementsAt(pos: BlockPos): List<ElementStack> = entries[pos.asLong()].orEmpty()

    fun takeElements(pos: BlockPos): List<ElementStack> {
        val removed = entries.remove(pos.asLong()).orEmpty()
        if (removed.isNotEmpty()) {
            setDirty()
        }
        return removed
    }

    fun remove(pos: BlockPos) {
        if (entries.remove(pos.asLong()) != null) {
            setDirty()
        }
    }

    data class Entry(
        val x: Int,
        val y: Int,
        val z: Int,
        val elements: List<ElementStack>
    ) {
        companion object {
            val CODEC: Codec<Entry> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("x").forGetter(Entry::x),
                    Codec.INT.fieldOf("y").forGetter(Entry::y),
                    Codec.INT.fieldOf("z").forGetter(Entry::z),
                    ElementStackCodec.CODEC.listOf().fieldOf("elements").forGetter(Entry::elements)
                ).apply(instance) { x, y, z, elements ->
                    Entry(x, y, z, elements)
                }
            }
        }
    }

    object ElementStackCodec {
        val CODEC: Codec<ElementStack> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.fieldOf("item").forGetter(ElementStack::itemId),
                Codec.INT.fieldOf("amount").forGetter(ElementStack::amount)
            ).apply(instance) { item, amount ->
                ElementStack(item, amount)
            }
        }
    }

    companion object {
        val CODEC: Codec<OniElementStore> = RecordCodecBuilder.create { instance ->
            instance.group(
                Entry.CODEC.listOf().fieldOf("entries").forGetter { storage ->
                    storage.entries.entries.map { (key, elements) ->
                        val pos = BlockPos.of(key)
                        Entry(pos.x, pos.y, pos.z, elements)
                    }
                }
            ).apply(instance) { entries -> OniElementStore(entries) }
        }

        val TYPE: SavedDataType<OniElementStore> =
            SavedDataType("mconi_elements", { OniElementStore() }, CODEC, DataFixTypes.LEVEL)

        fun get(level: ServerLevel): OniElementStore {
            return level.dataStorage.computeIfAbsent(TYPE)
        }
    }
}
