package net.globalcontrols.platform.api;

public record ControlInfo(
    String translationKey,
    String displayName,
    String category,
    int keyCode,
    int modifierCode
) {
    public ControlInfo(String translationKey, String displayName, String category, int keyCode) {
        this(translationKey, displayName, category, keyCode, -1);
    }
}
