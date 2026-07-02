package net.globalcontrols.loader.forge;

import net.globalcontrols.common.bootstrap.ModBootstrap;
import net.globalcontrols.platform.api.*;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

public final class LoaderBootstrap {
    private LoaderBootstrap() {}

    public static void init(
        CommandPlatform commands,
        ControlPlatform controls,
        ModPlatform mods,
        Path configDir,
        Consumer<String> fireKeyAction,
        String minecraftVersion,
        List<ExternalControlHandler> externalHandlers
    ) {
        PlatformServices services = new PlatformServices() {
            @Override
            public CommandPlatform commands() { return commands; }

            @Override
            public ControlPlatform controls() { return controls; }

            @Override
            public ModPlatform mods() { return mods; }

            @Override
            public ConfigDirProvider configDir() { return () -> configDir; }

            @Override
            public void fireKeyAction(String key) { fireKeyAction.accept(key); }

            @Override
            public String minecraftVersion() { return minecraftVersion; }

            @Override
            public List<ExternalControlHandler> externalHandlers() { return externalHandlers; }
        };
        ModBootstrap.init(services);
    }
}
