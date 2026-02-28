package mconi.common.sim.power

import net.minecraft.core.BlockPos

data class OniPowerNetwork(
    val circuits: List<Circuit>,
    val transformerLinks: List<TransformerLink>,
    val consumerPoweredByPos: Set<Long>,
    val batteryEnergyByPos: Map<Long, Double>,
    val totalGenerationW: Double,
    val totalDemandW: Double,
    val totalStoredEnergyJ: Double,
    val tripped: Boolean,
) {
    // Circuit is a connected component of wire blocks and attached devices.
    data class Circuit(
        val id: Int,
        val wireCapacityW: Double,
        val generationW: Double,
        val demandW: Double,
        val batteryCapacityJ: Double,
        val batteryEnergyJ: Double,
        val powered: Boolean,
        val overloaded: Boolean,
    )

    // TransformerLink connects two circuits with a capped throughput.
    data class TransformerLink(
        val pos: BlockPos,
        val circuitA: Int,
        val circuitB: Int,
        val capacityW: Double,
        val transferW: Double,
    )
}
