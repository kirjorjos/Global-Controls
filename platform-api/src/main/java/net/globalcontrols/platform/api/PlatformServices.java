package net.globalcontrols.platform.api;

public interface PlatformServices {
    CommandPlatform commands();
    ControlPlatform controls();
    ModPlatform mods();
    ConfigDirProvider configDir();
}
