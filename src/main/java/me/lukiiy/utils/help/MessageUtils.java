package me.lukiiy.utils.help;

import me.lukiiy.utils.Defaults;
import me.lukiiy.utils.Lukitils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

public class MessageUtils {
    public static Component coolLocation(Location loc) {
        return Component.text(loc.blockX() + " " + loc.blockY() + " " + loc.blockZ())
                .hoverEvent(HoverEvent.showText(Component.text("Suggest command").color(Defaults.YELLOW)))
                .clickEvent(ClickEvent.suggestCommand("/tp @s " + loc.blockX() + " " + loc.blockY() + " " + loc.blockZ())).append(Component.text(" @ " + loc.getWorld().getName()));
    }

    public static void adminCmdFeedback(@NotNull CommandSender sender, @NotNull String message) {
        Component senderName = (sender instanceof ConsoleCommandSender) ? Component.text("Server") : sender.name();

        Component msg = Component.translatable("chat.type.admin").arguments(senderName, Defaults.FancyString.deserialize(message)).color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC);

        Bukkit.getOnlinePlayers().stream()
                .filter(p -> p.hasPermission("minecraft.admin.command_feedback") && !p.equals(sender))
                .forEach(p -> p.sendMessage(msg));
        if (!(sender instanceof ConsoleCommandSender)) Lukitils.getInstance().getComponentLogger().trace(msg);
    }
}
