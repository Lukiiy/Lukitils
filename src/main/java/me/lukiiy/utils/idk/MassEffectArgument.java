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
    private static CommandSyntaxException NotFound(String type) {
        return Defaults.CmdException(Component.text("No effect \"").append(Component.text(type).color(Defaults.RED)).append(Component.text("\" was found")));
    }

    @Override
    public @NotNull MassEffect convert(@NotNull String nativeType) throws CommandSyntaxException {
        MassEffect mass = Lukitils.getInstance().getMassEffects().get(nativeType);
        if (mass == null) throw NotFound(nativeType);

        return mass;
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
                MassEffect mass = map.get(it);

                builder.suggest(it, MessageComponentSerializer.message().serialize(Component.text(mass.name()).color(Defaults.YELLOW).append(Component.text(" → ").color(Defaults.ORANGE)).append(Component.text(mass.description()).color(Defaults.YELLOW))));
            }
        });

        return builder.buildFuture();
    }
}
