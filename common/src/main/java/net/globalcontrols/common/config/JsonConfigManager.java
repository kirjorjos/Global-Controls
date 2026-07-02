package net.globalcontrols.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonConfigManager implements ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

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
        try (Reader reader = Files.newBufferedReader(filePath)) {
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            return data != null ? data : ConfigData.defaults();
        } catch (IOException e) {
            return ConfigData.defaults();
        }
    }

    @Override
    public void save(ConfigData config) {
        try (Writer writer = Files.newBufferedWriter(filePath)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            // log and continue
        }
    }

    @Override
    public Path configFilePath() {
        return filePath;
    }
}
