package conservecraft.fabric

import conservecraft.common.world.OniWorldgenConfig
import conservecraft.common.world.OniWorldgenConfigData
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import me.shedaniel.clothconfig2.api.ConfigBuilder

object FabricWorldgenConfigScreen {
    fun create(parent: Screen?): Screen {
        val data = OniWorldgenConfig.load()
        val builder = ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.literal("ConserveCraft Worldgen"))
        val entryBuilder = builder.entryBuilder()
        val category = builder.getOrCreateCategory(Component.literal("Worldgen"))
        category.addEntry(
            entryBuilder.startIntField(Component.literal("Z Thickness"), data.zThickness)
                .setDefaultValue(OniWorldgenConfigData.DEFAULT_Z_THICKNESS)
                .setMin(1)
                .setMax(64)
                .setSaveConsumer { value -> data.zThickness = value }
                .build()
        )
        builder.setSavingRunnable { OniWorldgenConfig.save(data) }
        return builder.build()
    }
}
