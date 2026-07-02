package net.globalcontrols.common.bootstrap;

import net.globalcontrols.common.autocomplete.ControlSuggestions;
import net.globalcontrols.common.autocomplete.InstalledModSuggestions;
import net.globalcontrols.common.command.CommandTreeBuilder;
import net.globalcontrols.common.config.ConfigData;
import net.globalcontrols.common.config.ConfigManager;
import net.globalcontrols.common.config.JsonConfigManager;
import net.globalcontrols.common.config.OsDefaults;
import net.globalcontrols.common.service.BindingRegistry;
import net.globalcontrols.common.service.ControlService;
import net.globalcontrols.common.service.KeyInterceptorHolder;
import net.globalcontrols.common.service.KeyStateTracker;
import net.globalcontrols.platform.api.PlatformServices;
import net.globalcontrols.platform.api.command.CommandDefinition;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ModBootstrap {
    private static final Logger LOG = Logger.getLogger("GlobalControls");

    private ModBootstrap() {}

    public static void init(PlatformServices services) {
        try {
            ConfigManager configManager = new JsonConfigManager(services.configDir().getConfigDirectory());
            ConfigData config = configManager.load();

            if (config.globalControlsFilePath().isEmpty()) {
                config = new ConfigData(
                    OsDefaults.defaultGlobalControlsFilePath(detectMcVersion(services)),
                    config.firstLaunchCompleted()
                );
                configManager.save(config);
            }

            BindingRegistry registry = new BindingRegistry();
            ControlService controlService = new ControlService(services.controls(), registry);

            KeyStateTracker tracker = new KeyStateTracker(registry, services::fireKeyAction);
            KeyInterceptorHolder.set(tracker);

            CommandDefinition tree = CommandTreeBuilder.build(
                config, controlService, services,
                new InstalledModSuggestions(services.mods()),
                new ControlSuggestions(services.controls())
            );

            services.commands().register(tree);
            LOG.info("GlobalControls initialized");
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Failed to initialize GlobalControls", e);
        }
    }

    private static String detectMcVersion(PlatformServices services) {
        return "unknown";
    }
}
