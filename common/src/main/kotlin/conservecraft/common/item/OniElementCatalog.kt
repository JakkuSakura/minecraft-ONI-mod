package conservecraft.common.item

import conservecraft.common.AbstractModBootstrap

object OniElementCatalog {
    private fun modId(id: String): String = "${AbstractModBootstrap.MOD_ID}:$id"

    val METAL_ORE: List<String> = listOf(
        modId(OniItemFactory.ELEMENT_METAL_ORE),
        modId(OniItemFactory.ELEMENT_IRON_ORE),
        modId(OniItemFactory.ELEMENT_COPPER_ORE),
        modId(OniItemFactory.ELEMENT_GOLD_AMALGAM),
        modId(OniItemFactory.ELEMENT_ALUMINUM_ORE),
        modId(OniItemFactory.ELEMENT_COBALTITE),
        modId(OniItemFactory.ELEMENT_TUNGSTEN_ORE)
    )

    val REFINED_METAL: List<String> = listOf(
        modId(OniItemFactory.ELEMENT_REFINED_METAL),
        modId(OniItemFactory.ELEMENT_IRON),
        modId(OniItemFactory.ELEMENT_COPPER),
        modId(OniItemFactory.ELEMENT_GOLD),
        modId(OniItemFactory.ELEMENT_ALUMINUM),
        modId(OniItemFactory.ELEMENT_COBALT),
        modId(OniItemFactory.ELEMENT_TUNGSTEN),
        modId(OniItemFactory.ELEMENT_STEEL)
    )

    val RAW_MINERAL: List<String> = listOf(
        modId(OniItemFactory.ELEMENT_SEDIMENTARY_ROCK),
        modId(OniItemFactory.ELEMENT_IGNEOUS_ROCK),
        modId(OniItemFactory.ELEMENT_GRANITE),
        modId(OniItemFactory.ELEMENT_ABYSSALITE),
        modId(OniItemFactory.ELEMENT_REGOLITH)
    )
}
