package net.globalcontrols.loader.forge;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.globalcontrols.platform.api.ExternalControlHandler;
import net.globalcontrols.platform.legacy.LegacyCommandAdapter;
import net.globalcontrols.platform.legacy.LegacyControlProvider;
import net.globalcontrols.platform.legacy.LegacyModProvider;
import net.globalcontrols.platform.legacy.handler.NeiHandler;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Mod(modid = "globalcontrols", version = "1.0.0")
public class ForgeEntrypoint {

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Path configDir = event.getModConfigurationDirectory().toPath();
        String mcVersion = cpw.mods.fml.common.Loader.instance().getMCVersionString();

        List<ExternalControlHandler> handlers = new ArrayList<>();
        handlers.add(new NeiHandler(configDir, mcVersion));

        LoaderBootstrap.init(
            root -> new LegacyCommandAdapter().adapt(root),
            new LegacyControlProvider(),
            new LegacyModProvider(),
            configDir,
            key -> {},
            mcVersion,
            handlers
        );
    }
}
