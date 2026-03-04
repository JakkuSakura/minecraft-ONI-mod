package conservecraft.common.world

import com.google.gson.GsonBuilder
import conservecraft.common.AbstractModBootstrap
import conservecraft.common.config.OniConfigPaths
import java.nio.file.Files
import java.nio.file.Path

data class OniWorldgenConfigData(
    var zThickness: Int = DEFAULT_Z_THICKNESS
) {
    companion object {
        const val DEFAULT_Z_THICKNESS: Int = 1
    }
}

object OniWorldgenConfig {
    private const val FILE_NAME = "conservecraft_worldgen.json"
    private val GSON = GsonBuilder().setPrettyPrinting().create()
    @Volatile private var cached: OniWorldgenConfigData? = null

    @JvmStatic
    fun load(): OniWorldgenConfigData {
        val existing = cached
        if (existing != null) {
            return existing
        }
        val path = configPath()
        val loaded = readConfig(path) ?: OniWorldgenConfigData()
        val sanitized = sanitize(loaded)
        cached = sanitized
        return sanitized
    }

    @JvmStatic
    fun save(data: OniWorldgenConfigData) {
        val sanitized = sanitize(data)
        cached = sanitized
        val path = configPath()
        try {
            Files.createDirectories(path.parent)
            Files.writeString(path, GSON.toJson(sanitized))
        } catch (ex: Exception) {
            AbstractModBootstrap.LOGGER.warn("Failed to save ONI worldgen config: {}", ex.toString())
        }
    }

    @JvmStatic
    fun resolveBounds(minZ: Int?, maxZ: Int?): OniWorldgenBounds {
        val thickness = load().zThickness
        return when {
            minZ != null && maxZ != null -> OniWorldgenBounds.fromBounds(minZ, maxZ)
            minZ != null -> {
                val clampedThickness = clampThickness(thickness)
                OniWorldgenBounds(minZ, minZ + clampedThickness - 1)
            }
            maxZ != null -> {
                val clampedThickness = clampThickness(thickness)
                OniWorldgenBounds(maxZ - clampedThickness + 1, maxZ)
            }
            else -> OniWorldgenBounds.fromThickness(thickness)
        }
    }

    @JvmStatic
    fun clampThickness(value: Int): Int {
        return value.coerceAtLeast(OniWorldgenConfigData.DEFAULT_Z_THICKNESS)
    }

    private fun sanitize(data: OniWorldgenConfigData): OniWorldgenConfigData {
        data.zThickness = clampThickness(data.zThickness)
        return data
    }

    private fun configPath(): Path {
        return OniConfigPaths.configDir().resolve(FILE_NAME)
    }

    private fun readConfig(path: Path): OniWorldgenConfigData? {
        if (!Files.exists(path)) {
            return null
        }
        return try {
            val text = Files.readString(path)
            GSON.fromJson(text, OniWorldgenConfigData::class.java)
        } catch (ex: Exception) {
            AbstractModBootstrap.LOGGER.warn("Failed to load ONI worldgen config: {}", ex.toString())
            null
        }
    }
}

data class OniWorldgenBounds(val minZ: Int, val maxZ: Int) {
    init {
        require(minZ <= maxZ) { "minZ must be <= maxZ" }
    }

    fun thickness(): Int = maxZ - minZ + 1

    companion object {
        fun fromThickness(thickness: Int): OniWorldgenBounds {
            val clamped = OniWorldgenConfig.clampThickness(thickness)
            val minZ = -((clamped - 1) / 2)
            val maxZ = minZ + clamped - 1
            return OniWorldgenBounds(minZ, maxZ)
        }

        fun fromBounds(minZ: Int, maxZ: Int): OniWorldgenBounds {
            return if (minZ <= maxZ) {
                OniWorldgenBounds(minZ, maxZ)
            } else {
                OniWorldgenBounds(maxZ, minZ)
            }
        }
    }
}
