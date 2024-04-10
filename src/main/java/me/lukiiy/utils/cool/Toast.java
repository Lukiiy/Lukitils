package me.lukiiy.utils.cool;

import me.lukiiy.utils.main;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Toast {
    public static void display(Player p, Style style, String item, String msg, boolean chatAnnounce) {
        Advancement advancement = Bukkit.getUnsafe().loadAdvancement(
            new NamespacedKey(main.plugin, UUID.randomUUID().toString()), 
            "{\"criteria\": {\"trigger\":{\"trigger\":\"minecraft:impossible\"}}," +
            "\"display\": {\"icon\":{\"item\":\"minecraft:" + item.toLowerCase() + "\"}," +
            "\"title\": {\"text\":\"" + msg + "\"}," +
            "\"description\": {\"text\":\"\"}," +
            "\"background\": \"minecraft:textures/gui/advancements/backgrounds/adventure.png\"," +
            "\"frame\": \"" + style.name().toLowerCase() + "\"," +
            "\"announce_to_chat\": " + chatAnnounce + "," + "\"show_toast\": true," + "\"hidden\": true}," +
            "\"requirements\": [[\"trigger\"]]}"
        );
        AdvancementProgress progress = p.getAdvancementProgress(advancement);
        progress.awardCriteria("trigger");
        Bukkit.getScheduler().runTaskLater(main.plugin, () -> progress.revokeCriteria("trigger"), 10L);
    }
    
    public static enum Style {
        TASK,
        GOAL,
        CHALLENGE
    }
}
