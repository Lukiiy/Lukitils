package me.lukiiy.utils;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class Defaults {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.builder() // TODO
            .preProcessor(s -> s.replace('§', '&').replace("&0", "<black>").replace("&1", "<dark_blue>").replace("&2", "<dark_green>")
                    .replace("&3", "<dark_aqua>").replace("&4", "<dark_red>").replace("&5", "<dark_purple>")
                    .replace("&6", "<gold>").replace("&7", "<gray>").replace("&8", "<dark_gray>")
                    .replace("&9", "<blue>").replace("&a", "<green>").replace("&b", "<aqua>")
                    .replace("&c", "<red>").replace("&d", "<light_purple>").replace("&e", "<yellow>")
                    .replace("&f", "<white>").replace("&k", "<obfuscated>").replace("&l", "<bold>")
                    .replace("&m", "<strikethrough>").replace("&n", "<underlined>").replace("&o", "<italic>")
                    .replace("&r", "<reset>"))
            .tags(TagResolver.standard())
            .build();

    public static Component mini(String text) {return MINI_MESSAGE.deserialize(text);}
    public static Component reMini(Component text) {return MINI_MESSAGE.deserialize(MINI_MESSAGE.serialize(text));}

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

    public static final Component prefix = Component.text("◆").color(BLUE);
    public static final Component successPrefix = Component.text("★").color(YELLOW);
    public static final Component failPrefix = Component.text("⚠").color(RED);

    public static final Component LIST_PREFIX = Component.text("• ").color(GRAY);

    public static Component msg(Component text) {return prefix.appendSpace().append(text.color(GRAY));}
    public static Component fail(Component text) {return prefix.appendSpace().append(failPrefix).appendSpace().append(text.color(GRAY));}

    public static final SimpleCommandExceptionType NOT_FOUND = new SimpleCommandExceptionType(new LiteralMessage("Player not found.")); // precaution
    public static final SimpleCommandExceptionType NOT_FOUND_MULTI = new SimpleCommandExceptionType(new LiteralMessage("Player(s) not found."));
    public static final SimpleCommandExceptionType NON_PLAYER = new SimpleCommandExceptionType(new LiteralMessage("This command can only be used by in-game players."));
    public static SimpleCommandExceptionType CUSTOM_ERR(String what) {return new SimpleCommandExceptionType(new LiteralMessage(what));}

    public static final Component ON = Component.text("ᴏɴ").color(Defaults.GREEN);
    public static final Component OFF = Component.text("ᴏꜰꜰ").color(Defaults.RED);

    public static final JoinConfiguration SEPARATOR = JoinConfiguration.builder().separator(Component.text(',')).lastSeparator(Component.text(" and ")).build();
    public static final JoinConfiguration LIST_LIKE = JoinConfiguration.builder().prefix(LIST_PREFIX).separator(Component.newline().append(LIST_PREFIX)).build();
}
