package conservecraft.fabric.wrappers

import conservecraft.common.ModChecker
import net.fabricmc.loader.api.FabricLoader
import java.io.File

/**
 * can check if a mod is installed
 */
class FabricModChecker : ModChecker() {
    override fun isModLoaded(modid: String): Boolean {
        return FabricLoader.getInstance().isModLoaded(modid)
    }

    override fun modLocation(modid: String): File {
        return File(
            FabricLoader.getInstance()
                .getModContainer(modid)
                .orElseThrow { IllegalArgumentException("Missing mod container: $modid") }
                .origin
                .paths[0]
                .toUri()
        )
    }
}
