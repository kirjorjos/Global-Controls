package net.globalcontrols.platform.legacy.handler;

import net.globalcontrols.common.model.KeyNames;
import net.globalcontrols.common.model.MinecraftKeyNames;
import net.globalcontrols.platform.api.ExternalControlHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class NeiHandler implements ExternalControlHandler {
    private final Path configFile;

    public NeiHandler(Path configDir, String mcVersion) {
        // 1.7.10 used NEIIntegration, 1.12.2 used nei
        boolean isLegacy = isLegacyVersion(mcVersion);
        this.configFile = configDir.resolve(isLegacy ? "NEIIntegration/client.cfg" : "nei/client.cfg");
    }

    private static boolean isLegacyVersion(String version) {
        return version != null && (version.startsWith("1.7.") || version.startsWith("1.8."));
    }

    @Override
    public String modId() {
        return "nei";
    }

    @Override
    public List<String> getControlIds() {
        return readControls().keySet().stream().toList();
    }

    @Override
    public Map<String, String> readControls() {
        Map<String, String> result = new LinkedHashMap<>();
        if (!Files.exists(configFile)) return result;
        try {
            boolean inKeys = false;
            for (String line : Files.readAllLines(configFile)) {
                String trimmed = line.trim();
                if (trimmed.startsWith("#") || trimmed.isEmpty()) continue;
                if (trimmed.startsWith("keybindings {")) {
                    inKeys = true;
                    continue;
                }
                if (trimmed.equals("}")) {
                    inKeys = false;
                    continue;
                }
                if (inKeys && trimmed.contains(":")) {
                    int colon = trimmed.indexOf(':');
                    String key = trimmed.substring(0, colon).trim();
                    String value = trimmed.substring(colon + 1).trim();
                    // NEI stores LWJGL2 key codes as integers
                    try {
                        int lwjglCode = Integer.parseInt(value);
                        int glfwCode = lwjglToGlfw(lwjglCode);
                        if (glfwCode >= 0) {
                            result.put(key, KeyNames.format(glfwCode));
                        } else {
                            result.put(key, value);
                        }
                    } catch (NumberFormatException e) {
                        result.put(key, value);
                    }
                }
            }
        } catch (IOException e) {
            // return empty
        }
        return result;
    }

    @Override
    public void writeControl(String controlId, int glfwCode) {
        Map<String, Integer> controls = new LinkedHashMap<>();
        controls.put(controlId, glfwCode);
        writeControls(controls);
    }

    @Override
    public void writeControls(Map<String, Integer> controls) {
        try {
            Files.createDirectories(configFile.getParent());
            List<String> lines = new ArrayList<>();
            if (Files.exists(configFile)) {
                lines = new ArrayList<>(Files.readAllLines(configFile));
            }

            int sectionStart = -1;
            int sectionEnd = -1;
            for (int i = 0; i < lines.size(); i++) {
                String trimmed = lines.get(i).trim();
                if (trimmed.equals("keybindings {")) {
                    sectionStart = i;
                    sectionEnd = i + 1;
                    while (sectionEnd < lines.size() && !lines.get(sectionEnd).trim().equals("}")) {
                        sectionEnd++;
                    }
                    break;
                }
            }

            List<String> newLines = controls.entrySet().stream()
                .map(e -> "    " + e.getKey() + ": " + glfwToLwjgl(e.getValue()))
                .collect(Collectors.toList());

            if (sectionStart >= 0) {
                List<String> before = new ArrayList<>(lines.subList(0, sectionStart));
                List<String> after = new ArrayList<>(lines.subList(sectionEnd + 1, lines.size()));
                lines = new ArrayList<>();
                lines.addAll(before);
                lines.add("keybindings {");
                lines.addAll(newLines);
                lines.add("}");
                lines.addAll(after);
            } else {
                if (!lines.isEmpty() && !lines.get(lines.size() - 1).isEmpty()) {
                    lines.add("");
                }
                lines.add("keybindings {");
                lines.addAll(newLines);
                lines.add("}");
            }

            Files.write(configFile, lines);
        } catch (IOException e) {
            // log and continue
        }
    }

    private static int lwjglToGlfw(int lwjglCode) {
        // LWJGL2 uses the same ASCII codes for letters/numbers as GLFW
        if (lwjglCode >= 0 && lwjglCode <= 255) return lwjglCode;
        return -1;
    }

    private static int glfwToLwjgl(int glfwCode) {
        // For standard keys, LWJGL2 and GLFW overlap in the 0-255 range
        if (glfwCode >= 0 && glfwCode <= 255) return glfwCode;
        return 0;
    }
}
