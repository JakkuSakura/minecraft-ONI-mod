package conservecraft.common.refining

enum class RefiningPhase {
    SOLID,
    LIQUID,
    GAS
}

enum class TemperatureMode {
    AVERAGE_INPUT,
    HEATED,
    MELTED,
    FIXED
}

data class RefiningIngredient(
    val elementIds: List<String>,
    val amount: Double,
    val phase: RefiningPhase
)

data class RefiningOutput(
    val elementId: String,
    val amount: Double,
    val phase: RefiningPhase,
    val temperatureMode: TemperatureMode,
    val minTemperatureK: Double = 0.0,
    val fixedTemperatureK: Double? = null,
    val storeOutput: Boolean = true
)

data class RefiningRecipe(
    val id: String,
    val durationSeconds: Double,
    val inputs: List<RefiningIngredient>,
    val outputs: List<RefiningOutput>,
    val continuous: Boolean = false
)

data class RefiningBuildingSpec(
    val id: String,
    val powerW: Double,
    val selfHeatKw: Double,
    val heatedTemperatureK: Double? = null,
    val recipes: List<RefiningRecipe>,
    val liquidInput: Boolean = false,
    val liquidOutput: Boolean = false,
    val gasInput: Boolean = false,
    val gasOutput: Boolean = false
)
