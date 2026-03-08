package conservecraft.common.sim.subsystem

import conservecraft.common.block.OniBlockFactory
import conservecraft.common.block.OniBlockLookup
import conservecraft.common.block.entity.OniConduitBlockEntity
import conservecraft.common.element.OniElements
import conservecraft.common.item.BottledMatterItem
import conservecraft.common.item.OniInventoryMass
import conservecraft.common.item.OniItemFactory
import conservecraft.common.item.OniItemMass
import conservecraft.common.item.OniSolidItems
import conservecraft.common.refining.*
import conservecraft.common.world.OniMatterAccess
import conservecraft.common.world.OniWorldScan
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData
import net.minecraft.world.level.block.Blocks

class RefiningSystem : OniSystem {
    override fun id(): String = "refining"

    override fun run(context: SystemContext) {
        val level = context.level()
        val config = context.config()
        val power = context.runtime().powerState()
        val tickSeconds = config.tickInterval().toDouble() / 20.0
        val dt = tickSeconds * ONI_RATE_SCALE
        val positions = OniWorldScan.positionsAroundPlayers(level, config.worldSampleRadiusBlocks(), config.cellSize())
        if (positions.isEmpty()) {
            return
        }

        for (pos in positions) {
            val state = level.getBlockState(pos)
            val blockId = OniBlockFactory.idOf(state.block) ?: continue
            val spec = RefiningCatalog.spec(blockId) ?: continue
            val entity = level.getBlockEntity(pos) as? RefiningMachineBlockEntity ?: continue
            if (spec.powerW > 0.0 && !power.isConsumerPowered(pos)) {
                continue
            }

            processMachine(level, pos, entity, spec, dt)
        }
    }

    private fun processMachine(
        level: ServerLevel,
        pos: BlockPos,
        entity: RefiningMachineBlockEntity,
        spec: RefiningBuildingSpec,
        dt: Double
    ) {
        val continuous = spec.recipes.filter { it.continuous }
        val batch = spec.recipes.filter { !it.continuous }
        if (continuous.isNotEmpty()) {
            for (recipe in continuous) {
                processContinuous(level, pos, entity, spec, recipe, dt)
            }
        }
        if (batch.isNotEmpty()) {
            processBatch(level, pos, entity, spec, batch, dt)
        }
        flushSolidOutputs(level, pos, entity)
    }

    private fun processContinuous(
        level: ServerLevel,
        pos: BlockPos,
        entity: RefiningMachineBlockEntity,
        spec: RefiningBuildingSpec,
        recipe: RefiningRecipe,
        dt: Double
    ) {
        val desiredInputs = recipe.inputs.map { it to it.amount * dt }
        val desiredOutputs = recipe.outputs.map { it to it.amount * dt }

        val availableScale = computeInputScale(level, pos, entity, desiredInputs)
        if (availableScale <= 1e-9) {
            return
        }
        val outputScale = computeOutputScale(level, pos, entity, spec, desiredOutputs)
        val scale = minOf(availableScale, outputScale)
        if (scale <= 1e-9) {
            return
        }

        val actualInputs = desiredInputs.map { it.first to it.second * scale }
        val actualOutputs = desiredOutputs.map { it.first to it.second * scale }

        val inputTemp = consumeInputs(level, pos, entity, actualInputs)
        if (inputTemp.totalMass <= 0.0) {
            return
        }
        val avgInputTemp = inputTemp.temperatureK
        produceOutputs(level, pos, entity, spec, actualOutputs, avgInputTemp)
    }

    private fun processBatch(
        level: ServerLevel,
        pos: BlockPos,
        entity: RefiningMachineBlockEntity,
        spec: RefiningBuildingSpec,
        recipes: List<RefiningRecipe>,
        dt: Double
    ) {
        val activeId = entity.activeRecipeId()
        val active = if (activeId != null) recipes.find { it.id == activeId } else null

        if (active == null) {
            val next = recipes.firstOrNull { canStartRecipe(level, pos, entity, spec, it) } ?: return
            entity.setActiveRecipeId(next.id)
            entity.setProgressSeconds(0.0)
        }

        val recipe = entity.activeRecipeId()?.let { id -> recipes.firstOrNull { it.id == id } } ?: return
        if (!canStartRecipe(level, pos, entity, spec, recipe)) {
            return
        }

        val nextProgress = entity.progressSeconds() + dt
        if (nextProgress + 1e-9 < recipe.durationSeconds) {
            entity.setProgressSeconds(nextProgress)
            return
        }

        val inputs = recipe.inputs.map { it to it.amount }
        val outputs = recipe.outputs.map { it to it.amount }
        val inputTemp = consumeInputs(level, pos, entity, inputs)
        val avgInputTemp = if (inputTemp.totalMass > 0.0) inputTemp.temperatureK else 293.15
        produceOutputs(level, pos, entity, spec, outputs, avgInputTemp)
        entity.setActiveRecipeId(null)
        entity.setProgressSeconds(0.0)
    }

    private fun canStartRecipe(
        level: ServerLevel,
        pos: BlockPos,
        entity: RefiningMachineBlockEntity,
        spec: RefiningBuildingSpec,
        recipe: RefiningRecipe
    ): Boolean {
        val desiredInputs = recipe.inputs.map { it to it.amount }
        val desiredOutputs = recipe.outputs.map { it to it.amount }
        val inputScale = computeInputScale(level, pos, entity, desiredInputs)
        if (inputScale < 1.0 - 1e-9) {
            return false
        }
        val outputScale = computeOutputScale(level, pos, entity, spec, desiredOutputs)
        return outputScale >= 1.0 - 1e-9
    }

    private data class InputTemperature(
        val totalMass: Double,
        val temperatureK: Double
    )

    private fun computeInputScale(
        level: ServerLevel,
        pos: BlockPos,
        entity: RefiningMachineBlockEntity,
        inputs: List<Pair<RefiningIngredient, Double>>
    ): Double {
        var scale = 1.0
        for ((ingredient, required) in inputs) {
            if (required <= 0.0) {
                continue
            }
            val available = availableMass(level, pos, entity, ingredient)
            if (available <= 0.0) {
                return 0.0
            }
            scale = minOf(scale, available / required)
        }
        return scale
    }

    private fun computeOutputScale(
        level: ServerLevel,
        pos: BlockPos,
        entity: RefiningMachineBlockEntity,
        spec: RefiningBuildingSpec,
        outputs: List<Pair<RefiningOutput, Double>>
    ): Double {
        var scale = 1.0
        for ((output, required) in outputs) {
            if (required <= 0.0) {
                continue
            }
            val capacity = outputCapacity(level, pos, entity, spec, output)
            if (capacity <= 0.0) {
                return 0.0
            }
            scale = minOf(scale, capacity / required)
        }
        return scale
    }

    private fun availableMass(
        level: ServerLevel,
        pos: BlockPos,
        entity: RefiningMachineBlockEntity,
        ingredient: RefiningIngredient
    ): Double {
        var total = 0.0
        for (elementId in ingredient.elementIds) {
            total += storedMass(entity, elementId)
            total += availableFromContainers(level, pos, elementId)
            total += availableFromConduits(level, pos, ingredient.phase, elementId)
        }
        return total
    }

    private fun storedMass(entity: RefiningMachineBlockEntity, elementId: String): Double {
        return entity.stored(elementId)?.mass ?: 0.0
    }

    private fun availableFromContainers(level: ServerLevel, pos: BlockPos, elementId: String): Double {
        var total = 0.0
        for (container in adjacentContainers(level, pos)) {
            conservecraft.common.item.OniItemThermal.equalizeContainerTemperature(container)
            val containerTemp = conservecraft.common.thermal.OniThermalMath.stateOfContainer(container).temperatureK(293.15)
            for (i in 0 until container.containerSize) {
                val stack = container.getItem(i)
                if (stack.isEmpty || OniSolidItems.elementIdOf(stack.item) != elementId) {
                    continue
                }
                total += OniItemMass.stackMass(stack)
            }
        }
        return total
    }

    private fun availableFromConduits(
        level: ServerLevel,
        pos: BlockPos,
        phase: RefiningPhase,
        elementId: String
    ): Double {
        if (phase == RefiningPhase.SOLID) {
            return 0.0
        }
        var total = 0.0
        for (conduit in adjacentConduits(level, pos, phase)) {
            if (conduit.elementId() == elementId) {
                total += conduit.mass()
            }
        }
        return total
    }

    private fun consumeInputs(
        level: ServerLevel,
        pos: BlockPos,
        entity: RefiningMachineBlockEntity,
        inputs: List<Pair<RefiningIngredient, Double>>
    ): InputTemperature {
        var totalMass = 0.0
        var totalHeatCapacity = 0.0
        var totalEnergy = 0.0
        for ((ingredient, required) in inputs) {
            if (required <= 0.0) {
                continue
            }
            val elementId = selectElementForIngredient(level, pos, entity, ingredient) ?: continue
            val taken = takeFromStorage(entity, elementId, required)
            if (taken.mass > 0.0) {
                totalMass += taken.mass
                totalHeatCapacity += taken.mass * taken.specificHeatCapacity
                totalEnergy += taken.mass * taken.specificHeatCapacity * taken.temperatureK
            }
            if (taken.mass + 1e-9 < required) {
                val remaining = required - taken.mass
                val fromContainers = takeFromContainers(level, pos, elementId, remaining)
                totalMass += fromContainers.mass
                totalHeatCapacity += fromContainers.mass * fromContainers.specificHeatCapacity
                totalEnergy += fromContainers.mass * fromContainers.specificHeatCapacity * fromContainers.temperatureK
                val remaining2 = remaining - fromContainers.mass
                if (remaining2 > 1e-9) {
                    val fromConduit = takeFromConduits(level, pos, ingredient.phase, elementId, remaining2)
                    totalMass += fromConduit.mass
                    totalHeatCapacity += fromConduit.mass * fromConduit.specificHeatCapacity
                    totalEnergy += fromConduit.mass * fromConduit.specificHeatCapacity * fromConduit.temperatureK
                }
            }
        }
        val avgTemp = if (totalHeatCapacity > 0.0) totalEnergy / totalHeatCapacity else 293.15
        return InputTemperature(totalMass, avgTemp)
    }

    private data class TakenMass(val mass: Double, val temperatureK: Double, val specificHeatCapacity: Double = 1.0)

    private fun takeFromStorage(entity: RefiningMachineBlockEntity, elementId: String, amount: Double): TakenMass {
        val stored = entity.takeStored(elementId, amount) ?: return TakenMass(0.0, 293.15)
        val specificHeatCapacity = conservecraft.common.element.OniElements.specificHeatCapacityForElementId(elementId) ?: 1.0
        return TakenMass(stored.mass, stored.temperatureK, specificHeatCapacity)
    }

    private fun takeFromContainers(level: ServerLevel, pos: BlockPos, elementId: String, amount: Double): TakenMass {
        if (amount <= 0.0) {
            return TakenMass(0.0, 293.15)
        }
        val specificHeatCapacity = conservecraft.common.element.OniElements.specificHeatCapacityForElementId(elementId) ?: 1.0
        var remaining = amount
        var totalMass = 0.0
        var totalHeatCapacity = 0.0
        var totalEnergy = 0.0
        for (container in adjacentContainers(level, pos)) {
            val containerTemp = conservecraft.common.thermal.OniThermalMath.stateOfContainer(container).temperatureK(293.15)
            for (i in 0 until container.containerSize) {
                val stack = container.getItem(i)
                if (stack.isEmpty || OniSolidItems.elementIdOf(stack.item) != elementId) {
                    continue
                }
                val temp = containerTemp
                val taken = OniItemMass.takeMass(stack, remaining)
                if (taken > 0.0) {
                    totalMass += taken
                    totalHeatCapacity += taken * specificHeatCapacity
                    totalEnergy += taken * specificHeatCapacity * temp
                    remaining -= taken
                }
                if (remaining <= 1e-9) {
                    break
                }
            }
            if (remaining <= 1e-9) {
                break
            }
        }
        val avgTemp = if (totalHeatCapacity > 0.0) totalEnergy / totalHeatCapacity else 293.15
        return TakenMass(totalMass, avgTemp, specificHeatCapacity)
    }

    private fun takeFromConduits(
        level: ServerLevel,
        pos: BlockPos,
        phase: RefiningPhase,
        elementId: String,
        amount: Double
    ): TakenMass {
        if (phase == RefiningPhase.SOLID || amount <= 0.0) {
            return TakenMass(0.0, 293.15)
        }
        val specificHeatCapacity = conservecraft.common.element.OniElements.specificHeatCapacityForElementId(elementId) ?: 1.0
        var remaining = amount
        var totalMass = 0.0
        var totalHeatCapacity = 0.0
        var totalEnergy = 0.0
        for (conduit in adjacentConduits(level, pos, phase)) {
            if (conduit.elementId() != elementId || conduit.mass() <= 0.0) {
                continue
            }
            val available = conduit.mass()
            val taken = minOf(available, remaining)
            if (taken <= 0.0) {
                continue
            }
            totalMass += taken
            totalHeatCapacity += taken * specificHeatCapacity
            totalEnergy += taken * specificHeatCapacity * conduit.temperatureK()
            remaining -= taken
            conduit.setMass(available - taken)
            if (remaining <= 1e-9) {
                break
            }
        }
        val avgTemp = if (totalHeatCapacity > 0.0) totalEnergy / totalHeatCapacity else 293.15
        return TakenMass(totalMass, avgTemp, specificHeatCapacity)
    }

    private fun selectElementForIngredient(
        level: ServerLevel,
        pos: BlockPos,
        entity: RefiningMachineBlockEntity,
        ingredient: RefiningIngredient
    ): String? {
        for (elementId in ingredient.elementIds) {
            if (availableMass(level, pos, entity, ingredient.copy(elementIds = listOf(elementId))) > 0.0) {
                return elementId
            }
        }
        return null
    }

    private fun outputCapacity(
        level: ServerLevel,
        pos: BlockPos,
        entity: RefiningMachineBlockEntity,
        spec: RefiningBuildingSpec,
        output: RefiningOutput
    ): Double {
        if (output.storeOutput) {
            if (output.phase == RefiningPhase.SOLID) {
                return Double.MAX_VALUE
            }
            if (output.phase == RefiningPhase.LIQUID && spec.liquidOutput) {
                return conduitCapacity(level, pos, RefiningPhase.LIQUID, output.elementId)
            }
            if (output.phase == RefiningPhase.GAS && spec.gasOutput) {
                return conduitCapacity(level, pos, RefiningPhase.GAS, output.elementId)
            }
            return Double.MAX_VALUE
        }
        return Double.MAX_VALUE
    }

    private fun conduitCapacity(
        level: ServerLevel,
        pos: BlockPos,
        phase: RefiningPhase,
        elementId: String
    ): Double {
        val maxMass = if (phase == RefiningPhase.LIQUID) 1000.0 else 1.0
        var capacity = 0.0
        for (conduit in adjacentConduits(level, pos, phase)) {
            val current = conduit.mass()
            if (conduit.elementId() != null && conduit.elementId() != elementId) {
                continue
            }
            capacity += (maxMass - current).coerceAtLeast(0.0)
        }
        return capacity
    }

    private fun produceOutputs(
        level: ServerLevel,
        pos: BlockPos,
        entity: RefiningMachineBlockEntity,
        spec: RefiningBuildingSpec,
        outputs: List<Pair<RefiningOutput, Double>>,
        averageInputTemp: Double
    ) {
        for ((output, amount) in outputs) {
            if (amount <= 0.0) {
                continue
            }
            val temp = resolveOutputTemperature(output, averageInputTemp, spec.heatedTemperatureK)
            if (output.storeOutput) {
                when (output.phase) {
                    RefiningPhase.SOLID -> entity.addStored(output.elementId, amount, temp, RefiningPhase.SOLID)
                    RefiningPhase.LIQUID -> {
                        val stored = pushToConduits(level, pos, RefiningPhase.LIQUID, output.elementId, amount, temp)
                        if (stored < amount) {
                            entity.addStored(output.elementId, amount - stored, temp, RefiningPhase.LIQUID)
                        }
                    }
                    RefiningPhase.GAS -> {
                        val stored = pushToConduits(level, pos, RefiningPhase.GAS, output.elementId, amount, temp)
                        if (stored < amount) {
                            entity.addStored(output.elementId, amount - stored, temp, RefiningPhase.GAS)
                        }
                    }
                }
            } else {
                emitToWorld(level, pos, output.phase, output.elementId, amount, temp)
            }
        }
    }

    private fun resolveOutputTemperature(
        output: RefiningOutput,
        averageInputTemp: Double,
        heatedTemperatureK: Double?
    ): Double {
        return when (output.temperatureMode) {
            TemperatureMode.AVERAGE_INPUT -> maxOf(averageInputTemp, output.minTemperatureK)
            TemperatureMode.HEATED -> {
                val base = maxOf(averageInputTemp, output.minTemperatureK)
                if (heatedTemperatureK != null) maxOf(base, heatedTemperatureK) else base
            }
            TemperatureMode.MELTED -> defaultTemperature(output.elementId)
            TemperatureMode.FIXED -> output.fixedTemperatureK ?: output.minTemperatureK
        }
    }

    private fun defaultTemperature(elementId: String): Double {
        val liquid = OniElements.liquidSpec(elementId)
        if (liquid != null) {
            return liquid.defaultTemperatureK
        }
        val gas = OniElements.parseGas(elementId)
        if (gas != null) {
            return gas.defaultTemperature
        }
        return 293.15
    }

    private fun pushToConduits(
        level: ServerLevel,
        pos: BlockPos,
        phase: RefiningPhase,
        elementId: String,
        amount: Double,
        temperatureK: Double
    ): Double {
        val maxMass = if (phase == RefiningPhase.LIQUID) 1000.0 else 1.0
        var remaining = amount
        for (conduit in adjacentConduits(level, pos, phase)) {
            if (conduit.elementId() != null && conduit.elementId() != elementId) {
                continue
            }
            val capacity = (maxMass - conduit.mass()).coerceAtLeast(0.0)
            if (capacity <= 0.0) {
                continue
            }
            val added = minOf(capacity, remaining)
            if (added <= 0.0) {
                continue
            }
            val total = conduit.mass() + added
            val newTemp = if (conduit.mass() <= 0.0) {
                temperatureK
            } else {
                (conduit.temperatureK() * conduit.mass() + temperatureK * added) / total
            }
            conduit.setElementId(elementId)
            conduit.setMass(total)
            conduit.setContentsTemperatureK(newTemp)
            remaining -= added
            if (remaining <= 1e-9) {
                break
            }
        }
        return amount - remaining
    }

    private fun emitToWorld(
        level: ServerLevel,
        pos: BlockPos,
        phase: RefiningPhase,
        elementId: String,
        amount: Double,
        temperatureK: Double
    ) {
        if (phase == RefiningPhase.SOLID) {
            spawnItem(level, pos, elementId, amount, temperatureK)
            return
        }
        val targetPos = if (phase == RefiningPhase.GAS) pos.above() else pos.below()
        val state = when (phase) {
            RefiningPhase.GAS -> gasStateFor(elementId)
            RefiningPhase.LIQUID -> liquidStateFor(elementId)
            else -> null
        } ?: return
        val current = level.getBlockState(targetPos)
        if (!current.isAir && current.block != state.block) {
            return
        }
        if (current.block != state.block) {
            level.setBlock(targetPos, state, 2)
        }
        val matter = OniMatterAccess.matterEntity(level, targetPos) ?: return
        val total = matter.mass() + amount
        val newTemp = if (matter.mass() <= 0.0) {
            temperatureK
        } else {
            (matter.temperatureK() * matter.mass() + temperatureK * amount) / total
        }
        matter.setMass(total)
        matter.setTemperatureK(newTemp)
    }

    private fun gasStateFor(elementId: String): net.minecraft.world.level.block.state.BlockState? {
        return when (elementId) {
            RefiningCatalog.Elements.OXYGEN -> OniBlockLookup.state(OniBlockFactory.OXYGEN_GAS)
            RefiningCatalog.Elements.CARBON_DIOXIDE -> OniBlockLookup.state(OniBlockFactory.CARBON_DIOXIDE_GAS)
            RefiningCatalog.Elements.HYDROGEN -> OniBlockLookup.state(OniBlockFactory.HYDROGEN_GAS)
            RefiningCatalog.Elements.METHANE -> OniBlockLookup.state(OniBlockFactory.METHANE_GAS)
            RefiningCatalog.Elements.STEAM -> OniBlockLookup.state(OniBlockFactory.STEAM_GAS)
            RefiningCatalog.Elements.CHLORINE -> OniBlockLookup.state(OniBlockFactory.CHLORINE_GAS)
            else -> null
        }
    }

    private fun liquidStateFor(elementId: String): net.minecraft.world.level.block.state.BlockState? {
        return when (elementId) {
            RefiningCatalog.Elements.WATER -> OniBlockLookup.state(OniBlockFactory.WATER)
            RefiningCatalog.Elements.POLLUTED_WATER -> OniBlockLookup.state(OniBlockFactory.POLLUTED_WATER)
            RefiningCatalog.Elements.CRUDE_OIL -> OniBlockLookup.state(OniBlockFactory.CRUDE_OIL)
            RefiningCatalog.Elements.LAVA -> OniBlockLookup.state(OniBlockFactory.LAVA)
            RefiningCatalog.Elements.SALT_WATER -> OniBlockLookup.state(OniBlockFactory.SALT_WATER)
            RefiningCatalog.Elements.BRINE -> OniBlockLookup.state(OniBlockFactory.BRINE)
            RefiningCatalog.Elements.ETHANOL -> OniBlockLookup.state(OniBlockFactory.ETHANOL)
            RefiningCatalog.Elements.PETROLEUM -> OniBlockLookup.state(OniBlockFactory.PETROLEUM)
            RefiningCatalog.Elements.MILK -> OniBlockLookup.state(OniBlockFactory.MILK)
            RefiningCatalog.Elements.NATURAL_RESIN -> OniBlockLookup.state(OniBlockFactory.NATURAL_RESIN)
            RefiningCatalog.Elements.PHYTO_OIL -> OniBlockLookup.state(OniBlockFactory.PHYTO_OIL)
            RefiningCatalog.Elements.MOLTEN_GLASS -> OniBlockLookup.state(OniBlockFactory.MOLTEN_GLASS)
            RefiningCatalog.Elements.SUPER_COOLANT -> OniBlockLookup.state(OniBlockFactory.SUPER_COOLANT)
            RefiningCatalog.Elements.VISCO_GEL -> OniBlockLookup.state(OniBlockFactory.VISCO_GEL)
            else -> null
        }
    }

    private fun flushSolidOutputs(level: ServerLevel, pos: BlockPos, entity: RefiningMachineBlockEntity) {
        val stored = entity.storage().values.toList()
        for (entry in stored) {
            if (entry.phase != RefiningPhase.SOLID || entry.mass <= 0.0) {
                continue
            }
            val stacks = OniSolidItems.encode(entry.elementId, entry.mass, entry.temperatureK)
            entity.takeStored(entry.elementId, entry.mass)
            var leftoverMass = 0.0
            for (stack in stacks) {
                val remaining = mergeIntoAdjacentContainers(level, pos, stack)
                leftoverMass += OniItemMass.stackMass(remaining)
            }
            if (leftoverMass > 0.0) {
                entity.addStored(entry.elementId, leftoverMass, entry.temperatureK, RefiningPhase.SOLID)
            }
        }
    }

    private fun spawnItem(level: ServerLevel, pos: BlockPos, elementId: String, amount: Double, temperatureK: Double) {
        for (stack in OniSolidItems.encode(elementId, amount, temperatureK)) {
            val remainder = mergeIntoAdjacentContainers(level, pos, stack)
            if (!remainder.isEmpty) {
                net.minecraft.world.entity.item.ItemEntity(level, pos.x + 0.5, pos.y + 1.0, pos.z + 0.5, remainder).let {
                    level.addFreshEntity(it)
                }
            }
        }
    }

    private fun mergeIntoAdjacentContainers(level: ServerLevel, pos: BlockPos, stack: ItemStack): ItemStack {
        var remaining = stack
        for (container in adjacentContainers(level, pos)) {
            if (remaining.isEmpty) {
                break
            }
            remaining = OniItemMass.mergeIntoContainer(container, remaining)
        }
        return remaining
    }

    private fun stackTemperature(stack: ItemStack): Double {
        val data = stack.get(DataComponents.CUSTOM_DATA) ?: return 293.15
        val tag = data.copyTag()
        if (!tag.contains(BottledMatterItem.TAG_TEMP_K)) {
            return 293.15
        }
        return tag.getDouble(BottledMatterItem.TAG_TEMP_K).orElse(293.15)
    }

    private fun setStackTemperature(stack: ItemStack, temperatureK: Double) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack) { root ->
            root.putDouble(BottledMatterItem.TAG_TEMP_K, temperatureK)
        }
    }

    private fun adjacentContainers(level: ServerLevel, pos: BlockPos): List<Container> {
        val containers = ArrayList<Container>()
        for (dir in Direction.values()) {
            val neighborPos = pos.offset(dir.stepX, dir.stepY, dir.stepZ)
            val entity = level.getBlockEntity(neighborPos)
            val container = entity as? Container ?: continue
            containers.add(container)
        }
        return containers
    }

    private fun adjacentConduits(level: ServerLevel, pos: BlockPos, phase: RefiningPhase): List<OniConduitBlockEntity> {
        val result = ArrayList<OniConduitBlockEntity>()
        val targetBlock = when (phase) {
            RefiningPhase.LIQUID -> OniBlockLookup.block(OniBlockFactory.LIQUID_CONDUIT)
            RefiningPhase.GAS -> OniBlockLookup.block(OniBlockFactory.GAS_CONDUIT)
            RefiningPhase.SOLID -> null
        } ?: return result
        for (dir in Direction.values()) {
            val neighborPos = pos.offset(dir.stepX, dir.stepY, dir.stepZ)
            val state = level.getBlockState(neighborPos)
            if (state.block != targetBlock) {
                continue
            }
            val entity = level.getBlockEntity(neighborPos) as? OniConduitBlockEntity ?: continue
            result.add(entity)
        }
        return result
    }

    companion object {
        private const val ONI_RATE_SCALE = 1.0 / 20.0
    }
}
