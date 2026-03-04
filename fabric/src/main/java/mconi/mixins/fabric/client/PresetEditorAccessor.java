package mconi.mixins.fabric.client;

import java.util.Map;
import java.util.Optional;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PresetEditor.class)
public interface PresetEditorAccessor {
    @Accessor("EDITORS")
    static Map<Optional<ResourceKey<WorldPreset>>, PresetEditor> mconi$getEditors() {
        throw new AssertionError("Mixin accessor was not applied");
    }
}
