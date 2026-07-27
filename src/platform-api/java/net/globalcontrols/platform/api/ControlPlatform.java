package net.globalcontrols.platform.api;

import java.util.Collection;
import java.util.List;

public interface ControlPlatform {
    Collection<ControlInfo> getControls();

    /** Accepts a full combo list (e.g. [340, 82] for LSHIFT+R).
     *  Platform should sync to vanilla KeyMapping for display compatibility. */
    void setKey(String translationKey, List<Integer> glfwCodes);

    void unsetKey(String translationKey);
}
