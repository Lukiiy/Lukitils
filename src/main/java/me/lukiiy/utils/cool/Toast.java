package me.lukiiy.utils.cool;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.lukiiy.utils.main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Toast {
    public static void display(Player p, Style style, Material item, String msg, boolean chatAnnounce) {
        NamespacedKey key = new NamespacedKey(main.plugin, UUID.randomUUID().toString());
        Advancement advancement = Bukkit.getUnsafe().loadAdvancement(key, "{\"display\":{\"icon\":{\"id\":\"" + item + "\"},\"title\":\"" + msg + "\",\"description\":\"\",\"frame\":\"" + style.name().toLowerCase() + "\",\"show_toast\":true,\"announce_to_chat\":" + chatAnnounce + ",\"hidden\":true},\"criteria\":{\"a\":{\"trigger\":\"minecraft:impossible\"}}}");
        AdvancementProgress progress = p.getAdvancementProgress(advancement);
        progress.awardCriteria("a");
        Bukkit.getScheduler().runTaskLater(main.plugin, () -> {
            progress.revokeCriteria("a");
            Bukkit.getUnsafe().removeAdvancement(key);
        }, 5L);
    }

    public enum Style {
        TASK,
        GOAL,
        CHALLENGE
    }
}
