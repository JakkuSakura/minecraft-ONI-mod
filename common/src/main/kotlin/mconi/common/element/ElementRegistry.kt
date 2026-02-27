package mconi.common.element

import net.minecraft.resources.Identifier
import java.util.Collections

class ElementRegistry {
    private val byId: MutableMap<String, ElementSpec> = LinkedHashMap()
    private val byItem: MutableMap<Identifier, ElementSpec> = LinkedHashMap()

    fun register(spec: ElementSpec) {
        require(spec.id.isNotBlank()) { "Element id is blank." }
        require(!byId.containsKey(spec.id)) { "Duplicate material id: ${spec.id}" }
        require(!byItem.containsKey(spec.itemId)) { "Duplicate material item id: ${spec.itemId}" }
        byId[spec.id] = spec
        byItem[spec.itemId] = spec
    }

    fun byId(id: String): ElementSpec? = byId[id]

    fun byItemId(itemId: Identifier): ElementSpec? = byItem[itemId]

    fun all(): Map<String, ElementSpec> = Collections.unmodifiableMap(byId)
}
