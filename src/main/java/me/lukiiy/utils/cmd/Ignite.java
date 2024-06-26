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

public class Ignite implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length < 1) {
            commandSender.sendMessage(main.argsErrorMsg);
            return true;
        }
        Player target = Bukkit.getPlayer(strings[0]);
        if (target == null) {
            commandSender.sendMessage(main.argsErrorMsg);
            return true;
        }
        int duration;
        try {duration = Integer.parseInt(strings[1]);}
        catch (NumberFormatException ignored) {
            commandSender.sendMessage(Presets.Companion.warnMsg("Invalid number!"));
            return true;
        }
        if (duration <= 0) {
            commandSender.sendMessage(Presets.Companion.warnMsg("Must be a positive value greater than 0."));
            return true;
        }
        target.setFireTicks(duration * 20);
        commandSender.sendMessage(Presets.Companion.msg(Component.text("Set ").append(target.name().color(Presets.Companion.getACCENT_NEUTRAL())).append(Component.text(" on fire for " + duration + " seconds."))));
        return true;
    }
}
