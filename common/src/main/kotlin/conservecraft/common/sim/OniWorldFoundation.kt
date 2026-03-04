package conservecraft.common.sim

/**
 * Query helpers for constrained-world topology rules.
 */
object OniWorldFoundation {
    @JvmStatic
    fun isWithinHorizontalBounds(x: Int, z: Int, config: OniSystemConfig): Boolean {
        return x >= config.worldMinX() && x <= config.worldMaxX() &&
            z >= config.worldMinZ() && z <= config.worldMaxZ()
    }

    @JvmStatic
    fun isVoidBand(y: Int, maxY: Int, config: OniSystemConfig): Boolean {
        return y >= (maxY - config.voidBandHeight() + 1)
    }

    @JvmStatic
    fun isLavaBand(y: Int, minY: Int, config: OniSystemConfig): Boolean {
        return y <= (minY + config.lavaBandHeight() - 1)
    }
}
