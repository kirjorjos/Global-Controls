package net.globalcontrols.common.command;

import net.globalcontrols.common.config.ConfigData;
import net.globalcontrols.common.model.KeyNames;
import net.globalcontrols.common.service.ControlService;
import net.globalcontrols.common.service.GlobalProfileService;
import net.globalcontrols.platform.api.InstalledMod;
import net.globalcontrols.platform.api.PlatformServices;
import net.globalcontrols.platform.api.command.*;

import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class CommandTreeBuilder {
    private static final Logger LOG = Logger.getLogger("GlobalControls");

    private CommandTreeBuilder() {}

    public static CommandDefinition build(
        ConfigData config,
        ControlService controlService,
        PlatformServices services,
        Supplier<List<String>> modSuggestions,
        Supplier<List<String>> controlSuggestions
    ) {
        GlobalProfileService profile = new GlobalProfileService(
            Paths.get(config.globalControlsFilePath())
        );
        Supplier<List<String>> allModIds = () -> services.mods().getInstalledMods().stream()
            .map(InstalledMod::id).toList();

        var pushAll = new LiteralNode("all", List.of(),
            args -> pushAllMods(profile, controlService, allModIds));
        var pushControlArg = new ArgumentNode<String>("control", controlSuggestions,
            List.of(), args -> pushSingle(profile, controlService, args));
        var pushModBranch = new ArgumentNode<String>("mod", allModIds, List.of(
            new LiteralNode("all", List.of(), args -> pushAllForMod(profile, controlService, args)),
            pushControlArg
        ), args -> pushAllForMod(profile, controlService, args));
        var push = new LiteralNode("push", List.of(pushAll, pushModBranch));

        var loadAll = new LiteralNode("all", List.of(),
            args -> loadAllMods(profile, controlService, allModIds));
        var loadControlArg = new ArgumentNode<String>("control", controlSuggestions,
            List.of(), args -> loadSingle(profile, controlService, args));
        var loadModBranch = new ArgumentNode<String>("mod", allModIds, List.of(
            new LiteralNode("all", List.of(), args -> loadAllForMod(profile, controlService, args)),
            loadControlArg
        ), args -> loadAllForMod(profile, controlService, args));
        var load = new LiteralNode("load", List.of(loadAll, loadModBranch));

        var setKeyArg = new ArgumentNode<String>("key", () -> List.of(),
            List.of(), args -> handleSet(profile, controlService, args));
        var setControlArg = new ArgumentNode<String>("control", controlSuggestions,
            List.of(setKeyArg), args -> handleSet(profile, controlService, args));
        var setModArg = new ArgumentNode<String>("mod", allModIds,
            List.of(setControlArg));
        var set = new LiteralNode("set", List.of(setModArg));

        var globalcontrols = new LiteralNode("globalcontrols", List.of(push, load, set));
        return new RootNode(List.of(globalcontrols));
    }

    private static void pushAllMods(GlobalProfileService profile, ControlService controlService, Supplier<List<String>> allModIds) {
        Map<String, Map<String, String>> data = profile.load();
        for (String modId : allModIds.get()) {
            Map<String, String> controls = controlService.getAllControls().stream()
                .filter(c -> c.translationKey().startsWith("key." + modId))
                .collect(Collectors.toMap(
                    c -> c.translationKey(),
                    c -> KeyNames.formatCombo(c.glfwCodes())
                ));
            if (!controls.isEmpty()) {
                data.put(modId, controls);
            }
        }
        profile.save(data);
        LOG.info("Pushed all controls for all mods");
    }

    private static void pushAllForMod(GlobalProfileService profile, ControlService controlService, List<String> args) {
        String modId = args.get(0);
        Map<String, String> controls = controlService.getAllControls().stream()
            .filter(c -> c.translationKey().startsWith("key." + modId))
            .collect(Collectors.toMap(
                c -> c.translationKey(),
                c -> KeyNames.formatCombo(c.glfwCodes())
            ));
        Map<String, Map<String, String>> data = profile.load();
        data.put(modId, controls);
        profile.save(data);
        LOG.info("Pushed controls for mod " + modId);
    }

    private static void pushSingle(GlobalProfileService profile, ControlService controlService, List<String> args) {
        String controlId = args.get(1);
        String combo = controlService.getAllControls().stream()
            .filter(c -> c.translationKey().equals(controlId))
            .findFirst()
            .map(c -> KeyNames.formatCombo(c.glfwCodes()))
            .orElse("");
        Map<String, Map<String, String>> data = profile.load();
        data.computeIfAbsent(args.get(0), k -> new java.util.HashMap<>()).put(controlId, combo);
        profile.save(data);
        LOG.info("Pushed control " + controlId);
    }

    private static void loadAllMods(GlobalProfileService profile, ControlService controlService, Supplier<List<String>> allModIds) {
        Map<String, Map<String, String>> data = profile.load();
        for (Map.Entry<String, Map<String, String>> modEntry : data.entrySet()) {
            if (!allModIds.get().contains(modEntry.getKey())) continue;
            for (Map.Entry<String, String> controlEntry : modEntry.getValue().entrySet()) {
                List<Integer> codes = KeyNames.parseCombo(controlEntry.getValue());
                controlService.applyCombo(controlEntry.getKey(), codes);
            }
        }
        LOG.info("Loaded controls for all mods");
    }

    private static void loadAllForMod(GlobalProfileService profile, ControlService controlService, List<String> args) {
        String modId = args.get(0);
        Map<String, Map<String, String>> data = profile.load();
        Map<String, String> modEntries = data.get(modId);
        if (modEntries == null) return;
        for (Map.Entry<String, String> entry : modEntries.entrySet()) {
            List<Integer> codes = KeyNames.parseCombo(entry.getValue());
            controlService.applyCombo(entry.getKey(), codes);
        }
        LOG.info("Loaded controls for mod " + modId);
    }

    private static void loadSingle(GlobalProfileService profile, ControlService controlService, List<String> args) {
        String controlId = args.get(1);
        Map<String, Map<String, String>> data = profile.load();
        Map<String, String> modEntries = data.get(args.get(0));
        if (modEntries == null || !modEntries.containsKey(controlId)) return;
        List<Integer> codes = KeyNames.parseCombo(modEntries.get(controlId));
        controlService.applyCombo(controlId, codes);
        LOG.info("Loaded control " + controlId);
    }

    private static void handleSet(GlobalProfileService profile, ControlService controlService, List<String> args) {
        String mod = args.get(0);
        String control = args.get(1);
        String key = args.size() > 2 ? args.get(2) : null;
        if (key == null || key.isEmpty()) {
            profile.removeEntry(mod, control);
            controlService.unset(control);
            LOG.info("Unset control " + control + " for mod " + mod);
        } else {
            List<Integer> codes = KeyNames.parseCombo(key);
            if (codes.isEmpty()) {
                LOG.warning("Invalid key combo: " + key);
                return;
            }
            profile.setEntry(mod, control, key);
            controlService.applyCombo(control, codes);
            LOG.info("Set control " + control + " for mod " + mod + " to " + key);
        }
    }
}
