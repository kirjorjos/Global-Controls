package net.globalcontrols.common.autocomplete;

import net.globalcontrols.platform.api.ModPlatform;

import java.util.List;
import java.util.function.Supplier;

public class InstalledModSuggestions implements Supplier<List<String>> {
    private final ModPlatform modPlatform;

    public InstalledModSuggestions(ModPlatform modPlatform) {
        this.modPlatform = modPlatform;
    }

    @Override
    public List<String> get() {
        return modPlatform.getInstalledMods().stream()
            .map(m -> m.id())
            .toList();
    }
}
