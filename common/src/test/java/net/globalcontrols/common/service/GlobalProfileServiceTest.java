package net.globalcontrols.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class GlobalProfileServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void loadReturnsEmptyWhenFileMissing() {
        GlobalProfileService svc = new GlobalProfileService(tempDir.resolve("missing.json"));
        assertTrue(svc.load().isEmpty());
    }

    @Test
    void saveAndLoadRoundTrip() {
        Path p = tempDir.resolve("profile.json");
        GlobalProfileService svc = new GlobalProfileService(p);
        Map<String, Map<String, String>> data = Map.of(
            "minecraft", Map.of("key.forward", "W", "key.jump", "SPACE"),
            "jei", Map.of("key.jei.show", "LCTRL+O")
        );
        svc.save(data);
        Map<String, Map<String, String>> loaded = svc.load();
        assertEquals(data, loaded);
    }

    @Test
    void setEntryAddsKey() {
        Path p = tempDir.resolve("profile.json");
        GlobalProfileService svc = new GlobalProfileService(p);
        svc.setEntry("minecraft", "key.forward", "W");
        Map<String, Map<String, String>> loaded = svc.load();
        assertEquals("W", loaded.get("minecraft").get("key.forward"));
    }

    @Test
    void setEntryOverwritesExisting() {
        Path p = tempDir.resolve("profile.json");
        GlobalProfileService svc = new GlobalProfileService(p);
        svc.setEntry("minecraft", "key.forward", "W");
        svc.setEntry("minecraft", "key.forward", "SPACE");
        assertEquals("SPACE", svc.load().get("minecraft").get("key.forward"));
    }

    @Test
    void removeEntryDeletesKey() {
        Path p = tempDir.resolve("profile.json");
        GlobalProfileService svc = new GlobalProfileService(p);
        svc.setEntry("minecraft", "key.forward", "W");
        svc.removeEntry("minecraft", "key.forward");
        assertTrue(svc.load().get("minecraft") == null || svc.load().get("minecraft").isEmpty());
    }

    @Test
    void removeEntryDoesNothingForMissingKey() {
        Path p = tempDir.resolve("profile.json");
        GlobalProfileService svc = new GlobalProfileService(p);
        svc.removeEntry("minecraft", "key.nonexistent");
        assertTrue(svc.load().isEmpty());
    }

    @Test
    void loadReturnsEmptyForEmptyFile() {
        Path p = tempDir.resolve("empty.json");
        GlobalProfileService svc = new GlobalProfileService(p);
        svc.save(Map.of());
        assertTrue(svc.load().isEmpty());
    }
}
