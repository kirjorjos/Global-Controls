package net.globalcontrols.platform.legacy;

import net.globalcontrols.platform.api.command.*;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LegacyCommandAdapter {
    private static final Logger LOG = Logger.getLogger("GlobalControls");

    private CommandDefinition root;

    public void adapt(CommandDefinition root) {
        this.root = root;
    }

    public void register() {
        if (!(root instanceof RootNode rootNode)) return;
        for (CommandDefinition child : rootNode.children()) {
            if (child instanceof LiteralNode literal) {
                registerCommand(literal);
            }
        }
    }

    private void registerCommand(LiteralNode rootLiteral) {
        try {
            Object handler = getClientCommandHandler();
            if (handler == null) {
                LOG.warning("ClientCommandHandler not found, commands not registered");
                return;
            }
            Object command = createICommandProxy(rootLiteral);
            handler.getClass()
                .getMethod("registerCommand", Class.forName("net.minecraft.command.ICommand"))
                .invoke(handler, command);
            LOG.fine("Registered command: " + rootLiteral.name());
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Failed to register command", e);
        }
    }

    private static Object createICommandProxy(LiteralNode rootLiteral) throws Exception {
        Class<?> icommandClass = Class.forName("net.minecraft.command.ICommand");
        return Proxy.newProxyInstance(
            LegacyCommandAdapter.class.getClassLoader(),
            new Class<?>[]{icommandClass},
            (proxy, method, methodArgs) -> {
                switch (method.getName()) {
                    case "getCommandName":
                        return rootLiteral.name();
                    case "getCommandUsage":
                        return "/" + rootLiteral.name() + " <subcommand>";
                    case "getAliases":
                        return Collections.emptyList();
                    case "getRequiredPermissionLevel":
                        return 0;
                    case "checkPermission":
                        return true;
                    case "processCommand":
                        String[] args = (String[]) methodArgs[1];
                        processCommand(rootLiteral, args, 0, new ArrayList<>());
                        return 1;
                    case "compareTo":
                        return 0;
                    case "equals":
                        return proxy == methodArgs[0];
                    case "hashCode":
                        return System.identityHashCode(proxy);
                    case "toString":
                        return "GlobalControls/" + rootLiteral.name();
                    default:
                        return null;
                }
            }
        );
    }

    private static void processCommand(CommandDefinition node, String[] args,
                                        int index, List<String> collected) {
        if (index >= args.length) {
            executeIfPresent(node, collected);
            return;
        }

        List<CommandDefinition> children = getChildren(node);
        if (children == null) return;

        String current = args[index];

        for (CommandDefinition child : children) {
            if (child instanceof LiteralNode literal && literal.name().equals(current)) {
                processCommand(literal, args, index + 1, collected);
                return;
            }
        }

        for (CommandDefinition child : children) {
            if (child instanceof ArgumentNode) {
                collected.add(current);
                processCommand(child, args, index + 1, collected);
                return;
            }
        }

        executeIfPresent(node, collected);
    }

    private static List<CommandDefinition> getChildren(CommandDefinition node) {
        if (node instanceof LiteralNode lit) return lit.children();
        if (node instanceof ArgumentNode arg) return arg.children();
        if (node instanceof RootNode root) return root.children();
        return null;
    }

    private static void executeIfPresent(CommandDefinition node, List<String> collected) {
        CommandExecutor executor = null;
        if (node instanceof LiteralNode literal) {
            executor = literal.executor();
        } else if (node instanceof ArgumentNode arg) {
            executor = arg.executor();
        }
        if (executor != null) {
            executor.execute(new ArrayList<>(collected));
        }
    }

    private static Object getClientCommandHandler() {
        for (String className : new String[]{
            "net.minecraftforge.client.ClientCommandHandler",
            "cpw.mods.fml.client.ClientCommandHandler"
        }) {
            try {
                Class<?> clazz = Class.forName(className);
                try {
                    return clazz.getMethod("instance").invoke(null);
                } catch (Exception e1) {
                    return clazz.getDeclaredField("instance").get(null);
                }
            } catch (Exception e2) {
                // try next class name
            }
        }
        return null;
    }
}
