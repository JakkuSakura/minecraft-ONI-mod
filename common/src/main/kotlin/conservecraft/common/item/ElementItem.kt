package conservecraft.common.item

import net.minecraft.world.item.Item

class ElementItem(properties: Item.Properties, elementId: String, unitMassKg: Double) :
    SolidElementItem(properties, elementId, unitMassKg, null)
