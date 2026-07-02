package net.globalcontrols.platform.brigadier.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.globalcontrols.common.model.MinecraftKeyNames;
import net.globalcontrols.platform.api.ExternalControlHandler;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ReiHandler implements ExternalControlHandler {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Map<String, Map<String, String>>>() {}.getType();

    private final Path configFile;

    public ReiHandler(Path configDir) {
        this.configFile = configDir.resolve("roughlyenoughitems/config.json");
    }

    @Override
    public String modId() {
        return "roughlyenoughitems";
    }

    @Override
    public List<String> getControlIds() {
        return readControls().keySet().stream().toList();
    }

    @Override
    public Map<String, String> readControls() {
        Map<String, String> raw = readRawKeybindings();
        Map<String, String> result = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : raw.entrySet()) {
            int code = MinecraftKeyNames.parse(entry.getValue());
            if (code >= 0) {
                result.put(entry.getKey(), net.globalcontrols.common.model.KeyNames.format(code));
            } else {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    @Override
    public void writeControl(String controlId, int glfwCode) {
        Map<String, String> raw = readRawKeybindings();
        raw.put(controlId, MinecraftKeyNames.format(glfwCode));
        writeRawKeybindings(raw);
    }

    @Override
    public void writeControls(Map<String, Integer> controls) {
        Map<String, String> raw = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : controls.entrySet()) {
            raw.put(entry.getKey(), MinecraftKeyNames.format(entry.getValue()));
        }
        writeRawKeybindings(raw);
    }

    private Map<String, String> readRawKeybindings() {
        if (!Files.exists(configFile)) return new LinkedHashMap<>();
        try (Reader reader = Files.newBufferedReader(configFile)) {
            Map<String, Map<String, String>> root = GSON.fromJson(reader, TYPE);
            if (root != null && root.containsKey("keybindings")) {
                return new LinkedHashMap<>(root.get("keybindings"));
            }
        } catch (IOException e) {
            // fall through
        }
        return new LinkedHashMap<>();
    }

    private void writeRawKeybindings(Map<String, String> keybindings) {
        try {
            Files.createDirectories(configFile.getParent());
            Map<String, Map<String, String>> root = new LinkedHashMap<>();
            root.put("keybindings", keybindings);
            try (Writer writer = Files.newBufferedWriter(configFile)) {
                GSON.toJson(root, TYPE, writer);
            }
        } catch (IOException e) {
            // log and continue
        }
    }
}
