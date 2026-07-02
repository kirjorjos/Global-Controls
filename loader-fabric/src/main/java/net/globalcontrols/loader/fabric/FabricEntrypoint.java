package net.globalcontrols.loader.fabric;

import net.globalcontrols.common.bootstrap.ModBootstrap;
import net.globalcontrols.platform.api.*;
import net.globalcontrols.platform.brigadier.BrigadierCommandAdapter;
import net.globalcontrols.platform.brigadier.BrigadierControlProvider;
import net.globalcontrols.platform.brigadier.BrigadierModProvider;
import net.globalcontrols.platform.brigadier.handler.*;

import java.util.List;

// TODO: implement ModInitializer (e.g. net.fabricmc.api.ModInitializer)
public class FabricEntrypoint {
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

            @Override
            public void fireKeyAction(String translationKey) {
                // TODO: look up KeyMapping by translationKey, call KeyMapping.click()
            }

            @Override
            public String minecraftVersion() {
                // TODO: once compiled against real MC: return net.minecraft.SharedConstants.getReleaseVersion()
                return "1.21";
            }

            @Override
            public List<ExternalControlHandler> externalHandlers() {
                java.nio.file.Path dir = configDir().getConfigDirectory();
                return List.of(
                    new JeiHandler(dir),
                    new EmiHandler(dir),
                    new ReiHandler(dir)
                );
            }
        };
    }
}
