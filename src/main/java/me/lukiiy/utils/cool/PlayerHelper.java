package me.lukiiy.utils.cool;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerHelper {
    public static Location getSpawnLocation(Player p) {
        Location location = p.getRespawnLocation();
        return location != null ? location : p.getWorld().getSpawnLocation();
    }

    public static Component getLocationComponent(Location loc) {
        String string = loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
        return Component.text(string)
                .clickEvent(ClickEvent.suggestCommand("/tp @s " + string))
                .hoverEvent(HoverEvent.showText(Component.text("Click to suggest TP Command").color(NamedTextColor.GREEN)));
    }

}
