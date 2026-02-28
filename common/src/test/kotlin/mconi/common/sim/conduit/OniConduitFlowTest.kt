package mconi.common.sim.conduit

import net.minecraft.core.BlockPos
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class OniConduitFlowTest {
    @Test
    fun flowConservesMassAndMixesTemperature() {
        val a = BlockPos(0, 0, 0)
        val b = BlockPos(1, 0, 0)
        val contents = mapOf(
            a to OniConduitFlow.Contents("water", 100.0, 300.0),
            b to OniConduitFlow.Contents("water", 0.0, 250.0)
        )
        val result = OniConduitFlow.step(
            listOf(a, b),
            contents,
            OniConduitFlow.FlowConfig(maxMass = 100.0, movePerStep = 50.0)
        )
        val nextA = result.contentsByPos[a]
        val nextB = result.contentsByPos[b]
        assertNotNull(nextA)
        assertNotNull(nextB)
        assertEquals(50.0, nextA.mass, 1e-6)
        assertEquals(50.0, nextB.mass, 1e-6)
        val expectedTempB = (50.0 * 300.0) / 50.0
        assertEquals(expectedTempB, nextB.temperatureK, 1e-6)
        assertEquals(300.0, nextA.temperatureK, 1e-6)
    }

    @Test
    fun flowBlocksDifferentElements() {
        val a = BlockPos(0, 0, 0)
        val b = BlockPos(1, 0, 0)
        val contents = mapOf(
            a to OniConduitFlow.Contents("water", 100.0, 300.0),
            b to OniConduitFlow.Contents("oil", 50.0, 280.0)
        )
        val result = OniConduitFlow.step(
            listOf(a, b),
            contents,
            OniConduitFlow.FlowConfig(maxMass = 100.0, movePerStep = 50.0)
        )
        val nextA = result.contentsByPos[a]
        val nextB = result.contentsByPos[b]
        assertNotNull(nextA)
        assertNotNull(nextB)
        assertEquals(100.0, nextA.mass, 1e-6)
        assertEquals(50.0, nextB.mass, 1e-6)
        assertEquals("water", nextA.elementId)
        assertEquals("oil", nextB.elementId)
    }
}
