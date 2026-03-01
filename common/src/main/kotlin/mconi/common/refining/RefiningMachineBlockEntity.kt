package mconi.common.refining

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class RefiningMachineBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(mconi.common.block.entity.OniBlockEntityTypes.REFINING_MACHINE, pos, state) {

    data class StoredElement(
        val elementId: String,
        var mass: Double,
        var temperatureK: Double,
        val phase: RefiningPhase
    )

    private val storage: MutableMap<String, StoredElement> = LinkedHashMap()
    private var activeRecipeId: String? = null
    private var progressSeconds: Double = 0.0

    fun storage(): Map<String, StoredElement> = storage
    fun stored(elementId: String): StoredElement? = storage[elementId]

    fun addStored(elementId: String, mass: Double, temperatureK: Double, phase: RefiningPhase) {
        if (mass <= 0.0) {
            return
        }
        val existing = storage[elementId]
        if (existing == null) {
            storage[elementId] = StoredElement(elementId, mass, temperatureK, phase)
        } else {
            val total = existing.mass + mass
            if (total > 0.0) {
                val mixedTemp = (existing.temperatureK * existing.mass + temperatureK * mass) / total
                existing.mass = total
                existing.temperatureK = mixedTemp
            }
        }
        setChanged()
    }

    fun takeStored(elementId: String, mass: Double): StoredElement? {
        val existing = storage[elementId] ?: return null
        if (mass <= 0.0) {
            return null
        }
        val taken = mass.coerceAtMost(existing.mass)
        if (taken <= 0.0) {
            return null
        }
        existing.mass -= taken
        val result = StoredElement(elementId, taken, existing.temperatureK, existing.phase)
        if (existing.mass <= 1e-9) {
            storage.remove(elementId)
        }
        setChanged()
        return result
    }

    fun activeRecipeId(): String? = activeRecipeId
    fun setActiveRecipeId(value: String?) {
        activeRecipeId = value
        setChanged()
    }

    fun progressSeconds(): Double = progressSeconds
    fun setProgressSeconds(value: Double) {
        progressSeconds = value
        setChanged()
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.putString("ActiveRecipe", activeRecipeId ?: "")
        output.putDouble("ProgressSeconds", progressSeconds)
        output.putInt("StoredCount", storage.size)
        var idx = 0
        for (entry in storage.values) {
            output.putString("Stored_${idx}_Id", entry.elementId)
            output.putDouble("Stored_${idx}_Mass", entry.mass)
            output.putDouble("Stored_${idx}_TempK", entry.temperatureK)
            output.putString("Stored_${idx}_Phase", entry.phase.name)
            idx++
        }
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        val recipeId = input.getStringOr("ActiveRecipe", "")
        activeRecipeId = recipeId.ifBlank { null }
        progressSeconds = input.getDoubleOr("ProgressSeconds", 0.0)
        storage.clear()
        val count = input.getIntOr("StoredCount", 0)
        for (i in 0 until count) {
            val id = input.getStringOr("Stored_${i}_Id", "")
            if (id.isBlank()) {
                continue
            }
            val mass = input.getDoubleOr("Stored_${i}_Mass", 0.0)
            val temp = input.getDoubleOr("Stored_${i}_TempK", 293.15)
            val phaseName = input.getStringOr("Stored_${i}_Phase", RefiningPhase.SOLID.name)
            val phase = runCatching { RefiningPhase.valueOf(phaseName) }.getOrDefault(RefiningPhase.SOLID)
            storage[id] = StoredElement(id, mass, temp, phase)
        }
    }
}
