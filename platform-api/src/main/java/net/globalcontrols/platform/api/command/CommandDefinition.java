package net.globalcontrols.platform.api.command;

public sealed interface CommandDefinition permits RootNode, LiteralNode, ArgumentNode {
    String name();
}
