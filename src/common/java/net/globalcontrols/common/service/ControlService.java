package net.globalcontrols.common.service;

import net.globalcontrols.common.model.KeyNames;
import net.globalcontrols.platform.api.ControlPlatform;
import net.globalcontrols.platform.api.ControlInfo;

import java.util.Collection;
import java.util.List;

public class ControlService {
    private final ControlPlatform controlPlatform;
    private final BindingRegistry bindingRegistry;

    public ControlService(ControlPlatform controlPlatform, BindingRegistry bindingRegistry) {
        this.controlPlatform = controlPlatform;
        this.bindingRegistry = bindingRegistry;
    }

    public Collection<ControlInfo> getAllControls() {
        return controlPlatform.getControls();
    }

    public void applyCombo(String translationKey, List<Integer> codes) {
        if (codes.isEmpty()) {
            bindingRegistry.unregister(translationKey);
            controlPlatform.unsetKey(translationKey);
        } else {
            bindingRegistry.register(translationKey, codes);
            controlPlatform.setKey(translationKey, codes);
        }
    }

    public void unset(String translationKey) {
        bindingRegistry.unregister(translationKey);
        controlPlatform.unsetKey(translationKey);
    }

    public BindingRegistry getBindingRegistry() {
        return bindingRegistry;
    }
}
