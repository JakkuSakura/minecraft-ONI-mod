package conservecraft.common.world

import conservecraft.common.block.OniBlockFactory
import conservecraft.common.block.OniBlockLookup
import conservecraft.common.element.OniElements
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids

object OniVanillaFluidInterop {
    private const val VANILLA_FLUID_FULL_LEVEL = 8.0

    fun isVanillaManagedFluid(fluidState: FluidState): Boolean {
        return fluidState.`is`(Fluids.WATER) ||
            fluidState.`is`(Fluids.FLOWING_WATER) ||
            fluidState.`is`(Fluids.LAVA) ||
            fluidState.`is`(Fluids.FLOWING_LAVA)
    }

    fun oniLiquidId(fluidState: FluidState): String? {
        return when {
            fluidState.`is`(Fluids.WATER) || fluidState.`is`(Fluids.FLOWING_WATER) -> OniElements.LIQUID_WATER
            fluidState.`is`(Fluids.LAVA) || fluidState.`is`(Fluids.FLOWING_LAVA) -> OniElements.LIQUID_LAVA
            else -> null
        }
    }

    fun massFor(fluidState: FluidState): Double {
        val liquidId = oniLiquidId(fluidState) ?: return 0.0
        val spec = OniElements.liquidSpec(liquidId) ?: return 0.0
        val fraction = (fluidState.amount.toDouble() / VANILLA_FLUID_FULL_LEVEL).coerceIn(0.0, 1.0)
        return spec.defaultMass * fraction
    }

    fun defaultTemperatureK(fluidState: FluidState): Double {
        val liquidId = oniLiquidId(fluidState) ?: return OniItemDefaults.DEFAULT_TEMP_K
        return OniElements.liquidSpec(liquidId)?.defaultTemperatureK ?: OniItemDefaults.DEFAULT_TEMP_K
    }

    fun convertVanillaFluid(level: ServerLevel, pos: BlockPos, fluidState: FluidState): Boolean {
        val liquidId = oniLiquidId(fluidState) ?: return false
        val targetState = when (liquidId) {
            OniElements.LIQUID_WATER -> OniBlockLookup.state(OniBlockFactory.WATER)
            OniElements.LIQUID_LAVA -> OniBlockLookup.state(OniBlockFactory.LAVA)
            else -> Blocks.AIR.defaultBlockState()
        }
        if (targetState.isAir) {
            return false
        }
        val temperatureK = defaultTemperatureK(fluidState)
        val mass = massFor(fluidState)
        level.setBlock(pos, targetState, 2)
        val entity = OniMatterAccess.matterEntity(level, pos) ?: return true
        entity.ensureContents(liquidId, mass, temperatureK)
        return true
    }
}

private object OniItemDefaults {
    const val DEFAULT_TEMP_K: Double = 293.15
}
