package net.globalcontrols.common.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class KeyNames {
    private static final Map<Integer, String> CODE_TO_NAME = new HashMap<>();
    private static final Map<String, Integer> NAME_TO_CODE = new HashMap<>();

    static {
        map(0, "MOUSE1");
        map(1, "MOUSE2");
        map(2, "MOUSE3");
        map(3, "MOUSE4");
        map(4, "MOUSE5");

        for (int i = 'A'; i <= 'Z'; i++) {
            map(i, String.valueOf((char) i));
        }

        for (int i = '0'; i <= '9'; i++) {
            map(i, String.valueOf((char) i));
        }

        map(32, "SPACE");
        map(39, "APOSTROPHE");
        map(44, "COMMA");
        map(45, "MINUS");
        map(46, "PERIOD");
        map(47, "SLASH");
        map(59, "SEMICOLON");
        map(61, "EQUALS");
        map(91, "LBRACKET");
        map(92, "BACKSLASH");
        map(93, "RBRACKET");
        map(96, "GRAVE");

        map(256, "ESC");
        map(257, "ENTER");
        map(258, "TAB");
        map(259, "BACKSPACE");
        map(260, "INSERT");
        map(261, "DELETE");
        map(262, "RIGHT");
        map(263, "LEFT");
        map(264, "DOWN");
        map(265, "UP");
        map(266, "PGUP");
        map(267, "PGDN");
        map(268, "HOME");
        map(269, "END");
        map(280, "CAPS");
        map(281, "SCROLL");
        map(282, "NUMLOCK");
        map(283, "PRINT");
        map(284, "PAUSE");

        map(340, "LSHIFT");
        map(341, "LCTRL");
        map(342, "LALT");
        map(343, "LWIN");
        map(344, "RSHIFT");
        map(345, "RCTRL");
        map(346, "RALT");
        map(347, "RWIN");

        for (int i = 1; i <= 24; i++) {
            map(289 + i, "F" + i);
        }

        map(320, "NP0");
        map(321, "NP1");
        map(322, "NP2");
        map(323, "NP3");
        map(324, "NP4");
        map(325, "NP5");
        map(326, "NP6");
        map(327, "NP7");
        map(328, "NP8");
        map(329, "NP9");
        map(330, "NPDOT");
        map(331, "NPDIVIDE");
        map(332, "NPMULTIPLY");
        map(333, "NPMINUS");
        map(334, "NPPLUS");
        map(335, "NPENTER");
        map(336, "NPEQUALS");
    }

    private static void map(int code, String name) {
        CODE_TO_NAME.put(code, name);
        NAME_TO_CODE.put(name, code);
    }

    public static int parse(String name) {
        if (name == null || name.isEmpty()) {
            return -1;
        }
        Integer code = NAME_TO_CODE.get(name);
        if (code != null) {
            return code;
        }
        if (name.startsWith("key")) {
            try {
                return Integer.parseInt(name.substring(3));
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    public static String format(int glfwCode) {
        String name = CODE_TO_NAME.get(glfwCode);
        if (name != null) {
            return name;
        }
        return "key" + glfwCode;
    }

    public static List<Integer> parseCombo(String combo) {
        if (combo == null || combo.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(combo.split("\\+"))
            .map(KeyNames::parse)
            .filter(code -> code >= 0)
            .toList();
    }

    public static String formatCombo(List<Integer> codes) {
        return codes.stream()
            .map(KeyNames::format)
            .collect(Collectors.joining("+"));
    }

    public static boolean isModifier(int glfwCode) {
        return glfwCode == 340 || glfwCode == 341 || glfwCode == 342
            || glfwCode == 344 || glfwCode == 345 || glfwCode == 346;
    }

    public static int extractMainKey(List<Integer> codes) {
        if (codes.isEmpty()) return -1;
        return codes.get(codes.size() - 1);
    }

    public static int extractModifier(List<Integer> codes) {
        for (int i = 0; i < codes.size() - 1; i++) {
            int code = codes.get(i);
            if (isModifier(code)) return code;
        }
        return -1;
    }

    private KeyNames() {}
}
