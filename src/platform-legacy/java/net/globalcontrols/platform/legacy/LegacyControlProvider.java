package net.globalcontrols.platform.legacy;

import net.globalcontrols.common.model.KeyNames;
import net.globalcontrols.platform.api.ControlInfo;
import net.globalcontrols.platform.api.ControlPlatform;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LegacyControlProvider implements ControlPlatform {
    private static final Logger LOG = Logger.getLogger("GlobalControls");

    /** LWJGL2 key codes for GLFW modifier keys */
    private static final Map<Integer, Integer> MODIFIER_GLFW_TO_LWJGL = new HashMap<>();
    static {
        MODIFIER_GLFW_TO_LWJGL.put(340, 42);  // LSHIFT
        MODIFIER_GLFW_TO_LWJGL.put(344, 54);  // RSHIFT
        MODIFIER_GLFW_TO_LWJGL.put(341, 29);  // LCTRL
        MODIFIER_GLFW_TO_LWJGL.put(345, 157); // RCTRL
        MODIFIER_GLFW_TO_LWJGL.put(342, 56);  // LALT
        MODIFIER_GLFW_TO_LWJGL.put(346, 184); // RALT
    }

    @Override
    public Collection<ControlInfo> getControls() {
        List<ControlInfo> controls = new ArrayList<>();
        try {
            Object mc = getMinecraft();
            if (mc == null) return controls;
            Object gameSettings = getGameSettings(mc);
            if (gameSettings == null) return controls;

            Object[] keyBindings = getKeyBindings(gameSettings);
            if (keyBindings == null) return controls;

            for (Object kb : keyBindings) {
                String desc = getKeyDescription(kb);
                int code = getKeyCode(kb);
                String category = getKeyCategory(kb);
                controls.add(new ControlInfo(desc, desc, category, code >= 0 ? List.of(code) : List.of()));
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to enumerate key bindings", e);
        }
        return controls;
    }

    @Override
    public void setKey(String translationKey, List<Integer> glfwCodes) {
        try {
            Object mc = getMinecraft();
            if (mc == null) return;
            Object gs = getGameSettings(mc);
            if (gs == null) return;

            Object kb = findKeyBinding(gs, translationKey);
            if (kb == null) return;

            int lwjglCode = glfwToLwjgl(KeyNames.extractMainKey(glfwCodes));
            setKeyCode(kb, lwjglCode);
            resetBindingArray();
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to set key for " + translationKey, e);
        }
    }

    @Override
    public void unsetKey(String translationKey) {
        try {
            Object mc = getMinecraft();
            if (mc == null) return;
            Object gs = getGameSettings(mc);
            if (gs == null) return;

            Object kb = findKeyBinding(gs, translationKey);
            if (kb == null) return;

            int defaultCode = getDefaultKeyCode(kb);
            setKeyCode(kb, defaultCode);
            resetBindingArray();
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to unset key for " + translationKey, e);
        }
    }

    public static void fireKey(String translationKey) {
        try {
            Object mc = getMinecraft();
            if (mc == null) return;
            Object gs = getGameSettings(mc);
            if (gs == null) return;

            Object kb = findKeyBinding(gs, translationKey);
            if (kb == null) return;

            clickKeyBinding(kb);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to fire key action for " + translationKey, e);
        }
    }

    private static Object getMinecraft() {
        try {
            return Class.forName("net.minecraft.client.Minecraft")
                .getMethod("getMinecraft").invoke(null);
        } catch (NoSuchMethodException e) {
            try {
                Field f = Class.forName("net.minecraft.client.Minecraft")
                    .getDeclaredField("instance");
                f.setAccessible(true);
                return f.get(null);
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
        return null;
    }

    private static Object getGameSettings(Object mc) {
        try {
            return mc.getClass().getField("gameSettings").get(mc);
        } catch (NoSuchFieldException e) {
            try {
                return mc.getClass().getMethod("getGameSettings").invoke(mc);
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
        return null;
    }

    private static Object[] getKeyBindings(Object gameSettings) {
        try {
            Field f = findField(gameSettings.getClass(), "keyBindings", "keyMappings");
            if (f != null) {
                Object val = f.get(gameSettings);
                if (val instanceof Object[]) return (Object[]) val;
                if (val instanceof Collection) return ((Collection<?>) val).toArray();
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static String getKeyCategory(Object keyBinding) {
        try {
            return (String) keyBinding.getClass().getMethod("getKeyCategory").invoke(keyBinding);
        } catch (NoSuchMethodException e) {
            try {
                java.lang.reflect.Field f = findField(keyBinding.getClass(), "keyCategory", "category");
                if (f != null) return (String) f.get(keyBinding);
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
        return "";
    }

    private static String getKeyDescription(Object keyBinding) {
        try {
            return (String) keyBinding.getClass().getMethod("getKeyDescription").invoke(keyBinding);
        } catch (NoSuchMethodException e) {
            try {
                Field f = findField(keyBinding.getClass(), "keyDescription", "description");
                if (f != null) return (String) f.get(keyBinding);
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
        return null;
    }

    private static int getKeyCode(Object keyBinding) {
        try {
            Field f = findField(keyBinding.getClass(), "keyCode");
            if (f != null) return f.getInt(keyBinding);
        } catch (Exception ignored) {}
        return -1;
    }

    private static void setKeyCode(Object keyBinding, int lwjglCode) {
        try {
            Field f = findField(keyBinding.getClass(), "keyCode");
            if (f != null) f.setInt(keyBinding, lwjglCode);
        } catch (Exception ignored) {}
    }

    private static int getDefaultKeyCode(Object keyBinding) {
        try {
            Field f = findField(keyBinding.getClass(), "defaultKeyCode");
            if (f != null) return f.getInt(keyBinding);
        } catch (Exception ignored) {}
        return -1;
    }

    private static void resetBindingArray() {
        try {
            Class<?> kbClass = Class.forName("net.minecraft.client.settings.KeyBinding");
            kbClass.getMethod("resetKeyBindingArrayAndHash").invoke(null);
        } catch (Exception ignored) {}
    }

    private static void clickKeyBinding(Object keyBinding) {
        try {
            keyBinding.getClass().getMethod("press").invoke(keyBinding);
        } catch (NoSuchMethodException e) {
            try {
                keyBinding.getClass().getMethod("click").invoke(keyBinding);
            } catch (NoSuchMethodException e2) {
                try {
                    Field pt = keyBinding.getClass().getDeclaredField("pressTime");
                    pt.setAccessible(true);
                    pt.setInt(keyBinding, pt.getInt(keyBinding) + 1);
                } catch (Exception ignored) {}
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
    }

    private static Object findKeyBinding(Object gameSettings, String translationKey) {
        Object[] bindings = getKeyBindings(gameSettings);
        if (bindings == null) return null;
        for (Object kb : bindings) {
            String desc = getKeyDescription(kb);
            if (translationKey.equals(desc)) return kb;
        }
        return null;
    }

    private static int glfwToLwjgl(int glfwCode) {
        Integer mod = MODIFIER_GLFW_TO_LWJGL.get(glfwCode);
        if (mod != null) return mod;
        if (glfwCode >= 65 && glfwCode <= 90) return glfwCode - 65 + 30; // A-Z -> LWJGL codes 30-55
        if (glfwCode >= 48 && glfwCode <= 57) return glfwCode - 48 + 2;  // 0-9 -> LWJGL codes 2-11
        if (glfwCode >= 289 && glfwCode <= 313) return glfwCode - 289 + 59; // F1-F24 -> LWJGL codes 59-82
        return glfwCode; // best-effort passthrough
    }

    private static Field findField(Class<?> clazz, String... names) {
        for (String name : names) {
            Class<?> c = clazz;
            while (c != null) {
                try {
                    Field f = c.getDeclaredField(name);
                    f.setAccessible(true);
                    return f;
                } catch (NoSuchFieldException ignored) {}
                c = c.getSuperclass();
            }
        }
        return null;
    }
}
