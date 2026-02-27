package mconi.common.item

import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomData

private const val BLUEPRINT_TAG = "mconi_blueprint"
private const val BLUEPRINT_ID = "blueprint_id"
private const val MATERIALS_TAG = "materials"
private const val MATERIAL_SLOT = "slot"
private const val MATERIAL_ITEM = "item"


data class OniMaterialChoice(
    val slotId: String,
    val itemId: String,
)

data class OniBlueprintSelection(
    val blueprintId: String,
    val materials: List<OniMaterialChoice>,
)

object OniBlueprintSelectionNbt {
    fun readFrom(stack: ItemStack): OniBlueprintSelection? {
        val data = stack.get(DataComponents.CUSTOM_DATA) ?: return null
        val root = data.copyTag()
        val dataTag = root.getCompoundOrEmpty(BLUEPRINT_TAG)
        val blueprintId = dataTag.getString(BLUEPRINT_ID).orElse("")
        if (blueprintId.isBlank()) {
            return null
        }
        val materials = ArrayList<OniMaterialChoice>()
        val list = dataTag.getListOrEmpty(MATERIALS_TAG)
        for (i in 0 until list.size) {
            val entry = list.getCompoundOrEmpty(i)
            val slotId = entry.getString(MATERIAL_SLOT).orElse("")
            val itemId = entry.getString(MATERIAL_ITEM).orElse("")
            if (slotId.isBlank() || itemId.isBlank()) {
                continue
            }
            materials.add(OniMaterialChoice(slotId, itemId))
        }
        return OniBlueprintSelection(blueprintId, materials)
    }

    fun writeTo(stack: ItemStack, selection: OniBlueprintSelection) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack) { root ->
            val data = CompoundTag()
            data.putString(BLUEPRINT_ID, selection.blueprintId)
            val list = ListTag()
            for (choice in selection.materials) {
                val entry = CompoundTag()
                entry.putString(MATERIAL_SLOT, choice.slotId)
                entry.putString(MATERIAL_ITEM, choice.itemId)
                list.add(entry)
            }
            data.put(MATERIALS_TAG, list)
            root.put(BLUEPRINT_TAG, data)
        }
    }

    fun starterSelection(blueprint: OniBlueprint): OniBlueprintSelection {
        val materials = blueprint.materialSlots.map { slot ->
            val itemId = slot.allowedItems.firstOrNull() ?: ""
            OniMaterialChoice(slot.slotId, itemId)
        }
        return OniBlueprintSelection(blueprint.id, materials)
    }
}
