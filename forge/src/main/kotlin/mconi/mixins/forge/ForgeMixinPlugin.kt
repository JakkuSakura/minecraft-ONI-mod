package mconi.mixins.forge

import net.minecraftforge.fml.ModList
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

/**
 * Mixin plugin for Forge loader.
 */
class ForgeMixinPlugin : IMixinConfigPlugin {
    private var firstRun = false
    private var isForgeMixinFile = false

    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean {
        if (!firstRun) {
            isForgeMixinFile = try {
                Class.forName("net.neoforged.fml.common.Mod")
                false
            } catch (_: ClassNotFoundException) {
                true
            }
            firstRun = true
        }
        if (!isForgeMixinFile) {
            return false
        }

        if (mixinClassName.contains(".mods.")) {
            val modId = mixinClassName
                .replace(Regex("^.*mods."), "")
                .replace(Regex("\\..*$"), "")
            return ModList.get().isLoaded(modId)
        }
        return true
    }

    override fun onLoad(mixinPackage: String) {
    }

    override fun getRefMapperConfig(): String? = null

    override fun acceptTargets(myTargets: MutableSet<String>, otherTargets: MutableSet<String>) {
    }

    override fun getMixins(): MutableList<String>? = null

    override fun preApply(targetClassName: String, targetClass: ClassNode, mixinClassName: String, mixinInfo: IMixinInfo) {
    }

    override fun postApply(targetClassName: String, targetClass: ClassNode, mixinClassName: String, mixinInfo: IMixinInfo) {
    }
}
