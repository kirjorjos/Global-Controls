package net.globalcontrols.common.service;

import net.globalcontrols.common.model.GlobalBinding;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class BindingRegistryTest {
    @Test
    void registerAndGet() {
        BindingRegistry r = new BindingRegistry();
        r.register("key.forward", List.of(87));
        GlobalBinding b = r.get("key.forward");
        assertNotNull(b);
        assertEquals("key.forward", b.translationKey());
        assertEquals(List.of(87), b.heldKeys());
    }

    @Test
    void unregisterRemoves() {
        BindingRegistry r = new BindingRegistry();
        r.register("key.forward", List.of(87));
        r.unregister("key.forward");
        assertNull(r.get("key.forward"));
        assertNull(r.getCombo("key.forward"));
    }

    @Test
    void getComboReturnsNullForUnknown() {
        BindingRegistry r = new BindingRegistry();
        assertNull(r.getCombo("key.nonexistent"));
    }

    @Test
    void matchExact() {
        BindingRegistry r = new BindingRegistry();
        r.register("key.jump", List.of(32));
        assertEquals("key.jump", r.match(Set.of(32)));
    }

    @Test
    void matchWithModifier() {
        BindingRegistry r = new BindingRegistry();
        r.register("key.sprint", List.of(341, 87));
        assertEquals("key.sprint", r.match(Set.of(341, 87)));
    }

    @Test
    void strictMatchRejectsExtraKeys() {
        BindingRegistry r = new BindingRegistry();
        r.register("key.jump", List.of(32));
        assertNull(r.match(Set.of(32, 87)));
    }

    @Test
    void strictMatchRejectsMissingKeys() {
        BindingRegistry r = new BindingRegistry();
        r.register("key.sprint", List.of(341, 87));
        assertNull(r.match(Set.of(87)));
    }

    @Test
    void matchReturnsNullForEmptyRegistry() {
        BindingRegistry r = new BindingRegistry();
        assertNull(r.match(Set.of(32)));
    }

    @Test
    void getAllReturnsRegistered() {
        BindingRegistry r = new BindingRegistry();
        r.register("key.forward", List.of(87));
        r.register("key.jump", List.of(32));
        assertEquals(2, r.getAll().size());
    }

    @Test
    void getAllIsUnmodifiable() {
        BindingRegistry r = new BindingRegistry();
        r.register("key.forward", List.of(87));
        assertThrows(Exception.class, () -> r.getAll().add(new GlobalBinding("x", "x", List.of())));
    }

    @Test
    void registerOverwrites() {
        BindingRegistry r = new BindingRegistry();
        r.register("key.forward", List.of(87));
        r.register("key.forward", List.of(32));
        assertEquals(List.of(32), r.getCombo("key.forward"));
    }

    @Test
    void matchReturnsNullWhenNothingHeld() {
        BindingRegistry r = new BindingRegistry();
        r.register("key.jump", List.of(32));
        assertNull(r.match(Set.of()));
    }

    @Test
    void extractsModId() {
        BindingRegistry r = new BindingRegistry();
        r.register("key.jei.show", List.of(76));
        assertEquals("jei", r.get("key.jei.show").modId());
    }

    @Test
    void matchCorrectAmongMultiple() {
        BindingRegistry r = new BindingRegistry();
        r.register("key.forward", List.of(87));
        r.register("key.left", List.of(65));
        r.register("key.sprint", List.of(341, 87));
        assertEquals("key.left", r.match(Set.of(65)));
        assertEquals("key.forward", r.match(Set.of(87)));
        assertEquals("key.sprint", r.match(Set.of(341, 87)));
    }
}
