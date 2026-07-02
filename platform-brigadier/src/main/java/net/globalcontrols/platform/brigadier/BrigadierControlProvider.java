package net.globalcontrols.platform.brigadier;

import net.globalcontrols.platform.api.ControlInfo;
import net.globalcontrols.platform.api.ControlPlatform;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BrigadierControlProvider implements ControlPlatform {
    @Override
    public Collection<ControlInfo> getControls() {
        // TODO: enumerate KeyMapping instances via Options
        return Collections.emptyList();
    }

    @Override
    public void setKey(String translationKey, List<Integer> glfwCodes) {
        // TODO: find KeyMapping by name
        //       if glfwCodes has a first entry that is a modifier (shift/ctrl/alt),
        //       set KeyModifier, otherwise NONE
        //       set keyCode to the last entry in glfwCodes
        //       save options
    }

    @Override
    public void unsetKey(String translationKey) {
        // TODO: reset KeyMapping to default, save options
    }
}
