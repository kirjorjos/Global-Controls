package net.globalcontrols.loader.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.globalcontrols.platform.api.ExternalControlHandler;
import net.globalcontrols.platform.brigadier.BrigadierCommandAdapter;
import net.globalcontrols.platform.brigadier.BrigadierControlProvider;
import net.globalcontrols.platform.brigadier.BrigadierModProvider;
import net.globalcontrols.platform.brigadier.handler.EmiHandler;
import net.globalcontrols.platform.brigadier.handler.JeiHandler;
import net.globalcontrols.platform.brigadier.handler.ReiHandler;

import java.nio.file.Path;
import java.util.List;

public class FabricEntrypoint implements ModInitializer {

    @Override
    public void onInitialize() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        String mcVersion = "1.20.1";

        List<ExternalControlHandler> handlers = List.of(
            new JeiHandler(configDir),
            new EmiHandler(configDir),
            new ReiHandler(configDir)
        );

        LoaderBootstrap.init(
            root -> new BrigadierCommandAdapter().adapt(root),
            new BrigadierControlProvider(),
            new BrigadierModProvider(),
            configDir,
            key -> {},
            mcVersion,
            handlers
        );
    }
}
