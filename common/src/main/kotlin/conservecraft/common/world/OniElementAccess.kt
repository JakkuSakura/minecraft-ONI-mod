package conservecraft.common.world

import conservecraft.common.block.entity.OniElementBlockEntity
import conservecraft.common.config.OniIntegrationConfig
import conservecraft.common.element.ElementContents
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level

object OniElementAccess {
    fun elements(level: Level, pos: BlockPos): List<ElementContents> {
        val entity = level.getBlockEntity(pos) as? OniElementBlockEntity
        if (entity != null) {
            return entity.elements()
        }
        val server = level as? ServerLevel ?: return emptyList()
        if (!OniIntegrationConfig.enableVanillaElements(server)) {
            return emptyList()
        }
        val defaults = OniVanillaElementBindings.defaultsFor(level.getBlockState(pos)) ?: return emptyList()
        return OniVanillaElementData.get(server).elementsAt(pos.asLong(), defaults)
    }

    fun setElements(level: Level, pos: BlockPos, elements: List<ElementContents>) {
        val entity = level.getBlockEntity(pos) as? OniElementBlockEntity
        if (entity != null) {
            entity.setElements(elements)
            return
        }
        val server = level as? ServerLevel ?: return
        if (!OniIntegrationConfig.enableVanillaElements(server)) {
            return
        }
        val defaults = OniVanillaElementBindings.defaultsFor(level.getBlockState(pos)) ?: return
        if (elements.isEmpty()) {
            OniVanillaElementData.get(server).remove(pos.asLong())
            return
        }
        OniVanillaElementData.get(server).setElements(pos.asLong(), elements.ifEmpty { defaults })
    }

    fun ensureDefaults(level: Level, pos: BlockPos): List<ElementContents> {
        val entity = level.getBlockEntity(pos) as? OniElementBlockEntity
        if (entity != null) {
            val current = entity.elements()
            if (current.isNotEmpty()) {
                return current
            }
            val defaults = OniVanillaElementBindings.defaultsFor(level.getBlockState(pos)) ?: return emptyList()
            entity.setElements(defaults)
            return defaults
        }
        val server = level as? ServerLevel ?: return emptyList()
        if (!OniIntegrationConfig.enableVanillaElements(server)) {
            return emptyList()
        }
        val defaults = OniVanillaElementBindings.defaultsFor(level.getBlockState(pos)) ?: return emptyList()
        return OniVanillaElementData.get(server).elementsAt(pos.asLong(), defaults)
    }

    fun totalMass(level: Level, pos: BlockPos): Double {
        return elements(level, pos).sumOf { it.mass }
    }

    fun averageTemperatureK(level: Level, pos: BlockPos): Double {
        val elements = elements(level, pos)
        if (elements.isEmpty()) {
            return 293.15
        }
        val total = elements.sumOf { it.mass }
        if (total <= 0.0) {
            return elements.first().temperatureK
        }
        val weighted = elements.sumOf { it.mass * it.temperatureK }
        return weighted / total
    }

    fun setTemperatureK(level: Level, pos: BlockPos, tempK: Double) {
        val entity = level.getBlockEntity(pos) as? OniElementBlockEntity
        if (entity != null) {
            entity.setTemperatureK(tempK)
            return
        }
        val server = level as? ServerLevel ?: return
        if (!OniIntegrationConfig.enableVanillaElements(server)) {
            return
        }
        val defaults = OniVanillaElementBindings.defaultsFor(level.getBlockState(pos)) ?: return
        val data = OniVanillaElementData.get(server)
        val current = data.elementsAt(pos.asLong(), defaults)
        if (current.isEmpty()) {
            return
        }
        val updated = current.map { it.copy(temperatureK = tempK) }
        data.setElements(pos.asLong(), updated)
    }

    fun remove(level: Level, pos: BlockPos) {
        val server = level as? ServerLevel ?: return
        if (!OniIntegrationConfig.enableVanillaElements(server)) {
            return
        }
        OniVanillaElementData.get(server).remove(pos.asLong())
    }
}
