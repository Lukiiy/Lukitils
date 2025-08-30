package me.lukiiy.utils.idk;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import me.lukiiy.utils.Defaults;
import me.lukiiy.utils.Lukitils;
import me.lukiiy.utils.help.MassEffect;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MassEffectArgument implements CustomArgumentType.Converted<MassEffect, String> {
    private final CommandSyntaxException NOT_FOUND = Defaults.CmdException(Component.text("Effect \"$type\" doesn't exist."));

    @Override
    public @NotNull MassEffect convert(@NotNull String nativeType) throws CommandSyntaxException {
        MassEffect m = Lukitils.getInstance().getMassEffects().get(nativeType);
        if (m == null) throw NOT_FOUND;
        return m;
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, SuggestionsBuilder builder) {
        Map<String, MassEffect> map = Lukitils.getInstance().getMassEffects();
        map.keySet().forEach(it -> {
            if (it.toLowerCase().startsWith(builder.getRemainingLowerCase())) {
                MassEffect m = map.get(it);
                builder.suggest(it, MessageComponentSerializer.message().serialize(Component.text(m.name()).color(Defaults.YELLOW).append(Component.text(" → ").color(Defaults.ORANGE)).append(Component.text(m.description()).color(Defaults.YELLOW))));
            }
        });

        return builder.buildFuture();
    }
}
