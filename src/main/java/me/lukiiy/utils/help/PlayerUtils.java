package me.lukiiy.utils.help;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerUtils {
    public static Location getSpawn(Player p) {
        Location loc = p.getRespawnLocation();
        return loc != null ? loc : Bukkit.getWorlds().getFirst().getSpawnLocation();
    }

    public static void applyScalarTransientAttribute(Player p, Attribute attribute, NamespacedKey key, double value) {
        AttributeInstance instance = p.getAttribute(attribute);
        if (instance == null) return;
        if (instance.getModifier(key) != null) instance.removeModifier(key);
        if (value == 1) instance.addTransientModifier(new AttributeModifier(key, value, AttributeModifier.Operation.ADD_SCALAR));
    }

    public static Component group(List<Player> list, Style style) {
        if (list.size() == 1) return list.getFirst().name();
        return Component.text(list.size() + " players").style(style).hoverEvent(HoverEvent.showText(Component.join(JoinConfiguration.builder().separator(Component.newline()).build(), list.stream().map(p -> p.name().style(style)).toList())));
    }
}
