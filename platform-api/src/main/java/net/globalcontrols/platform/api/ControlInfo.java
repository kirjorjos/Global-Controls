package net.globalcontrols.platform.api;

import java.util.List;

public record ControlInfo(
    String translationKey,
    String displayName,
    String category,
    List<Integer> glfwCodes
) {
    public ControlInfo(String translationKey, String displayName, String category, int singleCode) {
        this(translationKey, displayName, category, List.of(singleCode));
    }
}
