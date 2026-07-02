package net.globalcontrols.platform.legacy;

import net.globalcontrols.platform.api.ControlInfo;
import net.globalcontrols.platform.api.ControlPlatform;

import java.util.Collection;
import java.util.Collections;

public class LegacyControlProvider implements ControlPlatform {
    @Override
    public Collection<ControlInfo> getControls() {
        // TODO: enumerate KeyBinding instances via Forge's ClientRegistry or reflection
        return Collections.emptyList();
    }

    @Override
    public void setKey(String translationKey, int keyCode, int modifierCode) {
        // TODO: find KeyBinding by description, set keyCode, call KeyBinding.resetKeyBindingArrayAndHash()
    }

    @Override
    public void unsetKey(String translationKey) {
        // TODO: reset KeyBinding to default, call KeyBinding.resetKeyBindingArrayAndHash()
    }
}
