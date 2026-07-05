package net.globalcontrols.loader.forge;

import net.globalcontrols.common.service.KeyInterceptorHolder;
import net.globalcontrols.platform.api.ExternalControlHandler;
import net.globalcontrols.platform.brigadier.BrigadierCommandAdapter;
import net.globalcontrols.platform.brigadier.BrigadierControlProvider;
import net.globalcontrols.platform.brigadier.BrigadierModProvider;
import net.globalcontrols.platform.brigadier.handler.JeiHandler;
import net.globalcontrols.platform.brigadier.handler.ReiHandler;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;
import net.minecraftforge.fml.loading.FMLPaths;
import java.util.List;

@Mod("globalcontrols")
public class ForgeEntrypoint {
    private final BrigadierCommandAdapter commandAdapter = new BrigadierCommandAdapter();

    public ForgeEntrypoint() {
        Path configDir = FMLPaths.CONFIGDIR.get();
        String mcVersion = "1.20.1";

        List<ExternalControlHandler> handlers = List.of(
            new JeiHandler(configDir),
            new ReiHandler(configDir)
        );

        LoaderBootstrap.init(
            root -> commandAdapter.adapt(root),
            new BrigadierControlProvider(),
            new BrigadierModProvider(),
            configDir,
            key -> BrigadierControlProvider.fireKey(key),
            mcVersion,
            handlers
        );

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        commandAdapter.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onKey(InputEvent.Key event) {
        var interceptor = KeyInterceptorHolder.get();
        if (interceptor != null) {
            interceptor.onKeyEvent(event.getKey(), event.getAction() != 0);
        }
    }
}
