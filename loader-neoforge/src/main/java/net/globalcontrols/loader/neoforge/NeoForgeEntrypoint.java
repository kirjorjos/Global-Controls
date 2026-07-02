package net.globalcontrols.loader.neoforge;

import net.globalcontrols.common.bootstrap.ModBootstrap;
import net.globalcontrols.platform.api.*;
import net.globalcontrols.platform.brigadier.BrigadierCommandAdapter;
import net.globalcontrols.platform.brigadier.BrigadierControlProvider;
import net.globalcontrols.platform.brigadier.BrigadierModProvider;
import net.globalcontrols.platform.brigadier.handler.EmiHandler;
import net.globalcontrols.platform.brigadier.handler.JeiHandler;
import net.globalcontrols.platform.brigadier.handler.ReiHandler;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.List;

@Mod("globalcontrols")
public class NeoForgeEntrypoint {

    public NeoForgeEntrypoint() {
        Path configDir = FMLPaths.CONFIGDIR.get();
        // TODO: use SharedConstants or FMLLoader at runtime
        String mcVersion = "1.21";

        List<ExternalControlHandler> handlers = List.of(
            new JeiHandler(configDir),
            new EmiHandler(configDir),
            new ReiHandler(configDir)
        );

        PlatformServices services = new PlatformServices() {
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
                return () -> configDir;
            }

            @Override
            public void fireKeyAction(String translationKey) {
                // TODO: look up KeyMapping by translationKey
            }

            @Override
            public String minecraftVersion() { return mcVersion; }

            @Override
            public List<ExternalControlHandler> externalHandlers() { return handlers; }
        };

        ModBootstrap.init(services);
    }
}
