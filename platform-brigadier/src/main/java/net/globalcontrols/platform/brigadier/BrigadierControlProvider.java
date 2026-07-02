package net.globalcontrols.platform.brigadier;

import net.globalcontrols.platform.api.ControlInfo;
import net.globalcontrols.platform.api.ControlPlatform;

import java.util.Collection;
import java.util.Collections;

public class BrigadierControlProvider implements ControlPlatform {
    @Override
    public Collection<ControlInfo> getControls() {
        // TODO: enumerate KeyMapping instances via KeyMappingAccessor or Options
        return Collections.emptyList();
    }

    @Override
    public void setKey(String translationKey, int keyCode, int modifierCode) {
        // TODO: find KeyMapping by name, setKeyModifierAndCode(...), save options
    }

    @Override
    public void unsetKey(String translationKey) {
        // TODO: find KeyMapping by name, reset to default, save options
    }
}
