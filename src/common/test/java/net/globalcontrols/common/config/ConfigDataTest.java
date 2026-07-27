package net.globalcontrols.common.config;

import net.globalcontrols.common.ModVersion;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConfigDataTest {
    @Test
    void defaults() {
        ConfigData d = ConfigData.defaults();
        assertEquals(ModVersion.VERSION, d.version());
        assertEquals("", d.globalControlsFilePath());
        assertFalse(d.firstLaunchCompleted());
    }

    @Test
    void twoArgConstructorDefaultsVersion() {
        ConfigData d = new ConfigData("/path/to/controls.json", true);
        assertEquals(ModVersion.VERSION, d.version());
        assertEquals("/path/to/controls.json", d.globalControlsFilePath());
        assertTrue(d.firstLaunchCompleted());
    }
}
