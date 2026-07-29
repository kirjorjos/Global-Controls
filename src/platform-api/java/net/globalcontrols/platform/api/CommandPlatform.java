package net.globalcontrols.platform.api;

import net.globalcontrols.platform.api.command.CommandDefinition;

public interface CommandPlatform {
    void register(CommandDefinition root);
}
