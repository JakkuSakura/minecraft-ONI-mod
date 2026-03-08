package conservecraft.common.world

import conservecraft.common.block.OniBlockFactory
import conservecraft.common.block.OniBlockLookup
import conservecraft.common.element.OniElements
import conservecraft.common.item.BottledMatterItem
import conservecraft.common.item.OniItemMass
import conservecraft.common.item.OniItemThermal
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.stats.Stats
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potions
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.BlockHitResult

object OniVanillaFluidItemInterop {
    private const val MIN_REMAINING_MASS = 1e-6

    fun shouldHandleBucketUse(stack: ItemStack): Boolean {
        return stack.`is`(Items.BUCKET) || isSupportedFluidBucket(stack)
    }

    fun shouldDisableCauldronInteraction(state: BlockState, stack: ItemStack): Boolean {
        if (isSupportedFluidCauldron(state)) {
            return true
        }
        if (!state.`is`(Blocks.CAULDRON)) {
            return false
        }
        return isSupportedFluidBucket(stack) || isWaterPotion(stack)
    }

    fun isSupportedFluidCauldron(state: BlockState): Boolean {
        return state.`is`(Blocks.WATER_CAULDRON) || state.`is`(Blocks.LAVA_CAULDRON)
    }

    fun isSupportedFluidBucket(stack: ItemStack): Boolean = isSupportedFluidBucket(stack.item)

    fun isSupportedFluidBucket(item: Item): Boolean {
        return item == Items.WATER_BUCKET || item == Items.LAVA_BUCKET
    }

    fun isWaterPotion(stack: ItemStack): Boolean {
        if (!stack.`is`(Items.POTION)) {
            return false
        }
        val contents = stack.get(DataComponents.POTION_CONTENTS) ?: return false
        return contents.`is`(Potions.WATER)
    }

    fun playerUse(level: Level, player: Player, hand: InteractionHand, heldStack: ItemStack): InteractionResult? {
        if (heldStack.`is`(Items.BUCKET)) {
            val hitResult = playerBlockHitResult(player) ?: return null
            return tryPickupFromOniLiquid(level, player, hand, heldStack, hitResult)
        }

        val liquidId = liquidIdForBucketItem(heldStack.item) ?: return null
        val hitResult = playerBlockHitResult(player) ?: return InteractionResult.PASS

        val clickedPos = hitResult.blockPos
        val clickedState = level.getBlockState(clickedPos)
        val placePos = resolvePlacementPos(clickedPos, clickedState, hitResult.direction, liquidId)
        if (!level.mayInteract(player, clickedPos) || !player.mayUseItemAt(placePos, hitResult.direction, heldStack)) {
            return InteractionResult.FAIL
        }
        if (!canPlaceLiquid(level, placePos, liquidId)) {
            return InteractionResult.FAIL
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        }

        if (!placeLiquid(level, placePos, liquidId, bucketMass(heldStack, liquidId), bucketTemperatureK(heldStack, liquidId))) {
            return InteractionResult.FAIL
        }
        if (!player.hasInfiniteMaterials()) {
            player.setItemInHand(hand, net.minecraft.world.item.BucketItem.getEmptySuccessItem(heldStack, player))
        }
        player.awardStat(Stats.ITEM_USED.get(heldStack.item))
        playEmptySound(level, placePos, liquidId)
        return InteractionResult.SUCCESS
    }

    fun placeLiquidFromBucket(level: Level, pos: BlockPos, fluid: Fluid): Boolean {
        val liquidId = liquidIdForFluid(fluid) ?: return false
        return placeLiquid(level, pos, liquidId, bucketMass(liquidId), defaultTemperatureK(liquidId))
    }

    private fun playerBlockHitResult(player: Player): BlockHitResult? {
        val hitResult = player.pick(player.blockInteractionRange(), 0.0f, false)
        return hitResult as? BlockHitResult
    }

    private fun tryPickupFromOniLiquid(
        level: Level,
        player: Player,
        hand: InteractionHand,
        heldStack: ItemStack,
        hitResult: BlockHitResult,
    ): InteractionResult? {
        val pos = hitResult.blockPos
        if (!level.mayInteract(player, pos) || !player.mayUseItemAt(pos, hitResult.direction, heldStack)) {
            return InteractionResult.FAIL
        }

        val state = level.getBlockState(pos)
        val liquidId = OniMatterAccess.liquidId(state) ?: return null
        if (!isSupportedBucketLiquid(liquidId)) {
            return null
        }

        val entity = OniMatterAccess.matterEntity(level, pos)
        val availableMass = entity?.mass() ?: bucketMass(liquidId)
        if (availableMass <= 0.0) {
            return InteractionResult.FAIL
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        }

        val takenMass = minOf(availableMass, bucketMass(liquidId))
        val temperatureK = entity?.temperatureK() ?: defaultTemperatureK(liquidId)
        val resultStack = ItemStack(bucketItemForLiquid(liquidId) ?: return InteractionResult.FAIL)
        OniItemMass.setStackMass(resultStack, takenMass)
        OniItemThermal.setTemperatureK(resultStack, temperatureK)

        val remainingMass = availableMass - takenMass
        if (remainingMass <= MIN_REMAINING_MASS) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3)
        } else {
            entity?.ensureContents(liquidId, remainingMass, temperatureK)
        }
        if (!player.hasInfiniteMaterials()) {
            player.setItemInHand(hand, resultStack)
        }
        player.awardStat(Stats.ITEM_USED.get(heldStack.item))
        playFillSound(level, pos, liquidId)
        return InteractionResult.SUCCESS
    }

    private fun canPlaceLiquid(level: Level, pos: BlockPos, liquidId: String): Boolean {
        val state = level.getBlockState(pos)
        val currentLiquidId = OniMatterAccess.liquidId(state)
        if (currentLiquidId != null) {
            return currentLiquidId == liquidId
        }
        val vanillaLiquidId = OniVanillaFluidInterop.oniLiquidId(state.fluidState)
        if (vanillaLiquidId != null) {
            return vanillaLiquidId == liquidId
        }
        val fluid = fluidForLiquidId(liquidId) ?: return false
        return state.isAir || state.canBeReplaced(fluid)
    }

    private fun placeLiquid(level: Level, pos: BlockPos, liquidId: String, addedMass: Double, addedTemperatureK: Double): Boolean {
        if (addedMass <= 0.0) {
            return false
        }
        val currentState = level.getBlockState(pos)
        if (OniVanillaFluidInterop.isVanillaManagedFluid(currentState.fluidState)) {
            val serverLevel = level as? net.minecraft.server.level.ServerLevel ?: return false
            OniVanillaFluidInterop.convertVanillaFluid(serverLevel, pos, currentState.fluidState)
        }
        val normalizedState = level.getBlockState(pos)
        val currentLiquidId = OniMatterAccess.liquidId(normalizedState)
        if (currentLiquidId != null) {
            if (currentLiquidId != liquidId) {
                return false
            }
            val entity = OniMatterAccess.matterEntity(level, pos) ?: return false
            val currentMass = entity.mass()
            val currentTemperatureK = entity.temperatureK()
            val totalMass = currentMass + addedMass
            val totalEnergy = currentMass * currentTemperatureK + addedMass * addedTemperatureK
            entity.ensureContents(liquidId, totalMass, totalEnergy / totalMass)
            return true
        }

        val fluid = fluidForLiquidId(liquidId) ?: return false
        if (!normalizedState.isAir && !normalizedState.canBeReplaced(fluid)) {
            return false
        }

        val targetState = when (liquidId) {
            OniElements.LIQUID_WATER -> OniBlockLookup.state(OniBlockFactory.WATER)
            OniElements.LIQUID_LAVA -> OniBlockLookup.state(OniBlockFactory.LAVA)
            else -> return false
        }
        level.setBlock(pos, targetState, 3)
        val entity = OniMatterAccess.matterEntity(level, pos) ?: return false
        entity.ensureContents(liquidId, addedMass, addedTemperatureK)
        return true
    }

    private fun resolvePlacementPos(clickedPos: BlockPos, clickedState: BlockState, direction: Direction, liquidId: String): BlockPos {
        if (isSameLiquid(clickedState, liquidId)) {
            return clickedPos
        }
        val fluid = fluidForLiquidId(liquidId) ?: Fluids.EMPTY
        if (clickedState.canBeReplaced(fluid)) {
            return clickedPos
        }
        return clickedPos.relative(direction)
    }

    private fun isSameLiquid(state: BlockState, liquidId: String): Boolean {
        val oniLiquidId = OniMatterAccess.liquidId(state)
        if (oniLiquidId != null) {
            return oniLiquidId == liquidId
        }
        val vanillaLiquidId = OniVanillaFluidInterop.oniLiquidId(state.fluidState)
        return vanillaLiquidId == liquidId
    }

    private fun playFillSound(level: Level, pos: BlockPos, liquidId: String) {
        val sound = when (liquidId) {
            OniElements.LIQUID_LAVA -> SoundEvents.BUCKET_FILL_LAVA
            else -> SoundEvents.BUCKET_FILL
        }
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0f, 1.0f)
    }

    private fun playEmptySound(level: Level, pos: BlockPos, liquidId: String) {
        val sound = when (liquidId) {
            OniElements.LIQUID_LAVA -> SoundEvents.BUCKET_EMPTY_LAVA
            else -> SoundEvents.BUCKET_EMPTY
        }
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0f, 1.0f)
    }

    private fun bucketMass(stack: ItemStack, liquidId: String): Double {
        return if (OniItemThermal.hasMassTag(stack)) {
            OniItemMass.stackMass(stack)
        } else {
            bucketMass(liquidId)
        }
    }

    private fun bucketTemperatureK(stack: ItemStack, liquidId: String): Double {
        return if (hasTemperatureTag(stack)) {
            OniItemThermal.temperatureK(stack)
        } else {
            defaultTemperatureK(liquidId)
        }
    }

    private fun hasTemperatureTag(stack: ItemStack): Boolean {
        val data = stack.get(DataComponents.CUSTOM_DATA) ?: return false
        return data.copyTag().contains(BottledMatterItem.TAG_TEMP_K)
    }

    private fun bucketMass(liquidId: String): Double {
        return OniElements.liquidSpec(liquidId)?.bottledMass() ?: 0.0
    }

    private fun defaultTemperatureK(liquidId: String): Double {
        return OniElements.liquidSpec(liquidId)?.bottledTemperatureK() ?: OniItemThermal.DEFAULT_TEMP_K
    }

    private fun liquidIdForBucketItem(item: Item): String? {
        return when (item) {
            Items.WATER_BUCKET -> OniElements.LIQUID_WATER
            Items.LAVA_BUCKET -> OniElements.LIQUID_LAVA
            else -> null
        }
    }

    private fun liquidIdForFluid(fluid: Fluid): String? {
        return when (fluid) {
            Fluids.WATER, Fluids.FLOWING_WATER -> OniElements.LIQUID_WATER
            Fluids.LAVA, Fluids.FLOWING_LAVA -> OniElements.LIQUID_LAVA
            else -> null
        }
    }

    private fun fluidForLiquidId(liquidId: String): Fluid? {
        return when (liquidId) {
            OniElements.LIQUID_WATER -> Fluids.WATER
            OniElements.LIQUID_LAVA -> Fluids.LAVA
            else -> null
        }
    }

    private fun bucketItemForLiquid(liquidId: String): Item? {
        return when (liquidId) {
            OniElements.LIQUID_WATER -> Items.WATER_BUCKET
            OniElements.LIQUID_LAVA -> Items.LAVA_BUCKET
            else -> null
        }
    }

    private fun isSupportedBucketLiquid(liquidId: String): Boolean {
        return liquidId == OniElements.LIQUID_WATER || liquidId == OniElements.LIQUID_LAVA
    }
}
