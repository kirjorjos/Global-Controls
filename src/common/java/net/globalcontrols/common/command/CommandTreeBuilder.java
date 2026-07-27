package net.globalcontrols.common.command;

import net.globalcontrols.common.config.ConfigData;
import net.globalcontrols.common.model.KeyNames;
import net.globalcontrols.common.service.ControlService;
import net.globalcontrols.common.service.GlobalProfileService;
import net.globalcontrols.platform.api.ExternalControlHandler;
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
        List<ExternalControlHandler> externalHandlers = services.externalHandlers();
        Supplier<List<String>> allModIds = () -> services.mods().getInstalledMods().stream()
            .map(InstalledMod::id).toList();

        var pushAll = new LiteralNode("all", List.of(),
            args -> pushAllMods(profile, controlService, externalHandlers, allModIds));
        var pushControlArg = new ArgumentNode<String>("control", controlSuggestions,
            List.of(), args -> pushSingle(profile, controlService, externalHandlers, args));
        var pushModBranch = new ArgumentNode<String>("mod", allModIds, List.of(
            new LiteralNode("all", List.of(), args -> pushAllForMod(profile, controlService, externalHandlers, args)),
            pushControlArg
        ), args -> pushAllForMod(profile, controlService, externalHandlers, args));
        var push = new LiteralNode("push", List.of(pushAll, pushModBranch));

        var loadAll = new LiteralNode("all", List.of(),
            args -> loadAllMods(profile, controlService, externalHandlers, allModIds));
        var loadControlArg = new ArgumentNode<String>("control", controlSuggestions,
            List.of(), args -> loadSingle(profile, controlService, externalHandlers, args));
        var loadModBranch = new ArgumentNode<String>("mod", allModIds, List.of(
            new LiteralNode("all", List.of(), args -> loadAllForMod(profile, controlService, externalHandlers, args)),
            loadControlArg
        ), args -> loadAllForMod(profile, controlService, externalHandlers, args));
        var load = new LiteralNode("load", List.of(loadAll, loadModBranch));

        var setKeyArg = new ArgumentNode<String>("key", () -> List.of(KeyNames.UNBOUND_NAME),
            List.of(), args -> handleSet(profile, controlService, externalHandlers, args));
        var setControlArg = new ArgumentNode<String>("control", controlSuggestions,
            List.of(setKeyArg), args -> handleSet(profile, controlService, externalHandlers, args));
        var setModArg = new ArgumentNode<String>("mod", allModIds,
            List.of(setControlArg));
        var set = new LiteralNode("set", List.of(setModArg));

        var globalcontrols = new LiteralNode("globalcontrols", List.of(push, load, set));
        return new RootNode(List.of(globalcontrols));
    }

    private static ExternalControlHandler findHandler(List<ExternalControlHandler> handlers, String modId) {
        return handlers.stream().filter(h -> h.modId().equals(modId)).findFirst().orElse(null);
    }

    private static void pushAllMods(GlobalProfileService profile, ControlService controlService,
                                    List<ExternalControlHandler> extHandlers, Supplier<List<String>> allModIds) {
        Map<String, Map<String, String>> data = profile.load();
        for (String modId : allModIds.get()) {
            ExternalControlHandler handler = findHandler(extHandlers, modId);
            if (handler != null) {
                data.put(modId, handler.readControls());
            } else {
                Map<String, String> controls = controlService.getAllControls().stream()
                    .filter(c -> c.translationKey().startsWith("key." + modId))
                    .collect(Collectors.toMap(c -> c.translationKey(), c -> {
                        String s = KeyNames.formatCombo(c.glfwCodes());
                        return s.isEmpty() ? KeyNames.UNBOUND_NAME : s;
                    }));
                if (!controls.isEmpty()) data.put(modId, controls);
            }
        }
        profile.save(data);
        LOG.info("Pushed all controls");
    }

    private static void pushAllForMod(GlobalProfileService profile, ControlService controlService,
                                      List<ExternalControlHandler> extHandlers, List<String> args) {
        String modId = args.get(0);
        ExternalControlHandler handler = findHandler(extHandlers, modId);
        Map<String, String> controls;
        if (handler != null) {
            controls = handler.readControls();
        } else {
            controls = controlService.getAllControls().stream()
                .filter(c -> c.translationKey().startsWith("key." + modId))
                .collect(Collectors.toMap(c -> c.translationKey(), c -> {
                    String s = KeyNames.formatCombo(c.glfwCodes());
                    return s.isEmpty() ? KeyNames.UNBOUND_NAME : s;
                }));
        }
        Map<String, Map<String, String>> data = profile.load();
        data.put(modId, controls);
        profile.save(data);
        LOG.info("Pushed mod " + modId);
    }

    private static void pushSingle(GlobalProfileService profile, ControlService controlService,
                                   List<ExternalControlHandler> extHandlers, List<String> args) {
        String modId = args.get(0);
        String controlId = args.get(1);
        ExternalControlHandler handler = findHandler(extHandlers, modId);
        String combo;
        if (handler != null) {
            combo = handler.readControls().getOrDefault(controlId, "");
        } else {
            combo = controlService.getAllControls().stream()
                .filter(c -> c.translationKey().equals(controlId))
                .findFirst()
                .map(c -> KeyNames.formatCombo(c.glfwCodes()))
                .orElse("");
        }
        if (combo.isEmpty()) combo = KeyNames.UNBOUND_NAME;
        Map<String, Map<String, String>> data = profile.load();
        data.computeIfAbsent(modId, k -> new HashMap<>()).put(controlId, combo);
        profile.save(data);
        LOG.info("Pushed control " + controlId);
    }

    private static void loadAllMods(GlobalProfileService profile, ControlService controlService,
                                    List<ExternalControlHandler> extHandlers, Supplier<List<String>> allModIds) {
        Map<String, Map<String, String>> data = profile.load();
        for (Map.Entry<String, Map<String, String>> modEntry : data.entrySet()) {
            if (!allModIds.get().contains(modEntry.getKey())) continue;
            applyModBindings(modEntry.getKey(), modEntry.getValue(), controlService, extHandlers);
        }
        LOG.info("Loaded all controls");
    }

    private static void loadAllForMod(GlobalProfileService profile, ControlService controlService,
                                      List<ExternalControlHandler> extHandlers, List<String> args) {
        String modId = args.get(0);
        Map<String, Map<String, String>> data = profile.load();
        Map<String, String> modEntries = data.get(modId);
        if (modEntries == null) return;
        applyModBindings(modId, modEntries, controlService, extHandlers);
        LOG.info("Loaded mod " + modId);
    }

    private static void loadSingle(GlobalProfileService profile, ControlService controlService,
                                   List<ExternalControlHandler> extHandlers, List<String> args) {
        String controlId = args.get(1);
        Map<String, Map<String, String>> data = profile.load();
        Map<String, String> modEntries = data.get(args.get(0));
        if (modEntries == null || !modEntries.containsKey(controlId)) return;
        applySingleBinding(args.get(0), controlId, modEntries.get(controlId), controlService, extHandlers);
        LOG.info("Loaded control " + controlId);
    }

    private static void applyModBindings(String modId, Map<String, String> entries,
                                         ControlService controlService, List<ExternalControlHandler> extHandlers) {
        ExternalControlHandler handler = findHandler(extHandlers, modId);
        if (handler != null) {
            Map<String, Integer> parsed = new LinkedHashMap<>();
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                List<Integer> codes = KeyNames.parseCombo(entry.getValue());
                if (isUnbound(codes)) {
                    handler.writeControl(entry.getKey(), -1);
                } else if (!codes.isEmpty()) {
                    parsed.put(entry.getKey(), codes.get(codes.size() - 1));
                }
            }
            handler.writeControls(parsed);
        } else {
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                List<Integer> codes = KeyNames.parseCombo(entry.getValue());
                if (isUnbound(codes)) {
                    controlService.unset(entry.getKey());
                } else {
                    controlService.applyCombo(entry.getKey(), codes);
                }
            }
        }
    }

    private static boolean isUnbound(List<Integer> codes) {
        return codes.size() == 1 && codes.get(0) == KeyNames.UNBOUND_CODE;
    }

    private static void applySingleBinding(String modId, String controlId, String keyName,
                                           ControlService controlService, List<ExternalControlHandler> extHandlers) {
        ExternalControlHandler handler = findHandler(extHandlers, modId);
        List<Integer> codes = KeyNames.parseCombo(keyName);
        if (isUnbound(codes)) {
            if (handler != null) {
                handler.writeControl(controlId, -1);
            } else {
                controlService.unset(controlId);
            }
        } else if (!codes.isEmpty()) {
            int mainKey = codes.get(codes.size() - 1);
            if (handler != null) {
                handler.writeControl(controlId, mainKey);
            } else {
                controlService.applyCombo(controlId, codes);
            }
        }
    }

    private static void handleSet(GlobalProfileService profile, ControlService controlService,
                                  List<ExternalControlHandler> extHandlers, List<String> args) {
        String mod = args.get(0);
        String control = args.get(1);
        String key = args.size() > 2 ? args.get(2) : null;
        ExternalControlHandler handler = findHandler(extHandlers, mod);

        if (key == null || key.isEmpty()) {
            profile.removeEntry(mod, control);
            if (handler != null) {
                handler.writeControl(control, -1);
            } else {
                controlService.unset(control);
            }
            LOG.info("Unset " + control + " for mod " + mod + " — will use default on next load");
            return;
        }

        if (key.equals(KeyNames.UNBOUND_NAME)) {
            profile.setEntry(mod, control, KeyNames.UNBOUND_NAME);
            if (handler != null) {
                handler.writeControl(control, -1);
            } else {
                controlService.unset(control);
            }
            LOG.info("Unbound " + control + " for mod " + mod);
            return;
        }

        List<Integer> codes = KeyNames.parseCombo(key);
        if (codes.isEmpty()) {
            LOG.warning("Invalid key combo: " + key);
            return;
        }
        profile.setEntry(mod, control, key);
        int mainKey = codes.get(codes.size() - 1);
        if (handler != null) {
            handler.writeControl(control, mainKey);
        } else {
            controlService.applyCombo(control, codes);
        }
        LOG.info("Set " + control + " for mod " + mod + " to " + key);
    }
}
