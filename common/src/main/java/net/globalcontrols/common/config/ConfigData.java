package net.globalcontrols.common.config;

public record ConfigData(
    String version,
    String globalControlsFilePath,
    boolean firstLaunchCompleted
) {
    public ConfigData(String globalControlsFilePath, boolean firstLaunchCompleted) {
        this("1.0", globalControlsFilePath, firstLaunchCompleted);
    }

    public static ConfigData defaults() {
        return new ConfigData("1.0", "", false);
    }

}
