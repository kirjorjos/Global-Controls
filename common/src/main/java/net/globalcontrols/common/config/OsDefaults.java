package net.globalcontrols.common.config;

public final class OsDefaults {
    private OsDefaults() {}

    public static String defaultGlobalControlsFilePath(String mcVersion) {
        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");
        String base;
        if (os.startsWith("windows")) {
            String appdata = System.getenv("APPDATA");
            if (appdata == null) {
                appdata = home + "\\AppData\\Roaming";
            }
            base = appdata + "\\GlobalControls";
        } else if (os.startsWith("mac")) {
            base = home + "/Library/Application Support/GlobalControls";
        } else {
            base = home + "/.local/share/GlobalControls";
        }
        return base + "/" + mcVersion + "-controls.json";
    }
}
