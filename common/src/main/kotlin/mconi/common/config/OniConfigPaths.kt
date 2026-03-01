package mconi.common.config

import java.nio.file.Path
import java.nio.file.Paths

object OniConfigPaths {
    @Volatile private var configDir: Path? = null

    @JvmStatic
    fun init(path: Path) {
        configDir = path
    }

    @JvmStatic
    fun configDir(): Path {
        return configDir ?: Paths.get("config")
    }
}
