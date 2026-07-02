package net.globalcontrols.platform.api;

public interface PlatformServices {
    CommandPlatform commands();
    ControlPlatform controls();
    ModPlatform mods();
    ConfigDirProvider configDir();

    /** Fire a Minecraft keybinding action by translation key.
     *  Called by {@code KeyStateTracker} when a combo match is found. */
    void fireKeyAction(String translationKey);
}
