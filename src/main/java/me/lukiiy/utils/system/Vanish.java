package me.lukiiy.utils.system;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import me.lukiiy.utils.cool.PlayerHelper;
import me.lukiiy.utils.cool.Presets;
import me.lukiiy.utils.main;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Vanish implements CommandExecutor, Listener {
    public static List<UUID> vanish = new ArrayList<>();

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

        boolean invisible = vanish.contains(target.getUniqueId());
        Component state = invisible ? main.OFF : main.ON;

        Component message = Presets.Companion.msg("Vanish is now ").append(state);

        if (target != commandSender) {
            commandSender.sendMessage(message.append(Presets.Companion.why(" for ")).append(target.name().color(Presets.Companion.getACCENT_NEUTRAL())));
            message = message.append(Presets.Companion.why(" (by " + commandSender.getName() + ")"));
        }

        if (!invisible) hide(target);
        else show(target);
        target.sendMessage(message);
        return true;
    }

    private void hide(Player player) {
        Bukkit.getOnlinePlayers().stream()
                .filter(online -> !player.equals(online))
                .forEach(online -> online.hidePlayer(main.plugin, player));
        vanish.add(player.getUniqueId());
    }

    private void show(Player player) {
        Bukkit.getOnlinePlayers().stream()
                .filter(online -> !player.equals(online))
                .forEach(online -> online.showPlayer(main.plugin, player));
        vanish.remove(player.getUniqueId());
    }

    @EventHandler
    public void revanish(PlayerJoinEvent e) {
        if (vanish.isEmpty()) return;
        Player player = e.getPlayer();

        if (vanish.contains(player.getUniqueId())) {
            hide(player);
        }

        Bukkit.getScheduler().runTaskLater(main.plugin, () -> {
            for (UUID vanished : vanish) {
                Player v = Bukkit.getPlayer(vanished);
                if (v != null) player.hidePlayer(main.plugin, v);
            }
        }, 5L);
    }


    @EventHandler
    public void serverlist(PaperServerListPingEvent e) {
        Iterator<Player> iterator = e.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (vanish.contains(player.getUniqueId())) iterator.remove();
        }
    }
}
