package net.globalcontrols.loader.forge;

import net.globalcontrols.common.bootstrap.ModBootstrap;
import net.globalcontrols.common.service.KeyInterceptorHolder;
import net.globalcontrols.platform.api.*;
import net.globalcontrols.platform.brigadier.BrigadierCommandAdapter;
import net.globalcontrols.platform.brigadier.BrigadierControlProvider;
import net.globalcontrols.platform.brigadier.BrigadierModProvider;
import net.globalcontrols.platform.brigadier.handler.JeiHandler;
import net.globalcontrols.platform.brigadier.handler.ReiHandler;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;

import java.nio.file.Path;
import java.util.List;

@Mod("globalcontrols")
public class ForgeEntrypoint {
    private final BrigadierCommandAdapter commandAdapter = new BrigadierCommandAdapter();

    public ForgeEntrypoint() {
        Path configDir = FMLPaths.CONFIGDIR.get();
                String mcVersion = FMLLoader.versionInfo().mcVersion();

        List<ExternalControlHandler> handlers = List.of(
            new JeiHandler(configDir),
            new ReiHandler(configDir)
        );

        PlatformServices services = new PlatformServices() {
            @Override
            public CommandPlatform commands() {
                return root -> commandAdapter.adapt(root);
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
                BrigadierControlProvider.fireKey(translationKey);
            }

            @Override
            public String minecraftVersion() { return mcVersion; }

            @Override
            public List<ExternalControlHandler> externalHandlers() { return handlers; }
        };

        ModBootstrap.init(services);

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