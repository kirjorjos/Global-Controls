package net.globalcontrols.platform.brigadier;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public final class TomlUtil {
    private TomlUtil() {}

    public static Map<String, String> readSection(Path file, String section) {
        Map<String, String> result = new LinkedHashMap<>();
        if (!Files.exists(file)) return result;

        try {
            String sectionHeader = "[" + section + "]";
            boolean inSection = false;
            for (String line : Files.readAllLines(file)) {
                String trimmed = line.trim();
                if (trimmed.startsWith("[")) {
                    inSection = trimmed.equalsIgnoreCase(sectionHeader);
                    continue;
                }
                if (inSection && trimmed.contains("=")) {
                    int eq = trimmed.indexOf('=');
                    String key = trimmed.substring(0, eq).trim();
                    String value = trimmed.substring(eq + 1).trim();
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    result.put(key, value);
                }
            }
        } catch (IOException e) {
            // return empty
        }
        return result;
    }

    public static void writeSection(Path file, String section, Map<String, String> entries) {
        try {
            Files.createDirectories(file.getParent());
            List<String> lines = new ArrayList<>();
            if (Files.exists(file)) {
                lines = new ArrayList<>(Files.readAllLines(file));
            }

            String sectionHeader = "[" + section + "]";
            int sectionStart = -1;
            int sectionEnd = -1;
            for (int i = 0; i < lines.size(); i++) {
                String trimmed = lines.get(i).trim();
                if (trimmed.equalsIgnoreCase(sectionHeader)) {
                    sectionStart = i;
                    sectionEnd = i + 1;
                    while (sectionEnd < lines.size() && !lines.get(sectionEnd).trim().startsWith("[")) {
                        sectionEnd++;
                    }
                    break;
                }
            }

            List<String> newLines = entries.entrySet().stream()
                .map(e -> e.getKey() + " = \"" + e.getValue() + "\"")
                .collect(Collectors.toList());

            if (sectionStart >= 0) {
                List<String> before = new ArrayList<>(lines.subList(0, sectionStart));
                List<String> after = new ArrayList<>(lines.subList(sectionEnd, lines.size()));
                lines = new ArrayList<>();
                lines.addAll(before);
                lines.add(sectionHeader);
                lines.addAll(newLines);
                lines.addAll(after);
            } else {
                if (!lines.isEmpty() && !lines.get(lines.size() - 1).isEmpty()) {
                    lines.add("");
                }
                lines.add(sectionHeader);
                lines.addAll(newLines);
            }

            Files.write(file, lines);
        } catch (IOException e) {
            // log and continue
        }
    }
}
