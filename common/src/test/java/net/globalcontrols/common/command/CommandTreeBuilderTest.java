package net.globalcontrols.common.command;

import net.globalcontrols.common.config.ConfigData;
import net.globalcontrols.common.service.ControlService;
import net.globalcontrols.platform.api.*;
import net.globalcontrols.platform.api.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class CommandTreeBuilderTest {
    @TempDir
    Path tempDir;

    private ConfigData config;
    private ControlService controlService;
    private PlatformServices services;
    private Supplier<List<String>> modSuggestions;
    private Supplier<List<String>> controlSuggestions;
    private List<ControlInfo> registeredControls;
    private List<String> appliedCombos;
    private List<String> unsetKeys;
    private List<String> externalWrittenControls;
    private List<String> externalBulkWritten;

    @BeforeEach
    void setUp() {
        config = new ConfigData(tempDir.resolve("profile.json").toString(), true);

        registeredControls = new ArrayList<>();
        appliedCombos = new ArrayList<>();
        unsetKeys = new ArrayList<>();
        externalWrittenControls = new ArrayList<>();
        externalBulkWritten = new ArrayList<>();

        controlService = new ControlService(new ControlPlatform() {
            @Override
            public Collection<ControlInfo> getControls() {
                return List.copyOf(registeredControls);
            }

            @Override
            public void setKey(String translationKey, List<Integer> codes) {
                appliedCombos.add(translationKey);
            }

            @Override
            public void unsetKey(String translationKey) {
                unsetKeys.add(translationKey);
            }
        }, null) {
            @Override
            public void unset(String translationKey) {
                unsetKeys.add(translationKey);
            }
            @Override
            public void applyCombo(String translationKey, List<Integer> codes) {
                appliedCombos.add(translationKey);
            }
        };

        modSuggestions = () -> List.of("minecraft", "jei");
        controlSuggestions = () -> List.of("key.forward", "key.jump");

        var extHandler = new ExternalControlHandler() {
            @Override
            public String modId() { return "jei"; }

            @Override
            public List<String> getControlIds() { return List.of("key.jei.show"); }

            @Override
            public Map<String, String> readControls() {
                return Map.of("key.jei.show", "LCTRL+O", "key.jei.recipe", "R");
            }

            @Override
            public void writeControl(String controlId, int glfwCode) {
                externalWrittenControls.add(controlId + "=" + glfwCode);
            }

            @Override
            public void writeControls(Map<String, Integer> controls) {
                controls.forEach((k, v) -> externalBulkWritten.add(k + "=" + v));
            }
        };

        services = new PlatformServices() {
            @Override
            public CommandPlatform commands() { return null; }
            @Override
            public ControlPlatform controls() { return null; }
            @Override
            public ModPlatform mods() {
                return () -> List.of(new InstalledMod("minecraft", "Minecraft"), new InstalledMod("jei", "JEI"));
            }
            @Override
            public ConfigDirProvider configDir() { return null; }
            @Override
            public void fireKeyAction(String translationKey) {}
            @Override
            public String minecraftVersion() { return "1.20.1"; }
            @Override
            public List<ExternalControlHandler> externalHandlers() { return List.of(extHandler); }
        };
    }

    // --- structure tests ---

    @Test
    void treeHasGlobalControlsRoot() {
        CommandDefinition def = CommandTreeBuilder.build(config, controlService, services, modSuggestions, controlSuggestions);
        assertInstanceOf(RootNode.class, def);
        RootNode root = (RootNode) def;
        assertEquals(1, root.children().size());
        assertEquals("globalcontrols", root.children().get(0).name());
    }

    @Test
    void treeHasPushLoadSetSubcommands() {
        CommandDefinition def = CommandTreeBuilder.build(config, controlService, services, modSuggestions, controlSuggestions);
        RootNode root = (RootNode) def;
        LiteralNode gc = (LiteralNode) root.children().get(0);
        Set<String> names = gc.children().stream().map(CommandDefinition::name).collect(Collectors.toSet());
        assertEquals(Set.of("push", "load", "set"), names);
    }

    @Test
    void pushHasAllAndModChildren() {
        CommandDefinition def = CommandTreeBuilder.build(config, controlService, services, modSuggestions, controlSuggestions);
        LiteralNode push = findChild((RootNode) def, "globalcontrols", "push");
        Set<String> names = push.children().stream().map(CommandDefinition::name).collect(Collectors.toSet());
        assertEquals(Set.of("all", "mod"), names);
    }

    @Test
    void loadHasAllAndModChildren() {
        CommandDefinition def = CommandTreeBuilder.build(config, controlService, services, modSuggestions, controlSuggestions);
        LiteralNode load = findChild((RootNode) def, "globalcontrols", "load");
        Set<String> names = load.children().stream().map(CommandDefinition::name).collect(Collectors.toSet());
        assertEquals(Set.of("all", "mod"), names);
    }

    @Test
    void setHasModArgument() {
        CommandDefinition def = CommandTreeBuilder.build(config, controlService, services, modSuggestions, controlSuggestions);
        LiteralNode set = findChild((RootNode) def, "globalcontrols", "set");
        assertEquals(1, set.children().size());
        assertEquals("mod", set.children().get(0).name());
    }

    @Test
    void argumentHasExpectedName() {
        CommandDefinition def = CommandTreeBuilder.build(config, controlService, services, modSuggestions, controlSuggestions);
        LiteralNode set = findChild((RootNode) def, "globalcontrols", "set");
        assertInstanceOf(ArgumentNode.class, set.children().get(0));
    }

    @Test
    void setModArgumentHasControlChild() {
        CommandDefinition def = CommandTreeBuilder.build(config, controlService, services, modSuggestions, controlSuggestions);
        LiteralNode set = findChild((RootNode) def, "globalcontrols", "set");
        ArgumentNode<?> mod = (ArgumentNode<?>) set.children().get(0);
        assertFalse(mod.children().isEmpty());
        assertEquals("control", mod.children().get(0).name());
    }

    @Test
    void setControlArgumentHasKeyChild() {
        CommandDefinition def = CommandTreeBuilder.build(config, controlService, services, modSuggestions, controlSuggestions);
        LiteralNode set = findChild((RootNode) def, "globalcontrols", "set");
        ArgumentNode<?> mod = (ArgumentNode<?>) set.children().get(0);
        ArgumentNode<?> control = (ArgumentNode<?>) mod.children().get(0);
        assertEquals("key", control.children().get(0).name());
    }

    @Test
    void pushModBranchHasAllAndControlChildren() {
        CommandDefinition def = CommandTreeBuilder.build(config, controlService, services, modSuggestions, controlSuggestions);
        LiteralNode push = findChild((RootNode) def, "globalcontrols", "push");
        ArgumentNode<?> modBranch = (ArgumentNode<?>) push.children().stream()
            .filter(c -> c instanceof ArgumentNode).findFirst().get();
        Set<String> names = modBranch.children().stream().map(CommandDefinition::name).collect(Collectors.toSet());
        assertEquals(Set.of("all", "control"), names);
    }

    // --- executor behavior tests ---

    @Test
    void setNoKeyCallsUnset() {
        CommandDefinition def = CommandTreeBuilder.build(config, controlService, services, modSuggestions, controlSuggestions);
        LiteralNode set = findChild((RootNode) def, "globalcontrols", "set");
        ArgumentNode<?> mod = (ArgumentNode<?>) set.children().get(0);
        ArgumentNode<?> control = (ArgumentNode<?>) mod.children().get(0);
        control.executor().execute(List.of("minecraft", "key.forward"));
        assertTrue(unsetKeys.contains("key.forward"));
    }

    @Test
    void setWithUnboundCallsUnset() {
        CommandDefinition def = CommandTreeBuilder.build(config, controlService, services, modSuggestions, controlSuggestions);
        LiteralNode set = findChild((RootNode) def, "globalcontrols", "set");
        ArgumentNode<?> mod = (ArgumentNode<?>) set.children().get(0);
        ArgumentNode<?> control = (ArgumentNode<?>) mod.children().get(0);
        ArgumentNode<?> key = (ArgumentNode<?>) control.children().get(0);
        key.executor().execute(List.of("minecraft", "key.forward", "Unbound"));
        assertTrue(unsetKeys.contains("key.forward"));
    }

    @Test
    void setWithValidKeyCallsApplyCombo() {
        CommandDefinition def = CommandTreeBuilder.build(config, controlService, services, modSuggestions, controlSuggestions);
        LiteralNode set = findChild((RootNode) def, "globalcontrols", "set");
        ArgumentNode<?> mod = (ArgumentNode<?>) set.children().get(0);
        ArgumentNode<?> control = (ArgumentNode<?>) mod.children().get(0);
        ArgumentNode<?> key = (ArgumentNode<?>) control.children().get(0);
        key.executor().execute(List.of("minecraft", "key.forward", "W"));
        assertTrue(appliedCombos.contains("key.forward"));
    }

    @Test
    void setWithUnboundOnExternalHandlerCallsWriteControl() {
        CommandDefinition def = CommandTreeBuilder.build(config, controlService, services, modSuggestions, controlSuggestions);
        LiteralNode set = findChild((RootNode) def, "globalcontrols", "set");
        ArgumentNode<?> mod = (ArgumentNode<?>) set.children().get(0);
        ArgumentNode<?> control = (ArgumentNode<?>) mod.children().get(0);
        ArgumentNode<?> key = (ArgumentNode<?>) control.children().get(0);
        key.executor().execute(List.of("jei", "key.jei.show", "Unbound"));
        assertTrue(externalWrittenControls.contains("key.jei.show=-1"));
    }

    @Test
    void setWithValidKeyOnExternalHandlerCallsWriteControl() {
        CommandDefinition def = CommandTreeBuilder.build(config, controlService, services, modSuggestions, controlSuggestions);
        LiteralNode set = findChild((RootNode) def, "globalcontrols", "set");
        ArgumentNode<?> mod = (ArgumentNode<?>) set.children().get(0);
        ArgumentNode<?> control = (ArgumentNode<?>) mod.children().get(0);
        ArgumentNode<?> key = (ArgumentNode<?>) control.children().get(0);
        key.executor().execute(List.of("jei", "key.jei.show", "LCTRL+O"));
        assertTrue(externalWrittenControls.contains("key.jei.show=79"));
    }

    @Test
    void setWithNoKeyOnExternalHandlerCallsWriteControl() {
        CommandDefinition def = CommandTreeBuilder.build(config, controlService, services, modSuggestions, controlSuggestions);
        LiteralNode set = findChild((RootNode) def, "globalcontrols", "set");
        ArgumentNode<?> mod = (ArgumentNode<?>) set.children().get(0);
        ArgumentNode<?> control = (ArgumentNode<?>) mod.children().get(0);
        control.executor().execute(List.of("jei", "key.jei.show"));
        assertTrue(externalWrittenControls.contains("key.jei.show=-1"));
    }

    @Test
    void unboundInSuggestions() {
        CommandDefinition def = CommandTreeBuilder.build(config, controlService, services, modSuggestions, controlSuggestions);
        LiteralNode set = findChild((RootNode) def, "globalcontrols", "set");
        ArgumentNode<?> mod = (ArgumentNode<?>) set.children().get(0);
        ArgumentNode<?> control = (ArgumentNode<?>) mod.children().get(0);
        ArgumentNode<?> key = (ArgumentNode<?>) control.children().get(0);
        assertTrue(key.suggestions().get().contains("Unbound"));
    }

    @Test
    void pushAllWritesProfileFile() {
        registeredControls.add(new ControlInfo("key.forward", "Forward", "minecraft", List.of(87)));
        CommandDefinition def = CommandTreeBuilder.build(config, controlService, services, modSuggestions, controlSuggestions);
        LiteralNode pushAll = findChild((RootNode) def, "globalcontrols", "push", "all");
        pushAll.executor().execute(List.of());
        assertTrue(tempDir.resolve("profile.json").toFile().exists());
    }

    @Test
    void pushModDefaultExecutorSavesToProfile() {
        CommandDefinition def = CommandTreeBuilder.build(config, controlService, services, modSuggestions, controlSuggestions);
        ArgumentNode<?> mod = findArg((RootNode) def, "globalcontrols", "push", "mod");
        mod.executor().execute(List.of("jei"));
        assertTrue(tempDir.resolve("profile.json").toFile().exists());
    }

    @Test
    void loadAllForExternalHandlerCallsWriteControls() {
        // First push JEI controls into profile
        CommandDefinition def = CommandTreeBuilder.build(config, controlService, services, modSuggestions, controlSuggestions);
        ArgumentNode<?> pushMod = findArg((RootNode) def, "globalcontrols", "push", "mod");
        pushMod.executor().execute(List.of("jei"));
        // Then load them
        def = CommandTreeBuilder.build(config, controlService, services, modSuggestions, controlSuggestions);
        ArgumentNode<?> loadMod = findArg((RootNode) def, "globalcontrols", "load", "mod");
        loadMod.executor().execute(List.of("jei"));
        assertTrue(externalBulkWritten.contains("key.jei.show=79"));
        assertTrue(externalBulkWritten.contains("key.jei.recipe=82"));
    }

    // --- helpers ---

    private LiteralNode findChild(RootNode root, String... path) {
        CommandDefinition node = root;
        for (String name : path) {
            node = descend(node, name);
        }
        if (!(node instanceof LiteralNode)) {
            throw new AssertionError("Expected LiteralNode at end of path " + Arrays.toString(path) + " but got " + node.getClass().getSimpleName());
        }
        return (LiteralNode) node;
    }

    @SuppressWarnings("unchecked")
    private ArgumentNode<String> findArg(RootNode root, String... path) {
        CommandDefinition node = root;
        for (int i = 0; i < path.length - 1; i++) {
            node = descend(node, path[i]);
        }
        CommandDefinition last = descend(node, path[path.length - 1]);
        if (!(last instanceof ArgumentNode)) {
            throw new AssertionError("Expected ArgumentNode at end of path " + Arrays.toString(path) + " but got " + last.getClass().getSimpleName());
        }
        return (ArgumentNode<String>) last;
    }

    private CommandDefinition descend(CommandDefinition node, String name) {
        if (node instanceof RootNode rn) {
            return rn.children().stream().filter(c -> c.name().equals(name)).findFirst().orElseThrow();
        } else if (node instanceof LiteralNode ln) {
            return ln.children().stream().filter(c -> c.name().equals(name)).findFirst().orElseThrow();
        } else if (node instanceof ArgumentNode<?> an) {
            return an.children().stream().filter(c -> c.name().equals(name)).findFirst().orElseThrow();
        }
        throw new AssertionError("Unknown node type: " + node.getClass().getSimpleName());
    }
}
