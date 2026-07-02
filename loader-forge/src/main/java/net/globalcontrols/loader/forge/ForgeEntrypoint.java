package net.globalcontrols.loader.forge;

import net.globalcontrols.common.bootstrap.ModBootstrap;
import net.globalcontrols.platform.api.*;
import net.globalcontrols.platform.legacy.LegacyCommandAdapter;
import net.globalcontrols.platform.legacy.LegacyControlProvider;
import net.globalcontrols.platform.legacy.LegacyModProvider;
import net.globalcontrols.platform.legacy.handler.NeiHandler;
import net.globalcontrols.platform.brigadier.BrigadierCommandAdapter;
import net.globalcontrols.platform.brigadier.BrigadierControlProvider;
import net.globalcontrols.platform.brigadier.BrigadierModProvider;
import net.globalcontrols.platform.brigadier.handler.*;

import java.util.ArrayList;
import java.util.List;

// TODO: annotate with @Mod("globalcontrols") and appropriate Forge entrypoint
public class ForgeEntrypoint {
    public void onInitialize() {
        PlatformServices services = buildServices();
        ModBootstrap.init(services);
    }

    private PlatformServices buildServices() {
        return new PlatformServices() {
            @Override
            public CommandPlatform commands() {
                // TODO: select adapter based on Minecraft version
                return root -> new LegacyCommandAdapter().adapt(root);
            }

            @Override
            public ControlPlatform controls() {
                // TODO: select provider based on Minecraft version
                return new LegacyControlProvider();
            }

            @Override
            public ModPlatform mods() {
                return new LegacyModProvider();
            }

            @Override
            public ConfigDirProvider configDir() {
                return () -> java.nio.file.Paths.get("config");
            }

            @Override
            public void fireKeyAction(String translationKey) {
                // TODO: look up KeyBinding by translationKey, call KeyBinding.onPress() or simulate press
            }

            @Override
            public String minecraftVersion() {
                // TODO: once compiled against real MC: return ForgeVersion.getMCVersion() or Loader.getMCVersionString()
                return "1.12.2";
            }

            @Override
            public List<ExternalControlHandler> externalHandlers() {
                java.nio.file.Path dir = configDir().getConfigDirectory();
                String mcVer = minecraftVersion();
                List<ExternalControlHandler> handlers = new ArrayList<>();
                handlers.add(new NeiHandler(dir, mcVer));
                // On Forge 1.13+ JEI is available via Brigadier
                if (!mcVer.startsWith("1.7.") && !mcVer.startsWith("1.8.") && !mcVer.startsWith("1.10.")) {
                    handlers.add(new JeiHandler(dir));
                    handlers.add(new ReiHandler(dir));
                }
                return handlers;
            }
        };
    }
}
