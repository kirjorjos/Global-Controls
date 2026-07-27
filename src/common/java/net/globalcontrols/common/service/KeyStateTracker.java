package net.globalcontrols.common.service;

import net.globalcontrols.platform.api.KeyInterceptor;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class KeyStateTracker implements KeyInterceptor {
    private static final Logger LOG = Logger.getLogger("GlobalControls");

    private final BindingRegistry registry;
    private final Set<Integer> heldKeys = new HashSet<>();
    private final Consumer<String> onMatch;
    private String lastMatchedKey;

    public KeyStateTracker(BindingRegistry registry, Consumer<String> onMatch) {
        this.registry = registry;
        this.onMatch = onMatch;
    }

    public void onKeyEvent(int glfwKeyCode, boolean pressed) {
        if (pressed) {
            heldKeys.add(glfwKeyCode);
        } else {
            heldKeys.remove(glfwKeyCode);
        }

        String match = registry.match(heldKeys);
        if (match != null && !match.equals(lastMatchedKey)) {
            lastMatchedKey = match;
            LOG.fine("Combo matched: " + match);
            onMatch.accept(match);
        } else if (match == null) {
            lastMatchedKey = null;
        }
    }

    public Set<Integer> getHeldKeys() {
        return Collections.unmodifiableSet(heldKeys);
    }
}
