package me.lukiiy.utils.cmd;

import me.lukiiy.utils.cool.PlayerHelper;
import me.lukiiy.utils.cool.Presets;
import me.lukiiy.utils.main;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Feed implements CommandExecutor {
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

        int value = 20;
        if (strings.length > 1) {
            try {
                value = Integer.parseInt(strings[1]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(Presets.Companion.warnMsg("Invalid number!"));
                return true;
            }
        }
        if (value <= 0) {
            commandSender.sendMessage(Presets.Companion.warnMsg("Must be a positive value greater than 0."));
            return true;
        }
        target.setFoodLevel(value);

        Component message = Presets.Companion.msg("Fed ");

        if (target != commandSender) {
            commandSender.sendMessage(message.append(target.displayName().color(Presets.Companion.getACCENT_NEUTRAL())));
            message = message.append(Presets.Companion.why("(by " + commandSender.getName() + ")"));
        }

        target.sendMessage(message);
        return true;
    }
}
