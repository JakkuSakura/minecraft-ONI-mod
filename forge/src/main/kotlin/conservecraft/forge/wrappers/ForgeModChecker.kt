package conservecraft.forge.wrappers

import conservecraft.common.ModChecker
import net.minecraftforge.fml.ModList
import java.io.File

/**
 * can check if a mod is installed
 */
class ForgeModChecker : ModChecker() {
    override fun isModLoaded(modid: String): Boolean {
        return ModList.get().isLoaded(modid)
    }

    override fun modLocation(modid: String): File {
        return ModList.get().getModFileById(modid).file.filePath.toFile()
    }
}
