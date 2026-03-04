package conservecraft.common.block.entity

import conservecraft.common.block.OniBlockLookup
import conservecraft.common.block.OniBlockFactory
import conservecraft.common.item.OniItemMass
import conservecraft.common.item.OniItemFactory
import conservecraft.common.element.ElementContents
import conservecraft.common.element.OniElements
import conservecraft.common.item.OniBlueprintRegistry
import conservecraft.common.item.OniBlueprintSelection
import conservecraft.common.item.OniBlueprintTargets
import conservecraft.common.sim.OniConstructionRates
import conservecraft.common.sim.OniServices
import net.minecraft.core.BlockPos
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.state.BlockState
import java.util.UUID

class ConstructionSiteBlockEntity(pos: BlockPos, state: BlockState) :
    OniElementBlockEntity(OniBlockEntityTypes.CONSTRUCTION_SITE, pos, state) {

    data class MaterialSlotState(
        val slotId: String,
        val requiredAmount: Int,
        var selectedItemId: String,
        var depositedAmount: Int,
    )

    enum class Action {
        DELIVER,
        BUILD
    }

    private var blueprintId: String = ""
    private var buildTimeSeconds: Int = 0
    private var buildProgressSeconds: Double = 0.0
    private var pausedReason: String = ""
    private val materialSlots: MutableList<MaterialSlotState> = ArrayList()

    private var activePlayer: UUID? = null
    private var activeAction: Action? = null
    private var lastInteractionTick: Long = 0L
    private var materialCarry: Double = 0.0

    fun applySelection(selection: OniBlueprintSelection, player: Player?) {
        val blueprint = OniBlueprintRegistry.get(selection.blueprintId) ?: return
        blueprintId = blueprint.id
        buildTimeSeconds = blueprint.buildTimeSeconds
        buildProgressSeconds = 0.0
        pausedReason = ""
        materialSlots.clear()
        for (slot in blueprint.materialSlots) {
            val selected = selection.materials.firstOrNull { it.slotId == slot.slotId }?.itemId
            val validSelection = if (selected != null && slot.allowedItems.contains(selected)) {
                selected
            } else {
                slot.allowedItems.firstOrNull().orEmpty()
            }
            materialSlots.add(
                MaterialSlotState(
                    slotId = slot.slotId,
                    requiredAmount = slot.amount,
                    selectedItemId = validSelection,
                    depositedAmount = 0
                )
            )
        }
        activePlayer = player?.uuid
        activeAction = null
        lastInteractionTick = level?.gameTime ?: 0L
        materialCarry = 0.0
        setChanged()
    }

    fun startInteraction(player: Player) {
        if (blueprintId.isBlank()) {
            pausedReason = "Missing blueprint"
            return
        }
        activePlayer = player.uuid
        lastInteractionTick = level?.gameTime ?: 0L
        activeAction = if (allMaterialsDelivered()) Action.BUILD else Action.DELIVER
        setChanged()
    }

    fun refundStacks(): MutableList<net.minecraft.world.item.ItemStack> {
        val refunds: MutableList<net.minecraft.world.item.ItemStack> = ArrayList()
        for (slot in materialSlots) {
            val item = OniItemFactory.itemById(slot.selectedItemId) ?: continue
            if (slot.depositedAmount <= 0) {
                continue
            }
            val stack = net.minecraft.world.item.ItemStack(item, 1)
            OniItemMass.setStackMass(stack, slot.depositedAmount.toDouble())
            refunds.add(stack)
        }
        return OniItemMass.mergeStacksByMass(refunds).toMutableList()
    }

    fun appendJadeLines(lines: MutableList<String>) {
        if (blueprintId.isBlank()) {
            lines.add("Construction Site: <empty>")
            return
        }
        lines.add("Construction Site: $blueprintId")
        for (slot in materialSlots) {
            val itemName = slot.selectedItemId.ifBlank { "<unselected>" }
            lines.add("- $itemName ${slot.depositedAmount}/${slot.requiredAmount}")
        }
        lines.add("Progress: ${"%.1f".format(buildProgressSeconds)}/${buildTimeSeconds}s")
        if (pausedReason.isNotBlank()) {
            lines.add("Paused: $pausedReason")
        }
    }

    fun serverTick(level: ServerLevel) {
        if (activePlayer == null || activeAction == null) {
            return
        }
        if (level.gameTime - lastInteractionTick > INTERACTION_GRACE_TICKS) {
            activePlayer = null
            activeAction = null
            return
        }

        val playerId = activePlayer ?: return
        val player = level.server.playerList.getPlayer(playerId) ?: return
        if (player.distanceToSqr(blockPos.x + 0.5, blockPos.y + 0.5, blockPos.z + 0.5) > 16.0) {
            pausedReason = "Out of range"
            return
        }

        val multiplier = OniConstructionRates.speedMultiplier(OniServices.systemRuntime().stressState().score(player))
        val deltaSeconds = 1.0 / 20.0

        if (!allMaterialsDelivered()) {
            activeAction = Action.DELIVER
            val slot = nextMaterialSlot() ?: return
            val item = OniItemFactory.itemById(slot.selectedItemId)
            if (item == null) {
                pausedReason = "Invalid material"
                return
            }
            val perSecond = OniConstructionRates.materialsPerSecond(multiplier)
            materialCarry = (materialCarry + perSecond * deltaSeconds).coerceAtMost(1.0)
            val requested = materialCarry.toInt()
            if (requested > 0) {
                val remaining = slot.requiredAmount - slot.depositedAmount
                val toDeposit = minOf(requested, remaining)
                val deposited = depositFromPlayer(player, item, toDeposit)
                slot.depositedAmount += deposited
                materialCarry -= deposited.toDouble()
                pausedReason = when {
                    deposited == 0 && remaining > 0 -> "Missing materials"
                    remaining > 0 -> "Delivering materials"
                    else -> ""
                }
            }
            setChanged()
            return
        }

        activeAction = Action.BUILD
        val buildRate = OniConstructionRates.buildSecondsPerSecond(multiplier)
        buildProgressSeconds += buildRate * deltaSeconds
        if (buildProgressSeconds >= buildTimeSeconds.toDouble()) {
            completeConstruction(level, player)
        } else {
            pausedReason = "Building"
            setChanged()
        }
    }

    private fun completeConstruction(level: ServerLevel, player: Player) {
        val targetId = OniBlueprintTargets.blockIdFor(blueprintId)
        if (targetId == null) {
            pausedReason = "Missing target block"
            return
        }
        val state = OniBlockLookup.state(targetId)
        level.setBlock(blockPos, state, 3)
        val materials = materialSlots
            .filter { it.selectedItemId.isNotBlank() && it.requiredAmount > 0 }
            .mapNotNull { slot ->
                val elementId = OniElements.elementIdForItemId(slot.selectedItemId) ?: return@mapNotNull null
                ElementContents(
                    elementId = elementId,
                    mass = slot.requiredAmount.toDouble(),
                    temperatureK = 293.15
                )
            }
        if (materials.isNotEmpty()) {
            val targetEntity = level.getBlockEntity(blockPos) as? OniElementBlockEntity
            targetEntity?.setElements(materials)
        }
        activePlayer = null
        activeAction = null
    }

    private fun nextMaterialSlot(): MaterialSlotState? {
        return materialSlots.firstOrNull { it.depositedAmount < it.requiredAmount }
    }

    private fun allMaterialsDelivered(): Boolean {
        return materialSlots.all { it.depositedAmount >= it.requiredAmount }
    }

    private fun depositFromPlayer(player: Player, item: net.minecraft.world.item.Item, needed: Int): Int {
        if (needed <= 0) {
            return 0
        }

        val inventory = player.inventory
        var remaining = needed.toDouble()
        var deposited = 0.0

        val size = inventory.containerSize
        for (i in 0 until size) {
            val stack = inventory.getItem(i)
            if (stack.isEmpty) {
                continue
            }
            if (stack.item != item) {
                continue
            }
            val take = minOf(remaining, OniItemMass.stackMass(stack))
            if (take <= 0.0) {
                continue
            }
            val actual = OniItemMass.takeMass(stack, take)
            deposited += actual
            remaining -= actual
            if (remaining <= 0.0) {
                break
            }
        }

        return deposited.toInt()
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.putString("blueprint_id", blueprintId)
        output.putInt("build_time", buildTimeSeconds)
        output.putDouble("build_progress", buildProgressSeconds)
        output.putString("paused_reason", pausedReason)
        val list = output.childrenList("materials")
        for (slot in materialSlots) {
            val entry = list.addChild()
            entry.putString("slot_id", slot.slotId)
            entry.putInt("required", slot.requiredAmount)
            entry.putInt("deposited", slot.depositedAmount)
            entry.putString("item", slot.selectedItemId)
        }
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        blueprintId = input.getStringOr("blueprint_id", "")
        buildTimeSeconds = input.getIntOr("build_time", 0)
        buildProgressSeconds = input.getDoubleOr("build_progress", 0.0)
        pausedReason = input.getStringOr("paused_reason", "")
        materialSlots.clear()
        val list = input.childrenListOrEmpty("materials")
        list.stream().forEach { entry ->
            materialSlots.add(
                MaterialSlotState(
                    slotId = entry.getStringOr("slot_id", ""),
                    requiredAmount = entry.getIntOr("required", 0),
                    selectedItemId = entry.getStringOr("item", ""),
                    depositedAmount = entry.getIntOr("deposited", 0)
                )
            )
        }
    }

    companion object {
        private const val INTERACTION_GRACE_TICKS = 2L
    }
}
