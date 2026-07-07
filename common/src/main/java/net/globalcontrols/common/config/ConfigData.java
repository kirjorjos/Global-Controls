package net.globalcontrols.common.config;

import net.globalcontrols.common.ModVersion;

public record ConfigData(
    String version,
    String globalControlsFilePath,
    boolean firstLaunchCompleted
) {
    public ConfigData(String globalControlsFilePath, boolean firstLaunchCompleted) {
        this(ModVersion.VERSION, globalControlsFilePath, firstLaunchCompleted);
    }

    public static ConfigData defaults() {
        return new ConfigData("", false);
    }

}
