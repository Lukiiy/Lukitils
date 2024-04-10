package me.lukiiy.utils.cmd;

import me.lukiiy.utils.cool.Presets;
import me.lukiiy.utils.cool.Toast;
import me.lukiiy.utils.main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DisplayToast implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(main.nonPlayerMsg);
            return true;
        }

        if (strings.length < 4) {
            commandSender.sendMessage(main.argsErrorMsg);
            return true;
        }

        Player target = Bukkit.getPlayer(strings[0]);
        if (target == null) {
            commandSender.sendMessage(main.notFoundMsg);
            return true;
        }

        Toast.Style style;
        try {
            style = Toast.Style.valueOf(strings[1].toUpperCase());
        } catch (Throwable n) {
            commandSender.sendMessage(Presets.Companion.warnMsg("Style not found!"));
            return true;
        }

        String item = strings[2];
        try {
            Material.valueOf(item.toUpperCase());
        } catch (Throwable n) {
            commandSender.sendMessage(Presets.Companion.warnMsg("Material ID not found!"));
            return true;
        }

        String msg = String.join(" ", Arrays.copyOfRange(strings, 3, strings.length))
                .replace("/n", "\n")
                .replace("&", "§");
        boolean chatAnnounce = msg.contains("-c ");
        if (chatAnnounce) msg = msg.replace("-c ", "");

        commandSender.sendMessage(Presets.Companion.msg("Displayed custom Toast to ").append(target.name().color(Presets.Companion.getACCENT_NEUTRAL())));
        Toast.display(target, style, item, msg, chatAnnounce);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> tab = new ArrayList<>();
        switch (strings.length) {
            case 1:
                return null;
            case 2:
                for (Toast.Style type : Toast.Style.values()) tab.add(type.name());
                break;
            case 3:
                for (Material id : Material.values()) tab.add(id.name().toLowerCase());
                break;
        }
        return tab.stream().filter(next -> next.toLowerCase().startsWith(strings[strings.length - 1])).collect(Collectors.toList());
    }
}
