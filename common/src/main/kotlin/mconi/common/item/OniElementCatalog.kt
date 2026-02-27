package mconi.common.item

import mconi.common.AbstractModInitializer

object OniElementCatalog {
    private fun modId(id: String): String = "${AbstractModInitializer.MOD_ID}:$id"

    val METAL_ORE: List<String> = listOf(
        modId(OniItemFactory.ELEMENT_METAL_ORE)
    )

    val REFINED_METAL: List<String> = listOf(
        modId(OniItemFactory.ELEMENT_REFINED_METAL)
    )

    val RAW_MINERAL: List<String> = listOf(
        modId(OniItemFactory.ELEMENT_SEDIMENTARY_ROCK),
        modId(OniItemFactory.ELEMENT_IGNEOUS_ROCK),
        modId(OniItemFactory.ELEMENT_GRANITE),
        modId(OniItemFactory.ELEMENT_ABYSSALITE),
        modId(OniItemFactory.ELEMENT_REGOLITH)
    )
}
