package net.globalcontrols.common.config;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonConfigManager implements ConfigManager {
    private final Path filePath;

    public JsonConfigManager(Path configDir) throws IOException {
        Path dir = configDir.resolve("GlobalControls");
        Files.createDirectories(dir);
        this.filePath = dir.resolve("config.json");
    }

    @Override
    public ConfigData load() {
        if (!Files.exists(filePath)) {
            return ConfigData.defaults();
        }
        // TODO: parse JSON using Gson or similar
        return ConfigData.defaults();
    }

    @Override
    public void save(ConfigData config) {
        // TODO: serialize to JSON
    }

    @Override
    public Path configFilePath() {
        return filePath;
    }
}
