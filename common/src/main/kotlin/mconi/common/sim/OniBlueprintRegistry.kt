package mconi.common.sim

data class OniBlueprint(
    val id: String,
    val requiredResearch: String,
    val requiredMaterialUnits: Int,
    val buildTimeSeconds: Int,
)

object OniBlueprintRegistry {
    private val blueprints: MutableMap<String, OniBlueprint> = LinkedHashMap()

    init {
        register(OniBlueprint("oxygen_diffuser", "oxygen", 20, 30))
        register(OniBlueprint("liquid_pump", "liquids", 40, 45))
        register(OniBlueprint("power_generator", "power", 30, 40))
        register(OniBlueprint("research_desk", "research", 25, 35))
    }

    @JvmStatic
    fun register(blueprint: OniBlueprint) {
        blueprints[blueprint.id] = blueprint
    }

    @JvmStatic
    fun get(id: String): OniBlueprint? = blueprints[id]

    @JvmStatic
    fun allIds(): Set<String> = blueprints.keys
}
