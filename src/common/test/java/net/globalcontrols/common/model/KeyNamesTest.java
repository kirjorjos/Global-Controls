package net.globalcontrols.common.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class KeyNamesTest {
    @Test
    void parseKnownKeys() {
        assertEquals(65, KeyNames.parse("A"));
        assertEquals(32, KeyNames.parse("SPACE"));
        assertEquals(256, KeyNames.parse("ESC"));
        assertEquals(341, KeyNames.parse("LCTRL"));
    }

    @Test
    void parseReturnsMinusOneForUnknown() {
        assertEquals(-1, KeyNames.parse("BOGUS"));
        assertEquals(-1, KeyNames.parse(""));
        assertEquals(-1, KeyNames.parse(null));
    }

    @Test
    void formatKnownKeys() {
        assertEquals("A", KeyNames.format(65));
        assertEquals("SPACE", KeyNames.format(32));
        assertEquals("LCTRL", KeyNames.format(341));
    }

    @Test
    void formatUnknownReturnsKeyPrefix() {
        assertEquals("key99999", KeyNames.format(99999));
    }

    @Test
    void roundTripAllMappedKeys() {
        for (int code = -2; code <= 347; code++) {
            String name = KeyNames.format(code);
            if (name.startsWith("key")) continue;
            int back = KeyNames.parse(name);
            assertEquals(code, back, "round-trip failed for " + name);
        }
    }

    @Test
    void parseComboEmptyReturnsEmptyList() {
        assertTrue(KeyNames.parseCombo(null).isEmpty());
        assertTrue(KeyNames.parseCombo("").isEmpty());
    }

    @Test
    void parseComboSingle() {
        assertEquals(List.of(65), KeyNames.parseCombo("A"));
    }

    @Test
    void parseComboModifierPlusKey() {
        assertEquals(List.of(341, 65), KeyNames.parseCombo("LCTRL+A"));
    }

    @Test
    void parseComboSkipsUnknown() {
        assertEquals(List.of(65), KeyNames.parseCombo("BOGUS+A"));
    }

    @Test
    void parseComboUnbound() {
        // parseCombo filters codes < 0; unbound is handled at the caller level
        assertTrue(KeyNames.parseCombo("Unbound").isEmpty());
    }

    @Test
    void formatComboEmptyReturnsEmptyString() {
        assertEquals("", KeyNames.formatCombo(List.of()));
    }

    @Test
    void formatComboSingle() {
        assertEquals("A", KeyNames.formatCombo(List.of(65)));
    }

    @Test
    void formatComboModifierPlusKey() {
        assertEquals("LCTRL+A", KeyNames.formatCombo(List.of(341, 65)));
    }

    @Test
    void formatComboUnbound() {
        assertEquals("Unbound", KeyNames.formatCombo(List.of(-2)));
    }

    @Test
    void isModifier() {
        assertTrue(KeyNames.isModifier(340));
        assertTrue(KeyNames.isModifier(341));
        assertTrue(KeyNames.isModifier(342));
        assertTrue(KeyNames.isModifier(344));
        assertTrue(KeyNames.isModifier(345));
        assertTrue(KeyNames.isModifier(346));
        assertFalse(KeyNames.isModifier(65));
        assertFalse(KeyNames.isModifier(0));
    }

    @Test
    void extractMainKey() {
        assertEquals(65, KeyNames.extractMainKey(List.of(341, 65)));
        assertEquals(65, KeyNames.extractMainKey(List.of(65)));
        assertEquals(-1, KeyNames.extractMainKey(List.of()));
    }

    @Test
    void extractModifier() {
        assertEquals(341, KeyNames.extractModifier(List.of(341, 65)));
        assertEquals(-1, KeyNames.extractModifier(List.of(65)));
        assertEquals(-1, KeyNames.extractModifier(List.of()));
    }

    @Test
    void unboundCodeRoundTrips() {
        assertEquals("Unbound", KeyNames.format(-2));
        assertEquals(-2, KeyNames.parse("Unbound"));
    }
}
