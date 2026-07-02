package net.globalcontrols.platform.api.command;

import java.util.List;

@FunctionalInterface
public interface CommandExecutor {
    void execute(List<String> args);
}
