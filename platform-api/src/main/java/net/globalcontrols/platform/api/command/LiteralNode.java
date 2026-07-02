package net.globalcontrols.platform.api.command;

import java.util.List;

public record LiteralNode(String name, List<CommandDefinition> children, CommandExecutor executor) implements CommandDefinition {
    public LiteralNode(String name, List<CommandDefinition> children) {
        this(name, children, null);
    }
}
