package me.lukiiy.utils.cmd;

import me.lukiiy.utils.cool.PlayerHelper;
import me.lukiiy.utils.cool.Presets;
import me.lukiiy.utils.main;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Ping implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player target;

        if (!(commandSender instanceof Player)) {
            if (strings.length < 1) {
                commandSender.sendMessage(main.argsErrorMsg);
                return true;
            }
            target = Bukkit.getPlayer(strings[0]);
        } else target = PlayerHelper.getCommandTarget((Player) commandSender, strings);

        if (target == null) {
            commandSender.sendMessage(main.notFoundMsg);
            return true;
        }

        Component message = Presets.Companion.msg("You have ");

        if (target != commandSender) {
            message = target.displayName().color(Presets.Companion.getACCENT_NEUTRAL())
                    .append(Presets.Companion.why(" has "));
        }
        message = message.append(Component.text(target.getPing()).color(Presets.Companion.getACCENT_NEUTRAL())).append(Presets.Companion.why("ms."));

        commandSender.sendMessage(message);
        return true;
    }
}
