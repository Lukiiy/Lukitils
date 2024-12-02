package me.lukiiy.utils.help;

import me.lukiiy.utils.Defaults;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Location;

public class ComponentUtils {
    public static Component coolLocation(Location loc) {
        return Component.text(loc.blockX() + " " + loc.blockY() + " " + loc.blockZ())
                .hoverEvent(HoverEvent.showText(Component.text("Suggest command").color(Defaults.YELLOW)))
                .clickEvent(ClickEvent.suggestCommand("/tp @s " + loc.blockX() + " " + loc.blockY() + " " + loc.blockZ())).append(Component.text(" @ " + loc.getWorld().getName()));
    }
}
