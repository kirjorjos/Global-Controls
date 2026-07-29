package net.globalcontrols.common.service;

import net.globalcontrols.common.model.GlobalBinding;
import org.junit.jupiter.api.Test;
import java.util.List;
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
    void extractsModId() {
        BindingRegistry r = new BindingRegistry();
        r.register("key.jei.show", List.of(76));
        assertEquals("jei", r.get("key.jei.show").modId());
    }
}
