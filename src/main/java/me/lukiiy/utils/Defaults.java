package me.lukiiy.utils;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class Defaults {
    public static final MiniMessage FancyString = MiniMessage.builder()
            .preProcessor(input -> MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(input)))
            .tags(TagResolver.standard())
            .build();

    // Colors
    public static final TextColor RED = TextColor.color(0xFF3854);
    public static final TextColor ORANGE = TextColor.color(0xFFAB4D);
    public static final TextColor YELLOW = TextColor.color(0xfffe52);
    public static final TextColor GREEN = TextColor.color(0x8ce636);
    public static final TextColor BLUE = TextColor.color(0x5BD2FF);
    public static final TextColor PURPLE = TextColor.color(0x8F5FFF);

    public static final TextColor WHITE = TextColor.color(0xDCFDFF);
    public static final TextColor GRAY = TextColor.color(0x7B9AB1);
    public static final TextColor DARK_GRAY = TextColor.color(0x4F5780);
    public static final TextColor BLACK = TextColor.color(0xE0D1A);

    // Symbols
    public static final Component PREFIX = Component.text("◆").color(BLUE);
    public static final Component SUCCESS_PREFIX = Component.text("★").color(YELLOW);
    public static final Component FAIL_PREFIX = Component.text("⚠").color(RED);
    public static final Component LIST_PREFIX = Component.text("• ").color(GRAY);

    public static final Component ON = Component.text("ᴏɴ").color(Defaults.GREEN);
    public static final Component OFF = Component.text("ᴏꜰꜰ").color(Defaults.RED);

    // Messages

    public static Component msg(@NotNull Component text) {return PREFIX.appendSpace().append(text.color(GRAY));}
    public static Component fail(@NotNull Component text) {return PREFIX.appendSpace().append(FAIL_PREFIX).appendSpace().append(text.color(GRAY));}

    // Command stuff
    public static CommandSyntaxException CmdException(@NotNull Component what) {return new SimpleCommandExceptionType(MessageComponentSerializer.message().serialize(fail(what))).create();}
    public static final CommandSyntaxException NOT_FOUND = CmdException(Component.translatable("argument.entity.notfound.player", "No player was found"));
    public static final CommandSyntaxException NON_PLAYER = CmdException(Component.text("This command can only be used by in-game players."));

    // Joins
    public static final JoinConfiguration DEF_SEPARATOR = JoinConfiguration.builder().separator(Component.text(',')).lastSeparator(Component.text(" and ")).build();
    public static final JoinConfiguration LIST_LIKE = JoinConfiguration.builder().prefix(LIST_PREFIX).separator(Component.newline().append(LIST_PREFIX)).build();
}
