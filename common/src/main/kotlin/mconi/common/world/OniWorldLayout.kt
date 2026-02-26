package mconi.common.world

object OniWorldLayout {
    const val WORLD_MIN_X = -256
    const val WORLD_MAX_X = 256
    const val WORLD_MIN_Z = -256
    const val WORLD_MAX_Z = 256

    // Minecraft 1.21 minY is -64; we clamp to that at runtime.
    const val WORLD_TARGET_MIN_Y = -128
    const val WORLD_TARGET_MAX_Y = 128

    const val POD_X = 0
    const val POD_Z = 0
    const val POD_Y = 32

    const val SURFACE_Y = 48
    const val LAVA_BAND_HEIGHT = 24
    const val SPACE_BAND_HEIGHT = 24
    const val TOPSOIL_DEPTH = 6
    const val SEDIMENTARY_DEPTH = 20
    const val IGNEOUS_DEPTH = 12
}
