package net.globalcontrols.common.config;

public record ConfigData(
    String globalControlsFilePath,
    boolean firstLaunchCompleted
) {
    public static ConfigData defaults() {
        return new ConfigData("", false);
    }
}
