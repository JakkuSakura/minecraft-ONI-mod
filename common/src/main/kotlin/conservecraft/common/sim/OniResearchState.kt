package conservecraft.common.sim

import java.util.concurrent.ConcurrentHashMap

class OniResearchState {
    private val unlockedNodes: MutableSet<String> = ConcurrentHashMap.newKeySet()

    fun unlock(nodeId: String) {
        unlockedNodes.add(nodeId)
    }

    fun isUnlocked(nodeId: String): Boolean = unlockedNodes.contains(nodeId)

    fun unlockedCount(): Int = unlockedNodes.size

    fun unlockedNodes(): Set<String> = unlockedNodes.toSet()

    fun clear() {
        unlockedNodes.clear()
    }
}
