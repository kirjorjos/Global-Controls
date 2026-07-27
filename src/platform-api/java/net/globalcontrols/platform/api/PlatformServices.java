package net.globalcontrols.platform.api;

import java.util.List;

public interface PlatformServices {
    CommandPlatform commands();
    ControlPlatform controls();
    ModPlatform mods();
    ConfigDirProvider configDir();

    /** Fire a Minecraft keybinding action by translation key.
     *  Called by {@code KeyStateTracker} when a combo match is found. */
    void fireKeyAction(String translationKey);

    /** The running Minecraft version string (e.g. "1.21", "1.12.2"). */
    String minecraftVersion();

    /** Handlers for mods that store keybinds in their own config files (JEI, EMI, REI, NEI, etc.). */
    List<ExternalControlHandler> externalHandlers();
}
