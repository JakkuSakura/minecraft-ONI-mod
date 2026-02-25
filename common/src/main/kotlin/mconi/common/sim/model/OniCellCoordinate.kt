package mconi.common.sim.model

data class OniCellCoordinate(
    private val cellX: Int,
    private val cellY: Int,
    private val cellZ: Int,
) {
    fun cellX(): Int = cellX
    fun cellY(): Int = cellY
    fun cellZ(): Int = cellZ

    companion object {
        @JvmStatic
        fun fromBlockPosition(x: Int, y: Int, z: Int, cellSize: Int): OniCellCoordinate {
            return OniCellCoordinate(
                Math.floorDiv(x, cellSize),
                Math.floorDiv(y, cellSize),
                Math.floorDiv(z, cellSize),
            )
        }
    }
}
