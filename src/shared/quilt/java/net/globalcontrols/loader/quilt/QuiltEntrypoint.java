package net.globalcontrols.loader.quilt;

import net.globalcontrols.common.bootstrap.ModBootstrap;
import net.globalcontrols.platform.api.*;
import net.globalcontrols.platform.brigadier.BrigadierCommandAdapter;
import net.globalcontrols.platform.brigadier.BrigadierControlProvider;
import net.globalcontrols.platform.brigadier.BrigadierModProvider;
import net.globalcontrols.platform.brigadier.handler.EmiHandler;
import net.globalcontrols.platform.brigadier.handler.JeiHandler;
import net.globalcontrols.platform.brigadier.handler.ReiHandler;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;

import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public class QuiltEntrypoint {
    private static final Logger LOG = Logger.getLogger("GlobalControls");
    private final BrigadierCommandAdapter commandAdapter = new BrigadierCommandAdapter();

    public QuiltEntrypoint() {
        Path configDir = QuiltLoader.getConfigDir();
        String mcVersion = QuiltLoader.getNormalizedGameVersion();

        List<ExternalControlHandler> handlers = List.of(
            new JeiHandler(configDir),
            new EmiHandler(configDir),
            new ReiHandler(configDir)
        );

        PlatformServices services = new PlatformServices() {
            @Override
            public CommandPlatform commands() {
                return root -> commandAdapter.adapt(root);
            }

            @Override
            public ControlPlatform controls() {
                return new BrigadierControlProvider();
            }

            @Override
            public ModPlatform mods() {
                return new BrigadierModProvider();
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
        CommandRegistrationHelper.registerViaFabricApi(commandAdapter, getClass().getClassLoader());
    }
}