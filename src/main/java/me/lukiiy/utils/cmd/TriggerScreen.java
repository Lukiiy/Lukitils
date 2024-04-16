package me.lukiiy.utils.cmd;

import me.lukiiy.utils.cool.PlayerHelper;
import me.lukiiy.utils.cool.Presets;
import me.lukiiy.utils.main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TriggerScreen implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length < 2) {
            commandSender.sendMessage(main.argsErrorMsg);
            return true;
        }

        Player target;
        if (!(commandSender instanceof Player)) target = Bukkit.getPlayer(strings[1]);
        else target = PlayerHelper.getCommandTarget((Player) commandSender, strings, 2);

        if (target == null) {
            commandSender.sendMessage(main.notFoundMsg);
            return true;
        }

        Effects type;
        try {
            type = Effects.valueOf(strings[0].toUpperCase());
        } catch (Throwable n) {
            commandSender.sendMessage(Presets.Companion.warnMsg("Effect not found!"));
            return true;
        }

        switch (type) {
            case DEMO:
                target.showDemoScreen();
                break;
            case CREDITS:
                target.showWinScreen();
                break;
            case GUARDIAN:
                target.showElderGuardian();
                break;
        }

        commandSender.sendMessage(Presets.Companion.msg("Triggered " + type.name() + " effect to ").append(target.name().color(Presets.Companion.getACCENT_NEUTRAL())));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> tab = new ArrayList<>();
        switch (strings.length) {
            case 1:
                for (Effects e : Effects.values()) tab.add(e.name());
                break;
            case 2:
                return null;
        }
        return tab.stream().filter(next -> next.toLowerCase().startsWith(strings[strings.length - 1])).collect(Collectors.toList());
    }

    public enum Effects {
        DEMO,
        CREDITS,
        GUARDIAN
    }
}
