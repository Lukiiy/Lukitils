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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Vanish implements CommandExecutor, Listener {
    public static List<UUID> vanish = new ArrayList<>();

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

        boolean invisible = vanish.contains(target.getUniqueId());
        Component state = invisible ? main.OFF : main.ON;

        Component message = Presets.Companion.msg("Vanish is now ").append(state);
        if (target != commandSender) {
            commandSender.sendMessage(message.append(Presets.Companion.why(" for ")).append(target.name().color(Presets.Companion.getACCENT_NEUTRAL())));
            message = message.append(Presets.Companion.why(" (by " + commandSender.getName() + ")"));
        }

        if (!invisible) {
            hide(target);
            vanish.add(target.getUniqueId());
        } else {
            show(target);
            vanish.remove(target.getUniqueId());
        }
        target.sendMessage(message);
        return true;
    }

    private void hide(Player p) {
        Bukkit.getOnlinePlayers().stream()
                .filter(online -> !p.equals(online))
                .forEach(online -> online.hidePlayer(main.plugin, p));
    }

    private void show(Player p) {
        Bukkit.getOnlinePlayers().stream()
                .filter(online -> !p.equals(online))
                .forEach(online -> online.showPlayer(main.plugin, p));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void join(PlayerJoinEvent e) {
        if (vanish.isEmpty()) return;
        Player p = e.getPlayer();

        if (vanish.contains(p.getUniqueId())) {
            e.joinMessage(null);
            hide(p);
            p.sendMessage(Presets.Companion.msg("You are still vanished."));
        }

        Bukkit.getScheduler().runTaskLater(main.plugin, () -> {
            for (UUID vanished : vanish) {
                Player v = Bukkit.getPlayer(vanished);
                if (v != null) p.hidePlayer(main.plugin, v);
            }
        }, 2L);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void quit(PlayerQuitEvent e) {
        if (vanish.isEmpty()) return;
        Player p = e.getPlayer();
        if (vanish.contains(p.getUniqueId())) e.quitMessage(null);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void noListing(PaperServerListPingEvent e) {
        List<PaperServerListPingEvent.ListedPlayerInfo> samples = e.getListedPlayers();
        samples.removeIf(p -> vanish.contains(p.id()));
        e.setNumPlayers(samples.size());
    }
}
