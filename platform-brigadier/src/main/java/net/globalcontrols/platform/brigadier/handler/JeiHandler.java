package net.globalcontrols.platform.brigadier.handler;

import net.globalcontrols.common.model.MinecraftKeyNames;
import net.globalcontrols.platform.api.ExternalControlHandler;
import net.globalcontrols.platform.brigadier.TomlUtil;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class JeiHandler implements ExternalControlHandler {
    private final Path configFile;

    public JeiHandler(Path configDir) {
        this.configFile = configDir.resolve("jei/jei.toml");
    }

    @Override
    public String modId() {
        return "jei";
    }

    @Override
    public List<String> getControlIds() {
        return readControls().keySet().stream().toList();
    }

    @Override
    public Map<String, String> readControls() {
        Map<String, String> raw = TomlUtil.readSection(configFile, "keys");
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
        String mcName = MinecraftKeyNames.format(glfwCode);
        Map<String, String> keys = new LinkedHashMap<>(readControls());
        keys.put(controlId, mcName);
        Map<String, String> raw = keys.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue(), (a, b) -> b, LinkedHashMap::new));
        TomlUtil.writeSection(configFile, "keys", raw);
    }

    @Override
    public void writeControls(Map<String, Integer> controls) {
        Map<String, String> raw = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : controls.entrySet()) {
            raw.put(entry.getKey(), MinecraftKeyNames.format(entry.getValue()));
        }
        TomlUtil.writeSection(configFile, "keys", raw);
    }
}
