package net.globalcontrols.common.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import static org.junit.jupiter.api.Assertions.*;

class OsDefaultsTest {
    @Test
    @DisabledOnOs(OS.WINDOWS)
    void unixPath() {
        String path = OsDefaults.defaultGlobalControlsFilePath("1.20.1");
        String home = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("mac")) {
            assertEquals(home + "/Library/Application Support/GlobalControls/1.20.1-controls.json", path);
        } else {
            assertEquals(home + "/.local/share/GlobalControls/1.20.1-controls.json", path);
        }
    }
}
