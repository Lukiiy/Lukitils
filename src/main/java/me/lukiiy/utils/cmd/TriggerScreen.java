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
        if (!(commandSender instanceof Player) && strings.length == 0) {
            commandSender.sendMessage(main.argsErrorMsg);
            return true;
        }
        Player target = strings.length > 0 ? Bukkit.getPlayer(strings[0]) : (Player) commandSender;
        if (target == null) {
            commandSender.sendMessage(main.notFoundMsg);
            return true;
        }

        Effects type;
        try {type = Effects.valueOf(strings[1].toUpperCase());}
        catch (Throwable ignored) {
            commandSender.sendMessage(Presets.Companion.warnMsg("Effect not found!"));
            return true;
        }

        switch (type) {
            case DEMO -> target.showDemoScreen();
            case CREDITS -> target.showWinScreen();
            case GUARDIAN -> target.showElderGuardian();
        }

        commandSender.sendMessage(Presets.Companion.msg("Triggered " + type.name() + " effect to ").append(target.name().color(Presets.Companion.getACCENT_NEUTRAL())));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> tab = new ArrayList<>();
        switch (strings.length) {
            case 1 -> {return null;}
            case 2 -> {for (Effects e : Effects.values()) tab.add(e.name());}
        }
        return tab.stream().filter(next -> next.toLowerCase().startsWith(strings[strings.length - 1])).collect(Collectors.toList());
    }

    public enum Effects {
        DEMO,
        CREDITS,
        GUARDIAN
    }
}
