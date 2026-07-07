package net.globalcontrols.common.service;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class KeyStateTrackerTest {
    @Test
    void matchFiresCallback() {
        BindingRegistry reg = new BindingRegistry();
        reg.register("key.jump", List.of(32));
        List<String> fired = new ArrayList<>();
        KeyStateTracker t = new KeyStateTracker(reg, fired::add);
        t.onKeyEvent(32, true);
        assertEquals(List.of("key.jump"), fired);
    }

    @Test
    void noMatchDoesNotFire() {
        BindingRegistry reg = new BindingRegistry();
        reg.register("key.jump", List.of(32));
        List<String> fired = new ArrayList<>();
        KeyStateTracker t = new KeyStateTracker(reg, fired::add);
        t.onKeyEvent(87, true);
        assertTrue(fired.isEmpty());
    }

    @Test
    void releaseClearsMatch() {
        BindingRegistry reg = new BindingRegistry();
        reg.register("key.jump", List.of(32));
        List<String> fired = new ArrayList<>();
        KeyStateTracker t = new KeyStateTracker(reg, fired::add);
        t.onKeyEvent(32, true);
        fired.clear();
        t.onKeyEvent(32, false);
        t.onKeyEvent(32, true);
        assertEquals(List.of("key.jump"), fired);
    }

    @Test
    void sameMatchNotFiredTwice() {
        BindingRegistry reg = new BindingRegistry();
        reg.register("key.jump", List.of(32));
        List<String> fired = new ArrayList<>();
        KeyStateTracker t = new KeyStateTracker(reg, fired::add);
        t.onKeyEvent(32, true);
        assertEquals(List.of("key.jump"), fired);
        fired.clear();
        // pressing same key again does not re-fire
        t.onKeyEvent(32, false);
        t.onKeyEvent(32, true);
        assertEquals(List.of("key.jump"), fired);
    }

    @Test
    void differentMatchAfterRelease() {
        BindingRegistry reg = new BindingRegistry();
        reg.register("key.jump", List.of(32));
        reg.register("key.sneak", List.of(29));
        List<String> fired = new ArrayList<>();
        KeyStateTracker t = new KeyStateTracker(reg, fired::add);
        t.onKeyEvent(32, true);
        t.onKeyEvent(32, false);
        t.onKeyEvent(29, true);
        assertEquals(List.of("key.jump", "key.sneak"), fired);
    }

    @Test
    void comboWithModifier() {
        BindingRegistry reg = new BindingRegistry();
        reg.register("key.sprint", List.of(341, 87));
        List<String> fired = new ArrayList<>();
        KeyStateTracker t = new KeyStateTracker(reg, fired::add);
        t.onKeyEvent(341, true);
        assertTrue(fired.isEmpty());
        t.onKeyEvent(87, true);
        assertEquals(List.of("key.sprint"), fired);
    }

    @Test
    void releaseOneKeyOfComboClearsMatch() {
        BindingRegistry reg = new BindingRegistry();
        reg.register("key.sprint", List.of(341, 87));
        List<String> fired = new ArrayList<>();
        KeyStateTracker t = new KeyStateTracker(reg, fired::add);
        t.onKeyEvent(341, true);
        t.onKeyEvent(87, true);
        fired.clear();
        t.onKeyEvent(87, false);
        t.onKeyEvent(87, true);
        assertEquals(List.of("key.sprint"), fired);
    }

    @Test
    void getHeldKeys() {
        BindingRegistry reg = new BindingRegistry();
        KeyStateTracker t = new KeyStateTracker(reg, __ -> {});
        assertTrue(t.getHeldKeys().isEmpty());
        t.onKeyEvent(87, true);
        assertEquals(1, t.getHeldKeys().size());
        assertTrue(t.getHeldKeys().contains(87));
        t.onKeyEvent(87, false);
        assertTrue(t.getHeldKeys().isEmpty());
    }
}
