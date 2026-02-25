package mconi.common.sim

/**
 * Runtime configuration for the ONI simulation kernel.
 * Values are currently in-memory and intentionally conservative.
 */
class OniSimulationConfig {
    private var tickInterval: Int = 10
    private var cellSize: Int = 4
    private var worldMinX: Int = -1024
    private var worldMaxX: Int = 1024
    private var worldMinZ: Int = -1024
    private var worldMaxZ: Int = 1024
    private var voidBandHeight: Int = 32
    private var lavaBandHeight: Int = 32

    fun tickInterval(): Int = tickInterval
    fun cellSize(): Int = cellSize
    fun worldMinX(): Int = worldMinX
    fun worldMaxX(): Int = worldMaxX
    fun worldMinZ(): Int = worldMinZ
    fun worldMaxZ(): Int = worldMaxZ
    fun voidBandHeight(): Int = voidBandHeight
    fun lavaBandHeight(): Int = lavaBandHeight

    fun setTickInterval(tickInterval: Int) {
        require(tickInterval >= 1) { "tickInterval must be >= 1" }
        this.tickInterval = tickInterval
    }

    fun setCellSize(cellSize: Int) {
        require(cellSize >= 1) { "cellSize must be >= 1" }
        this.cellSize = cellSize
    }
}
