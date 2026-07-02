package net.globalcontrols.loader.quilt;

import net.globalcontrols.common.bootstrap.ModBootstrap;
import net.globalcontrols.platform.api.PlatformServices;
import net.globalcontrols.platform.api.CommandPlatform;
import net.globalcontrols.platform.api.ControlPlatform;
import net.globalcontrols.platform.api.ModPlatform;
import net.globalcontrols.platform.api.ConfigDirProvider;
import net.globalcontrols.platform.brigadier.BrigadierCommandAdapter;
import net.globalcontrols.platform.brigadier.BrigadierControlProvider;
import net.globalcontrols.platform.brigadier.BrigadierModProvider;

// TODO: implement Quilt ModInitializer
public class QuiltEntrypoint {
    public void onInitialize() {
        PlatformServices services = buildServices();
        ModBootstrap.init(services);
    }

    private PlatformServices buildServices() {
        return new PlatformServices() {
            @Override
            public CommandPlatform commands() {
                return root -> new BrigadierCommandAdapter().adapt(root);
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
                return () -> java.nio.file.Paths.get("config");
            }
        };
    }
}
