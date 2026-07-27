package net.globalcontrols.common.service;

import net.globalcontrols.common.model.GlobalBinding;

import java.util.*;

public class BindingRegistry {
    private final Map<String, GlobalBinding> byKey = new LinkedHashMap<>();

    public void register(String translationKey, List<Integer> heldKeys) {
        String modId = extractModId(translationKey);
        byKey.put(translationKey, new GlobalBinding(translationKey, modId, List.copyOf(heldKeys)));
    }

    public void unregister(String translationKey) {
        byKey.remove(translationKey);
    }

    public GlobalBinding get(String translationKey) {
        return byKey.get(translationKey);
    }

    public List<Integer> getCombo(String translationKey) {
        GlobalBinding b = byKey.get(translationKey);
        return b != null ? b.heldKeys() : null;
    }

    public Collection<GlobalBinding> getAll() {
        return Collections.unmodifiableCollection(byKey.values());
    }

    private static String extractModId(String translationKey) {
        // e.g. "key.jei.showRecipes" → "jei"
        String[] parts = translationKey.split("\\.");
        if (parts.length >= 2) return parts[1];
        return "minecraft";
    }
}
