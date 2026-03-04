package conservecraft.common.item

import conservecraft.common.block.OniBlockFactory
import kotlin.test.Test
import kotlin.test.assertEquals

class OniBlueprintTargetsTest {
    @Test
    fun resolvesBlockIdsForKnownBlueprints() {
        assertEquals(OniBlockFactory.OXYGEN_DIFFUSER, OniBlueprintTargets.blockIdFor("oxygen_diffuser"))
        assertEquals("oxygen_diffuser", OniBlueprintTargets.blueprintIdFor(OniBlockFactory.OXYGEN_DIFFUSER))
    }
}
