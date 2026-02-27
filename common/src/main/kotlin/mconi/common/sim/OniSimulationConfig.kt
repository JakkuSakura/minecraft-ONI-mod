package mconi.common.sim

/**
 * Runtime configuration for the ONI simulation kernel.
 * Values are currently in-memory and intentionally conservative.
 */
class OniSimulationConfig {
    private var tickInterval: Int = 1
    private var cellSize: Int = 1
    private var worldMinX: Int = -256
    private var worldMaxX: Int = 255
    private var worldMinZ: Int = -256
    private var worldMaxZ: Int = 255
    private var voidBandHeight: Int = 24
    private var lavaBandHeight: Int = 24
    private var gasTransferKgPerStep: Double = 0.25
    private var fluidTransferKgPerStep: Double = 50.0
    private var voidGasDrainFraction: Double = 0.35
    private var voidFluidDrainFraction: Double = 0.35
    private var baseO2MassKg: Double = 1.0
    private var baseCO2MassKg: Double = 0.02
    private var baseH2MassKg: Double = 0.02
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
    fun gasTransferKgPerStep(): Double = gasTransferKgPerStep
    fun fluidTransferKgPerStep(): Double = fluidTransferKgPerStep
    fun voidGasDrainFraction(): Double = voidGasDrainFraction
    fun voidFluidDrainFraction(): Double = voidFluidDrainFraction
    fun baseO2MassKg(): Double = baseO2MassKg
    fun baseCO2MassKg(): Double = baseCO2MassKg
    fun baseH2MassKg(): Double = baseH2MassKg
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

    fun setGasTransferKgPerStep(gasTransferKgPerStep: Double) {
        require(gasTransferKgPerStep >= 0.0) { "gasTransferKgPerStep must be >= 0" }
        this.gasTransferKgPerStep = gasTransferKgPerStep
    }

    fun setFluidTransferKgPerStep(fluidTransferKgPerStep: Double) {
        require(fluidTransferKgPerStep >= 0.0) { "fluidTransferKgPerStep must be >= 0" }
        this.fluidTransferKgPerStep = fluidTransferKgPerStep
    }

    fun setVoidGasDrainFraction(voidGasDrainFraction: Double) {
        require(voidGasDrainFraction in 0.0..1.0) { "voidGasDrainFraction must be 0..1" }
        this.voidGasDrainFraction = voidGasDrainFraction
    }

    fun setVoidFluidDrainFraction(voidFluidDrainFraction: Double) {
        require(voidFluidDrainFraction in 0.0..1.0) { "voidFluidDrainFraction must be 0..1" }
        this.voidFluidDrainFraction = voidFluidDrainFraction
    }

    fun setBaseO2MassKg(baseO2MassKg: Double) {
        require(baseO2MassKg >= 0.0) { "baseO2MassKg must be >= 0" }
        this.baseO2MassKg = baseO2MassKg
    }

    fun setBaseCO2MassKg(baseCO2MassKg: Double) {
        require(baseCO2MassKg >= 0.0) { "baseCO2MassKg must be >= 0" }
        this.baseCO2MassKg = baseCO2MassKg
    }

    fun setBaseH2MassKg(baseH2MassKg: Double) {
        require(baseH2MassKg >= 0.0) { "baseH2MassKg must be >= 0" }
        this.baseH2MassKg = baseH2MassKg
    }

    fun setWorldSampleRadiusBlocks(worldSampleRadiusBlocks: Int) {
        require(worldSampleRadiusBlocks >= 0) { "worldSampleRadiusBlocks must be >= 0" }
        this.worldSampleRadiusBlocks = worldSampleRadiusBlocks
    }

    fun setWorldSampleIntervalTicks(worldSampleIntervalTicks: Int) {
        require(worldSampleIntervalTicks >= 1) { "worldSampleIntervalTicks must be >= 1" }
        this.worldSampleIntervalTicks = worldSampleIntervalTicks
    }
}
