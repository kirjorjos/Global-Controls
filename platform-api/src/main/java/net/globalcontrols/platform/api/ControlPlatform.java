package net.globalcontrols.platform.api;

import java.util.Collection;

public interface ControlPlatform {
    Collection<ControlInfo> getControls();

    /** @param modifierCode GLFW code of modifier key, or -1 for none */
    void setKey(String translationKey, int keyCode, int modifierCode);

    void unsetKey(String translationKey);
}
