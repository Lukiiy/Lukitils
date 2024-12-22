package me.lukiiy.utils.help;

import me.lukiiy.utils.Lukitils;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerUtils {
    public static Location getSpawn(@NotNull Player p) {
        Location loc = p.getRespawnLocation();
        return loc != null ? loc : Bukkit.getWorlds().getFirst().getSpawnLocation();
    }

    public static void applyMultipTransientAttribute(@NotNull Player p, @NotNull Attribute attribute, @NotNull String id, double value) {
        NamespacedKey key = new NamespacedKey(Lukitils.getInstance(), id);
        AttributeInstance instance = p.getAttribute(attribute);
        if (instance == null) return;
        if (instance.getModifier(key) != null) instance.removeModifier(key);
        if (value != 0) instance.addTransientModifier(new AttributeModifier(key, value, AttributeModifier.Operation.MULTIPLY_SCALAR_1));
    }

    public static String group(@NotNull List<Player> list) {
        return list.size() == 1 ? list.getFirst().getName() : list.size() + " players";
    }

    public static Component group(@NotNull List<Player> list, @NotNull Style style) {
        if (list.size() == 1) return list.getFirst().name();
        return Component.text(group(list)).style(style).hoverEvent(HoverEvent.showText(Component.join(JoinConfiguration.builder().separator(Component.newline()).build(), list.stream().map(p -> p.name().style(style)).toList())));
    }
}
