package mconi.mixins.fabric

import net.fabricmc.loader.api.FabricLoader
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo
import java.util.Set

/**
 * Mixin plugin for Fabric loader.
 */
class FabricMixinPlugin : IMixinConfigPlugin {
    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String): Boolean {
        if (mixinClassName.contains(".mods.")) {
            val modId = mixinClassName
                .replace(Regex("^.*mods."), "")
                .replace(Regex("\\..*$"), "")
            return FabricLoader.getInstance().isModLoaded(modId)
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
