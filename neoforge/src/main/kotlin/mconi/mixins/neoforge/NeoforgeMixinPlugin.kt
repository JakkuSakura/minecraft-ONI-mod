package mconi.mixins.neoforge

import net.neoforged.fml.ModList
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

/**
 * Mixin plugin for NeoForge loader.
 */
class NeoforgeMixinPlugin : IMixinConfigPlugin {
    private var firstRun = false
    private var isNeoforgeMixinFile = false

    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean {
        if (!firstRun) {
            isNeoforgeMixinFile = try {
                Class.forName("net.neoforged.fml.common.Mod")
                true
            } catch (_: ClassNotFoundException) {
                false
            }
            firstRun = true
        }
        if (!isNeoforgeMixinFile) {
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
