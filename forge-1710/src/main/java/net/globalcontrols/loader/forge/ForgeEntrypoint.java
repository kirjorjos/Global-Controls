package net.globalcontrols.loader.forge;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.globalcontrols.common.bootstrap.ModBootstrap;
import net.globalcontrols.platform.api.*;
import net.globalcontrols.platform.legacy.LegacyCommandAdapter;
import net.globalcontrols.platform.legacy.LegacyControlProvider;
import net.globalcontrols.platform.legacy.LegacyModProvider;
import net.globalcontrols.platform.legacy.handler.NeiHandler;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = "globalcontrols", version = "1.0.0")
public class ForgeEntrypoint {
    private final LegacyCommandAdapter commandAdapter = new LegacyCommandAdapter();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Path configDir = event.getModConfigurationDirectory().toPath();
        String mcVersion = cpw.mods.fml.common.Loader.instance().getMCVersionString();

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
            public void fireKeyAction(String translationKey) {
                LegacyControlProvider.fireKey(translationKey);
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