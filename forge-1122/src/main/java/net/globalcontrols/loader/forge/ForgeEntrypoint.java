package net.globalcontrols.loader.forge;

import net.globalcontrols.platform.api.ExternalControlHandler;
import net.globalcontrols.platform.legacy.LegacyCommandAdapter;
import net.globalcontrols.platform.legacy.LegacyControlProvider;
import net.globalcontrols.platform.legacy.LegacyModProvider;
import net.globalcontrols.platform.legacy.handler.NeiHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = "globalcontrols", version = "1.0.0")
public class ForgeEntrypoint {
    private final LegacyCommandAdapter commandAdapter = new LegacyCommandAdapter();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Path configDir = event.getModConfigurationDirectory().toPath();
        String mcVersion = net.minecraftforge.fml.common.Loader.instance().getMCVersionString();

        List<ExternalControlHandler> handlers = new ArrayList<>();
        handlers.add(new NeiHandler(configDir, mcVersion));

        LoaderBootstrap.init(
            root -> commandAdapter.adapt(root),
            new LegacyControlProvider(),
            new LegacyModProvider(),
            configDir,
            key -> LegacyControlProvider.fireKey(key),
            mcVersion,
            handlers
        );

        commandAdapter.register();
    }
}
