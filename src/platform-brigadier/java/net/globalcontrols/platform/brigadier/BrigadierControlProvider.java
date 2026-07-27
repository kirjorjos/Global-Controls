package net.globalcontrols.platform.brigadier;

import net.globalcontrols.common.model.KeyNames;
import net.globalcontrols.platform.api.ControlInfo;
import net.globalcontrols.platform.api.ControlPlatform;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BrigadierControlProvider implements ControlPlatform {
    private static final Logger LOG = Logger.getLogger("GlobalControls");
    private static final int[] MODIFIERS = {340, 341, 342, 343, 344, 345, 346, 347};

    @Override
    public Collection<ControlInfo> getControls() {
        List<ControlInfo> controls = new ArrayList<>();
        try {
            Object mc = getMinecraft();
            if (mc == null) return controls;
            Object options = getOptions(mc);
            if (options == null) return controls;

            Object[] keyMappings = getKeyMappings(options);
            if (keyMappings == null) return controls;

            for (Object km : keyMappings) {
                try {
                    String name = getName(km);
                    if (name == null) continue;
                    int code = getKeyCode(km);
                    String category = getCategory(km);
                    controls.add(new ControlInfo(name, name, category, code >= 0 ? List.of(code) : List.of()));
                } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to enumerate key mappings", e);
        }
        return controls;
    }

    @Override
    public void setKey(String translationKey, List<Integer> glfwCodes) {
        try {
            Object mc = getMinecraft();
            if (mc == null) return;
            Object options = getOptions(mc);
            if (options == null) return;

            Object km = findKeyMapping(options, translationKey);
            if (km == null) return;

            int mainKey = KeyNames.extractMainKey(glfwCodes);
            int modifier = KeyNames.extractModifier(glfwCodes);

            setKeyCode(km, mainKey);
            setKeyModifier(km, modifier);
            saveOptions(options);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to set key for " + translationKey, e);
        }
    }

    @Override
    public void unsetKey(String translationKey) {
        try {
            Object mc = getMinecraft();
            if (mc == null) return;
            Object options = getOptions(mc);
            if (options == null) return;

            Object km = findKeyMapping(options, translationKey);
            if (km == null) return;

            setKeyCode(km, getDefaultKeyCode(km));
            setKeyModifier(km, -1);
            saveOptions(options);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to unset key for " + translationKey, e);
        }
    }

    public static void fireKey(String translationKey) {
        try {
            Object mc = getMinecraft();
            if (mc == null) return;
            Object options = getOptions(mc);
            if (options == null) return;

            Object km = findKeyMapping(options, translationKey);
            if (km == null) return;

            clickKeyMapping(km);
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to fire key action for " + translationKey, e);
        }
    }

    private static Object getMinecraft() {
        try {
            return Class.forName("net.minecraft.client.Minecraft")
                .getMethod("getInstance").invoke(null);
        } catch (Exception e) {
            try {
                Field f = Class.forName("net.minecraft.client.Minecraft")
                    .getDeclaredField("instance");
                f.setAccessible(true);
                return f.get(null);
            } catch (Exception ignored) {}
        }
        return null;
    }

    private static Object getOptions(Object mc) {
        try {
            return mc.getClass().getMethod("getOptions").invoke(mc);
        } catch (NoSuchMethodException e) {
            try {
                Field f = findField(mc.getClass(), "options", "gameSettings");
                if (f != null) return f.get(mc);
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
        return null;
    }

    private static Object[] getKeyMappings(Object options) {
        try {
            Method m = options.getClass().getMethod("getKeyMappings");
            Object result = m.invoke(options);
            if (result instanceof Object[]) return (Object[]) result;
            if (result instanceof Collection) return ((Collection<?>) result).toArray();
        } catch (NoSuchMethodException e) {
            try {
                Field f = findField(options.getClass(), "keyMappings", "keyBindings");
                if (f != null) {
                    Object result = f.get(options);
                    if (result instanceof Object[]) return (Object[]) result;
                    if (result instanceof Collection) return ((Collection<?>) result).toArray();
                }
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
        return null;
    }

    private static String getCategory(Object keyMapping) {
        try {
            return (String) keyMapping.getClass().getMethod("getCategory").invoke(keyMapping);
        } catch (NoSuchMethodException e) {
            try {
                return (String) keyMapping.getClass().getMethod("getKeyCategory").invoke(keyMapping);
            } catch (NoSuchMethodException e2) {
                try {
                    java.lang.reflect.Field f = findField(keyMapping.getClass(), "category", "keyCategory");
                    if (f != null) return (String) f.get(keyMapping);
                } catch (Exception ignored) {}
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
        return "";
    }

    private static String getName(Object keyMapping) {
        try {
            return (String) keyMapping.getClass().getMethod("getName").invoke(keyMapping);
        } catch (NoSuchMethodException e) {
            try {
                return (String) keyMapping.getClass().getMethod("getTranslationKey").invoke(keyMapping);
            } catch (NoSuchMethodException e2) {
                try {
                    Field f = findField(keyMapping.getClass(), "name", "description", "keyDescription");
                    if (f != null) return (String) f.get(keyMapping);
                } catch (Exception ignored) {}
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
        return null;
    }

    private static int getKeyCode(Object keyMapping) {
        try {
            Method getKey = keyMapping.getClass().getMethod("getKey");
            Object key = getKey.invoke(keyMapping);
            try {
                return (int) key.getClass().getMethod("getValue").invoke(key);
            } catch (NoSuchMethodException e) {
                try {
                    Field vf = findField(key.getClass(), "value", "keyCode");
                    if (vf != null) return vf.getInt(key);
                } catch (Exception ignored) {}
            }
        } catch (NoSuchMethodException e) {
            try {
                Field kf = findField(keyMapping.getClass(), "key", "keyCode");
                if (kf != null) {
                    Object key = kf.get(keyMapping);
                    if (key instanceof Integer) return (Integer) key;
                    try {
                        return (int) key.getClass().getMethod("getValue").invoke(key);
                    } catch (Exception ignored) {}
                }
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
        return -1;
    }

    private static void setKeyCode(Object keyMapping, int glfwCode) {
        try {
            Method setKey = keyMapping.getClass().getMethod("setKey", Class.forName("net.minecraft.client.InputConstants$Key"));
            Object key = createInputConstantsKey(glfwCode);
            if (key != null) setKey.invoke(keyMapping, key);
            return;
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            // try field-based approach
        } catch (Exception ignored) {}
        try {
            Field kf = findField(keyMapping.getClass(), "key", "keyCode");
            if (kf != null) {
                kf.setInt(keyMapping, glfwCode);
            }
        } catch (Exception ignored) {}
    }

    private static Object createInputConstantsKey(int glfwCode) {
        try {
            Class<?> keyClass = Class.forName("net.minecraft.client.InputConstants$Key");
            Class<?> typeClass = Class.forName("net.minecraft.client.InputConstants$Type");
            Object keysym = typeClass.getDeclaredField("KEYSYM").get(null);
            for (var ctor : keyClass.getDeclaredConstructors()) {
                if (ctor.getParameterCount() == 2 && ctor.getParameterTypes()[0] == typeClass) {
                    ctor.setAccessible(true);
                    return ctor.newInstance(keysym, glfwCode);
                }
            }
        } catch (Exception ignored) {}
        try {
            Method getKey = Class.forName("net.minecraft.client.InputConstants")
                .getMethod("getKey", int.class, int.class);
            return getKey.invoke(null, glfwCode, 0);
        } catch (Exception ignored) {}
        return null;
    }

    private static void setKeyModifier(Object keyMapping, int modifierGlfw) {
        try {
            Class<?> modifierClass = Class.forName("net.minecraft.client.KeyMapping$KeyModifier");
            Object modifier = null;
            if (modifierGlfw == 340 || modifierGlfw == 344) {
                modifier = modifierClass.getDeclaredField("SHIFT").get(null);
            } else if (modifierGlfw == 341 || modifierGlfw == 345) {
                modifier = modifierClass.getDeclaredField("CONTROL").get(null);
            } else if (modifierGlfw == 342 || modifierGlfw == 346) {
                modifier = modifierClass.getDeclaredField("ALT").get(null);
            } else {
                modifier = modifierClass.getDeclaredField("NONE").get(null);
            }
            Method setMod = keyMapping.getClass().getMethod("setKeyModifier", modifierClass);
            setMod.invoke(keyMapping, modifier);
        } catch (Exception ignored) {}
    }

    private static int getDefaultKeyCode(Object keyMapping) {
        try {
            Field df = findField(keyMapping.getClass(), "defaultKey", "defaultKeyCode");
            if (df != null) {
                Object dk = df.get(keyMapping);
                if (dk instanceof Integer) return (Integer) dk;
                try {
                    return (int) dk.getClass().getMethod("getValue").invoke(dk);
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}
        return -1;
    }

    private static void saveOptions(Object options) {
        try {
            options.getClass().getMethod("save").invoke(options);
        } catch (NoSuchMethodException e) {
            try {
                Object mc = getMinecraft();
                if (mc != null) {
                    mc.getClass().getMethod("saveOptions").invoke(mc);
                }
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
    }

    private static void clickKeyMapping(Object keyMapping) {
        try {
            keyMapping.getClass().getMethod("click").invoke(keyMapping);
        } catch (NoSuchMethodException e) {
            try {
                keyMapping.getClass().getMethod("press").invoke(keyMapping);
            } catch (NoSuchMethodException e2) {
                try {
                    Field cf = keyMapping.getClass().getDeclaredField("clickCount");
                    cf.setAccessible(true);
                    cf.setInt(keyMapping, cf.getInt(keyMapping) + 1);
                    Field df = keyMapping.getClass().getDeclaredField("isDown");
                    df.setAccessible(true);
                    df.setBoolean(keyMapping, true);
                } catch (Exception ignored) {}
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
    }

    private static Object findKeyMapping(Object options, String translationKey) {
        Object[] mappings = getKeyMappings(options);
        if (mappings == null) return null;
        for (Object km : mappings) {
            String name = getName(km);
            if (translationKey.equals(name)) return km;
        }
        return null;
    }

    private static Field findField(Class<?> clazz, String... names) {
        for (String name : names) {
            try {
                Field f = clazz.getDeclaredField(name);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException e) {
                Class<?> s = clazz.getSuperclass();
                while (s != null) {
                    try {
                        Field f = s.getDeclaredField(name);
                        f.setAccessible(true);
                        return f;
                    } catch (NoSuchFieldException ignored) {}
                    s = s.getSuperclass();
                }
            }
        }
        return null;
    }
}
