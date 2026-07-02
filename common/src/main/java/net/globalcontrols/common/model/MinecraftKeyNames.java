package net.globalcontrols.common.model;

import java.util.HashMap;
import java.util.Map;

public final class MinecraftKeyNames {
    private static final Map<Integer, String> CODE_TO_MC = new HashMap<>();
    private static final Map<String, Integer> MC_TO_CODE = new HashMap<>();

    static {
        map(0, "key.mouse.left", "MOUSE1");
        map(1, "key.mouse.right", "MOUSE2");
        map(2, "key.mouse.middle", "MOUSE3");
        map(3, "key.mouse.4", "MOUSE4");
        map(4, "key.mouse.5", "MOUSE5");

        for (int i = 'A'; i <= 'Z'; i++) {
            map(i, "key.keyboard." + Character.toLowerCase((char) i), String.valueOf((char) i));
        }

        for (int i = '0'; i <= '9'; i++) {
            map(i, "key.keyboard." + (char) i, String.valueOf((char) i));
        }

        map(32, "key.keyboard.space", "SPACE");
        map(39, "key.keyboard.apostrophe", "APOSTROPHE");
        map(44, "key.keyboard.comma", "COMMA");
        map(45, "key.keyboard.minus", "MINUS");
        map(46, "key.keyboard.period", "PERIOD");
        map(47, "key.keyboard.slash", "SLASH");
        map(59, "key.keyboard.semicolon", "SEMICOLON");
        map(61, "key.keyboard.equal", "EQUALS");
        map(91, "key.keyboard.left.bracket", "LBRACKET");
        map(92, "key.keyboard.backslash", "BACKSLASH");
        map(93, "key.keyboard.right.bracket", "RBRACKET");
        map(96, "key.keyboard.grave.accent", "GRAVE");

        map(256, "key.keyboard.escape", "ESC");
        map(257, "key.keyboard.enter", "ENTER");
        map(258, "key.keyboard.tab", "TAB");
        map(259, "key.keyboard.backspace", "BACKSPACE");
        map(260, "key.keyboard.insert", "INSERT");
        map(261, "key.keyboard.delete", "DELETE");
        map(262, "key.keyboard.right", "RIGHT");
        map(263, "key.keyboard.left", "LEFT");
        map(264, "key.keyboard.down", "DOWN");
        map(265, "key.keyboard.up", "UP");
        map(266, "key.keyboard.page.up", "PGUP");
        map(267, "key.keyboard.page.down", "PGDN");
        map(268, "key.keyboard.home", "HOME");
        map(269, "key.keyboard.end", "END");
        map(280, "key.keyboard.caps.lock", "CAPS");
        map(281, "key.keyboard.scroll.lock", "SCROLL");
        map(282, "key.keyboard.num.lock", "NUMLOCK");
        map(283, "key.keyboard.print.screen", "PRINT");
        map(284, "key.keyboard.pause", "PAUSE");

        map(340, "key.keyboard.left.shift", "LSHIFT");
        map(341, "key.keyboard.left.control", "LCTRL");
        map(342, "key.keyboard.left.alt", "LALT");
        map(343, "key.keyboard.left.super", "LWIN");
        map(344, "key.keyboard.right.shift", "RSHIFT");
        map(345, "key.keyboard.right.control", "RCTRL");
        map(346, "key.keyboard.right.alt", "RALT");
        map(347, "key.keyboard.right.super", "RWIN");

        for (int i = 1; i <= 24; i++) {
            map(289 + i, "f" + i, "F" + i);
        }

        map(320, "key.keyboard.keypad.0", "NP0");
        map(321, "key.keyboard.keypad.1", "NP1");
        map(322, "key.keyboard.keypad.2", "NP2");
        map(323, "key.keyboard.keypad.3", "NP3");
        map(324, "key.keyboard.keypad.4", "NP4");
        map(325, "key.keyboard.keypad.5", "NP5");
        map(326, "key.keyboard.keypad.6", "NP6");
        map(327, "key.keyboard.keypad.7", "NP7");
        map(328, "key.keyboard.keypad.8", "NP8");
        map(329, "key.keyboard.keypad.9", "NP9");
        map(330, "key.keyboard.keypad.decimal", "NPDOT");
        map(331, "key.keyboard.keypad.divide", "NPDIVIDE");
        map(332, "key.keyboard.keypad.multiply", "NPMULTIPLY");
        map(333, "key.keyboard.keypad.subtract", "NPMINUS");
        map(334, "key.keyboard.keypad.add", "NPPLUS");
        map(335, "key.keyboard.keypad.enter", "NPENTER");
        map(336, "key.keyboard.keypad.equal", "NPEQUALS");
    }

    private static void map(int code, String mcName, String shortName) {
        CODE_TO_MC.put(code, mcName);
        MC_TO_CODE.put(mcName, code);
    }

    public static int parse(String mcKeyName) {
        if (mcKeyName == null || mcKeyName.isEmpty()) return -1;
        Integer code = MC_TO_CODE.get(mcKeyName);
        return code != null ? code : -1;
    }

    public static String format(int glfwCode) {
        String name = CODE_TO_MC.get(glfwCode);
        return name != null ? name : ("key.keyboard.unknown");
    }

    private MinecraftKeyNames() {}
}
