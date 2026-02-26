package mconi.neoforge.wrappers

import mconi.common.ModChecker
import net.neoforged.fml.ModList
import java.io.File

/**
 * can check if a mod is installed
 */
class NeoForgeModChecker : ModChecker() {
    override fun isModLoaded(modid: String): Boolean {
        return ModList.get().isLoaded(modid)
    }

    override fun modLocation(modid: String): File {
        return ModList.get().getModFileById(modid).file.filePath.toFile()
    }
}
