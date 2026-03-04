package conservecraft.common.sim

/**
 * Runtime configuration for the ONI system loop.
 * Values are currently in-memory and intentionally conservative.
 */
class OniSystemConfig {
    private var tickInterval: Int = 1
    private var cellSize: Int = 1
    private var worldMinX: Int = -256
    private var worldMaxX: Int = 255
    private var worldMinZ: Int = -256
    private var worldMaxZ: Int = 255
    private var voidBandHeight: Int = 24
    private var lavaBandHeight: Int = 24
    private var gasTransferPerStep: Double = 0.25
    private var liquidTransferPerStep: Double = 50.0
    private var voidGasDrainFraction: Double = 0.35
    private var voidLiquidDrainFraction: Double = 0.35
    private var worldSampleRadiusBlocks: Int = 24
    private var worldSampleIntervalTicks: Int = 1

    fun tickInterval(): Int = tickInterval
    fun cellSize(): Int = cellSize
    fun worldMinX(): Int = worldMinX
    fun worldMaxX(): Int = worldMaxX
    fun worldMinZ(): Int = worldMinZ
    fun worldMaxZ(): Int = worldMaxZ
    fun voidBandHeight(): Int = voidBandHeight
    fun lavaBandHeight(): Int = lavaBandHeight
    fun gasTransferPerStep(): Double = gasTransferPerStep
    fun liquidTransferPerStep(): Double = liquidTransferPerStep
    fun voidGasDrainFraction(): Double = voidGasDrainFraction
    fun voidLiquidDrainFraction(): Double = voidLiquidDrainFraction
    fun worldSampleRadiusBlocks(): Int = worldSampleRadiusBlocks
    fun worldSampleIntervalTicks(): Int = worldSampleIntervalTicks

    fun setTickInterval(tickInterval: Int) {
        require(tickInterval >= 1) { "tickInterval must be >= 1" }
        this.tickInterval = tickInterval
    }

    fun setCellSize(cellSize: Int) {
        require(cellSize >= 1) { "cellSize must be >= 1" }
        this.cellSize = cellSize
    }

    fun setGasTransferPerStep(gasTransferPerStep: Double) {
        require(gasTransferPerStep >= 0.0) { "gasTransferPerStep must be >= 0" }
        this.gasTransferPerStep = gasTransferPerStep
    }

    fun setLiquidTransferPerStep(liquidTransferPerStep: Double) {
        require(liquidTransferPerStep >= 0.0) { "liquidTransferPerStep must be >= 0" }
        this.liquidTransferPerStep = liquidTransferPerStep
    }

    fun setVoidGasDrainFraction(voidGasDrainFraction: Double) {
        require(voidGasDrainFraction in 0.0..1.0) { "voidGasDrainFraction must be 0..1" }
        this.voidGasDrainFraction = voidGasDrainFraction
    }

    fun setVoidLiquidDrainFraction(voidLiquidDrainFraction: Double) {
        require(voidLiquidDrainFraction in 0.0..1.0) { "voidLiquidDrainFraction must be 0..1" }
        this.voidLiquidDrainFraction = voidLiquidDrainFraction
    }


    fun setWorldSampleRadiusBlocks(worldSampleRadiusBlocks: Int) {
        require(worldSampleRadiusBlocks >= 0) { "worldSampleRadiusBlocks must be >= 0" }
        this.worldSampleRadiusBlocks = worldSampleRadiusBlocks
    }

    fun setWorldSampleIntervalTicks(worldSampleIntervalTicks: Int) {
        require(worldSampleIntervalTicks >= 1) { "worldSampleIntervalTicks must be >= 1" }
        this.worldSampleIntervalTicks = worldSampleIntervalTicks
    }

    fun setWorldMinZ(worldMinZ: Int) {
        this.worldMinZ = worldMinZ
    }

    fun setWorldMaxZ(worldMaxZ: Int) {
        this.worldMaxZ = worldMaxZ
    }
}
