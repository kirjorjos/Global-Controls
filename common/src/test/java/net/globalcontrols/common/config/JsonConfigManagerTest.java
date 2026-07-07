package net.globalcontrols.common.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class JsonConfigManagerTest {
    @TempDir
    Path tempDir;

    @Test
    void loadReturnsDefaultsWhenFileMissing() throws IOException {
        Path freshDir = tempDir.resolve("fresh");
        JsonConfigManager mgr = new JsonConfigManager(freshDir);
        ConfigData d = mgr.load();
        assertEquals("1.0", d.version());
        assertEquals("", d.globalControlsFilePath());
        assertFalse(d.firstLaunchCompleted());
    }

    @Test
    void saveAndLoadRoundTrip() throws IOException {
        JsonConfigManager mgr = new JsonConfigManager(tempDir);
        ConfigData original = new ConfigData("/path/gc.json", true);
        mgr.save(original);
        ConfigData loaded = mgr.load();
        assertEquals(original.version(), loaded.version());
        assertEquals(original.globalControlsFilePath(), loaded.globalControlsFilePath());
        assertEquals(original.firstLaunchCompleted(), loaded.firstLaunchCompleted());
    }

    @Test
    void configFilePathIsCorrect() throws IOException {
        JsonConfigManager mgr = new JsonConfigManager(tempDir);
        assertEquals(tempDir.resolve("GlobalControls/config.json"), mgr.configFilePath());
    }

    @Test
    void loadReturnsDefaultsWhenFileCorrupted() throws IOException {
        JsonConfigManager mgr = new JsonConfigManager(tempDir);
        // Write invalid JSON
        java.nio.file.Files.writeString(mgr.configFilePath(), "not json");
        ConfigData d = mgr.load();
        assertEquals("1.0", d.version());
    }
}
