package net.globalcontrols.common.autocomplete;

import net.globalcontrols.platform.api.ControlPlatform;
import net.globalcontrols.platform.api.ControlInfo;

import java.util.List;
import java.util.function.Supplier;

public class ControlSuggestions implements Supplier<List<String>> {
    private final ControlPlatform controlPlatform;

    public ControlSuggestions(ControlPlatform controlPlatform) {
        this.controlPlatform = controlPlatform;
    }

    @Override
    public List<String> get() {
        return controlPlatform.getControls().stream()
            .map(ControlInfo::translationKey)
            .toList();
    }
}
