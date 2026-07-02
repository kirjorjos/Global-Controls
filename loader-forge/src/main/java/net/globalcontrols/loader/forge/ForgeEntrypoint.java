package net.globalcontrols.loader.forge;

import net.globalcontrols.common.bootstrap.ModBootstrap;
import net.globalcontrols.platform.api.PlatformServices;
import net.globalcontrols.platform.api.CommandPlatform;
import net.globalcontrols.platform.api.ControlPlatform;
import net.globalcontrols.platform.api.ModPlatform;
import net.globalcontrols.platform.api.ConfigDirProvider;
import net.globalcontrols.platform.legacy.LegacyCommandAdapter;
import net.globalcontrols.platform.legacy.LegacyControlProvider;
import net.globalcontrols.platform.legacy.LegacyModProvider;
import net.globalcontrols.platform.brigadier.BrigadierCommandAdapter;
import net.globalcontrols.platform.brigadier.BrigadierControlProvider;
import net.globalcontrols.platform.brigadier.BrigadierModProvider;

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
        };
    }
}
