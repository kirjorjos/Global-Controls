package net.globalcontrols.common.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GlobalProfileService {
    private static final Logger LOG = Logger.getLogger("GlobalControls");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Map<String, Map<String, String>>>() {}.getType();

    private final Path profilePath;

    public GlobalProfileService(Path profilePath) {
        this.profilePath = profilePath;
    }

    public Map<String, Map<String, String>> load() {
        if (!Files.exists(profilePath)) {
            return new HashMap<>();
        }
        try (Reader reader = Files.newBufferedReader(profilePath)) {
            Map<String, Map<String, String>> data = GSON.fromJson(reader, TYPE);
            return data != null ? data : new HashMap<>();
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Failed to read global controls profile", e);
            return new HashMap<>();
        }
    }

    public void save(Map<String, Map<String, String>> profile) {
        try {
            Files.createDirectories(profilePath.getParent());
            try (Writer writer = Files.newBufferedWriter(profilePath)) {
                GSON.toJson(profile, TYPE, writer);
            }
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Failed to save global controls profile", e);
        }
    }

    public void setEntry(String modId, String controlId, String keyName) {
        Map<String, Map<String, String>> profile = load();
        profile.computeIfAbsent(modId, k -> new HashMap<>()).put(controlId, keyName);
        save(profile);
    }

    public void removeEntry(String modId, String controlId) {
        Map<String, Map<String, String>> profile = load();
        Map<String, String> modEntries = profile.get(modId);
        if (modEntries != null) {
            modEntries.remove(controlId);
            if (modEntries.isEmpty()) {
                profile.remove(modId);
            }
        }
        save(profile);
    }
}
