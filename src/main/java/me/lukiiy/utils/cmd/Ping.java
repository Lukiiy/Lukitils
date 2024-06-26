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
        if (!(commandSender instanceof Player) && strings.length == 0) {
            commandSender.sendMessage(main.argsErrorMsg);
            return true;
        }
        Player target = strings.length > 0 ? Bukkit.getPlayer(strings[0]) : (Player) commandSender;
        if (target == null) {
            commandSender.sendMessage(main.notFoundMsg);
            return true;
        }
        commandSender.sendMessage(Presets.Companion.msg(target.name().append(Component.text(" has ").append(Component.text(target.getPing()).color(Presets.Companion.getACCENT_NEUTRAL())).append(Component.text(" ms.")))));
        return true;
    }
}
