package net.globalcontrols.loader.forge;

import net.globalcontrols.common.bootstrap.ModBootstrap;
import net.globalcontrols.platform.api.*;
import net.globalcontrols.platform.legacy.LegacyCommandAdapter;
import net.globalcontrols.platform.legacy.LegacyControlProvider;
import net.globalcontrols.platform.legacy.LegacyModProvider;
import net.globalcontrols.platform.legacy.handler.NeiHandler;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared bootstrap logic for Forge 1.7.10 and 1.12.2.
 * Both versions use the legacy (non-brigadier) command platform.
 * Each per-loader entrypoint provides the version-specific {@code Loader} class.
 */
public final class LegacyForgeEntrypoint {
    private LegacyForgeEntrypoint() {}

    public static void init(LegacyCommandAdapter commandAdapter, Path configDir, String mcVersion) {
        List<ExternalControlHandler> handlers = new ArrayList<>();
        handlers.add(new NeiHandler(configDir, mcVersion));

        PlatformServices services = new PlatformServices() {
            @Override
            public CommandPlatform commands() {
                return root -> commandAdapter.adapt(root);
            }

            @Override
            public ControlPlatform controls() {
                return new LegacyControlProvider();
            }

            @Override
            public ModPlatform mods() {
                return new LegacyModProvider();
            }

            @Override
            public ConfigDirProvider configDir() {
                return () -> configDir;
            }

            @Override
            public String minecraftVersion() { return mcVersion; }

            @Override
            public List<ExternalControlHandler> externalHandlers() { return handlers; }
        };

        ModBootstrap.init(services);
        commandAdapter.register();
    }
}
