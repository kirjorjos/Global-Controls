package net.globalcontrols.platform.api.command;

import java.util.List;
import java.util.function.Supplier;

public record ArgumentNode<T>(String name, Supplier<List<String>> suggestions, List<CommandDefinition> children, CommandExecutor executor) implements CommandDefinition {
    public ArgumentNode(String name, Supplier<List<String>> suggestions) {
        this(name, suggestions, List.of(), null);
    }

    public ArgumentNode(String name, Supplier<List<String>> suggestions, List<CommandDefinition> children) {
        this(name, suggestions, children, null);
    }
}
