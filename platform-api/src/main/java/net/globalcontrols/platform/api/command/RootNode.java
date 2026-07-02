package net.globalcontrols.platform.api.command;

import java.util.List;

public record RootNode(List<CommandDefinition> children) implements CommandDefinition {
    @Override
    public String name() {
        return "root";
    }
}
