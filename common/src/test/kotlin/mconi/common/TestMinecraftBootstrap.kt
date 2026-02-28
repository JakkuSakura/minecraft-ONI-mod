package mconi.common

object TestMinecraftBootstrap {
    @Volatile
    private var bootstrapped = false

    fun ensureBootstrapped() {
        if (bootstrapped) {
            return
        }
        synchronized(this) {
            if (bootstrapped) {
                return
            }
            ensureVersion()
            net.minecraft.server.Bootstrap.bootStrap()
            bootstrapped = true
        }
    }

    private fun ensureVersion() {
        try {
            net.minecraft.SharedConstants.tryDetectVersion()
            net.minecraft.SharedConstants.getCurrentVersion()
            return
        } catch (_: Throwable) {
        }
        val sharedConstants = Class.forName("net.minecraft.SharedConstants")
        val detectedVersion = Class.forName("net.minecraft.DetectedVersion")
        val builtIn = try {
            detectedVersion.getField("BUILT_IN").get(null)
        } catch (_: Throwable) {
            detectedVersion.getField("BUILTIN").get(null)
        }
        val setVersion = sharedConstants.getMethod("setVersion", detectedVersion)
        setVersion.invoke(null, builtIn)
    }
}
