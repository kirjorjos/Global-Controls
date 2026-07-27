package net.globalcontrols.platform.brigadier;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import net.globalcontrols.platform.api.command.*;

import java.util.ArrayList;
import java.util.List;

public class BrigadierCommandAdapter {
    private CommandDefinition root;

    public void adapt(CommandDefinition root) {
        this.root = root;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <S> void register(CommandDispatcher<S> dispatcher) {
        if (!(root instanceof RootNode rootNode)) return;
        for (CommandDefinition child : rootNode.children()) {
            if (child instanceof LiteralNode literal) {
                dispatcher.register(buildLiteral(literal));
            }
        }
    }

    public void register(Object dispatcher) {
        register((CommandDispatcher<?>) dispatcher);
    }

    private <S> LiteralArgumentBuilder<S> buildLiteral(LiteralNode node) {
        LiteralArgumentBuilder<S> builder = LiteralArgumentBuilder.literal(node.name());
        if (node.executor() != null) {
            CommandExecutor executor = node.executor();
            builder = builder.executes(ctx -> {
                executor.execute(collectArgs(ctx));
                return Command.SINGLE_SUCCESS;
            });
        }
        for (CommandDefinition child : node.children()) {
            if (child instanceof LiteralNode literalChild) {
                builder = builder.then(buildLiteral(literalChild));
            } else if (child instanceof ArgumentNode argChild) {
                builder = builder.then(buildArgument(argChild));
            }
        }
        return builder;
    }

    private <S> RequiredArgumentBuilder<S, String> buildArgument(ArgumentNode<?> node) {
        RequiredArgumentBuilder<S, String> builder = RequiredArgumentBuilder
            .argument(node.name(), StringArgumentType.word());
        List<String> suggestions = node.suggestions().get();
        if (!suggestions.isEmpty()) {
            SuggestionProvider<S> provider = (ctx, sb) -> {
                for (String s : suggestions) sb.suggest(s);
                return sb.buildFuture();
            };
            builder = builder.suggests(provider);
        }
        if (node.executor() != null) {
            CommandExecutor executor = node.executor();
            builder = builder.executes(ctx -> {
                executor.execute(collectArgs(ctx));
                return Command.SINGLE_SUCCESS;
            });
        }
        for (CommandDefinition child : node.children()) {
            if (child instanceof LiteralNode literalChild) {
                builder = builder.then(buildLiteral(literalChild));
            } else if (child instanceof ArgumentNode argChild) {
                builder = builder.then(buildArgument(argChild));
            }
        }
        return builder;
    }

    private static List<String> collectArgs(CommandContext<?> ctx) {
        List<String> args = new ArrayList<>();
        for (ParsedCommandNode<?> node : ctx.getNodes()) {
            CommandNode<?> cmdNode = node.getNode();
            if (cmdNode instanceof ArgumentCommandNode) {
                try {
                    args.add(ctx.getArgument(cmdNode.getName(), String.class));
                } catch (IllegalArgumentException ignored) {}
            }
        }
        return args;
    }
}
