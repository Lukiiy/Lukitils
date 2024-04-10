package me.lukiiy.utils.cool;

import me.lukiiy.utils.main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerHelper {
    // todo: reorganize file

    public static Location getSpawnLocation(Player p) {
        Location location = p.getRespawnLocation();
        return location != null ? location : p.getWorld().getSpawnLocation();
    }

    public static Player getCommandTarget(Player p, String[] args) {
        if (args.length == 0) return p;
        Player t = Bukkit.getPlayerExact(args[0]);
        return t == null ? p : t;
    }

    public static Player getCommandTarget(Player p, String[] args, int argValue) {
        if (args.length < argValue) return p;
        Player t = Bukkit.getPlayerExact(args[argValue - 1]);
        return t == null ? p : t;
    }

    public static Component getLocationComponent(Location loc) {
        String string = loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
        return Component.text(string)
                .clickEvent(ClickEvent.suggestCommand("/tp @s " + string))
                .hoverEvent(HoverEvent.showText(Component.text("Click to suggest TP Command").color(NamedTextColor.GREEN)));
    }

    public static Component state(boolean condition) {
        return condition ? main.ON : main.OFF;
    }

}
