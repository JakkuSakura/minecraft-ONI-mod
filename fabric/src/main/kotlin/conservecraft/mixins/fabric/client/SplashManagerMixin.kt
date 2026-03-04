package conservecraft.mixins.fabric.client

import net.minecraft.client.gui.components.SplashRenderer
import net.minecraft.client.resources.SplashManager
import net.minecraft.network.chat.Component
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

@Mixin(SplashManager::class)
class SplashManagerMixin {
    @Inject(method = ["getSplash"], at = [At("HEAD")], cancellable = true)
    private fun setSplash(cir: CallbackInfoReturnable<SplashRenderer>) {
        cir.returnValue = SplashRenderer(Component.literal("§c§lExampleMod Fabric!"))
    }
}
