package conservecraft.common.config

import com.google.gson.GsonBuilder
import conservecraft.common.AbstractModBootstrap
import conservecraft.common.world.OniWorldType
import net.minecraft.server.level.ServerLevel
import java.nio.file.Files
import java.nio.file.Path

data class OniIntegrationConfigData(
    var vanillaElementsEverywhere: Boolean = true
)

object OniIntegrationConfig {
    private const val FILE_NAME = "conservecraft_integration.json"
    private val GSON = GsonBuilder().setPrettyPrinting().create()
    @Volatile private var cached: OniIntegrationConfigData? = null

    fun load(): OniIntegrationConfigData {
        val existing = cached
        if (existing != null) {
            return existing
        }
        val path = configPath()
        val loaded = readConfig(path) ?: OniIntegrationConfigData()
        val sanitized = sanitize(loaded)
        cached = sanitized
        return sanitized
    }

    fun save(data: OniIntegrationConfigData) {
        val sanitized = sanitize(data)
        cached = sanitized
        val path = configPath()
        try {
            Files.createDirectories(path.parent)
            Files.writeString(path, GSON.toJson(sanitized))
        } catch (ex: Exception) {
            AbstractModBootstrap.LOGGER.warn("Failed to save integration config: {}", ex.toString())
        }
    }

    fun enableVanillaElements(level: ServerLevel): Boolean {
        val data = load()
        if (data.vanillaElementsEverywhere) {
            return true
        }
        return OniWorldType.isConserveCraftWorld(level)
    }

    private fun sanitize(data: OniIntegrationConfigData): OniIntegrationConfigData {
        return data
    }

    private fun configPath(): Path {
        return OniConfigPaths.configDir().resolve(FILE_NAME)
    }

    private fun readConfig(path: Path): OniIntegrationConfigData? {
        if (!Files.exists(path)) {
            return null
        }
        return try {
            val text = Files.readString(path)
            GSON.fromJson(text, OniIntegrationConfigData::class.java)
        } catch (ex: Exception) {
            AbstractModBootstrap.LOGGER.warn("Failed to load integration config: {}", ex.toString())
            null
        }
    }
}
