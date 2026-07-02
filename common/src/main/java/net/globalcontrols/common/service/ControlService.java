package net.globalcontrols.common.service;

import net.globalcontrols.common.model.KeyNames;
import net.globalcontrols.platform.api.ControlPlatform;
import net.globalcontrols.platform.api.ControlInfo;

import java.util.Collection;
import java.util.List;

public class ControlService {
    private final ControlPlatform controlPlatform;

    public ControlService(ControlPlatform controlPlatform) {
        this.controlPlatform = controlPlatform;
    }

    public Collection<ControlInfo> getAllControls() {
        return controlPlatform.getControls();
    }

    public void applyCombo(String translationKey, List<Integer> codes) {
        if (codes.isEmpty()) {
            controlPlatform.unsetKey(translationKey);
        } else {
            int mainKey = KeyNames.extractMainKey(codes);
            int modifier = KeyNames.extractModifier(codes);
            controlPlatform.setKey(translationKey, mainKey, modifier);
        }
    }

    public void unset(String translationKey) {
        controlPlatform.unsetKey(translationKey);
    }
}
