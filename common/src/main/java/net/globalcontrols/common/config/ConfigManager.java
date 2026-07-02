package net.globalcontrols.common.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface ConfigManager {
    ConfigData load();

    void save(ConfigData config);

    Path configFilePath();
}
