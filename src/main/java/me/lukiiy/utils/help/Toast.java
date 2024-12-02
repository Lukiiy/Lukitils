package me.lukiiy.utils.help;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.lukiiy.utils.Lukitils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@SuppressWarnings("deprecation")
public class Toast {
    private static final Gson gson = new Gson();

    public static void display(String origin, List<Player> players, Style style, ItemStack item, Component title, boolean chatAnnounce) {
        NamespacedKey key = new NamespacedKey(Lukitils.getInstance(), origin + "-" + System.currentTimeMillis());

        JsonObject advancement = new JsonObject();
        JsonObject display = new JsonObject();

        JsonObject icon = new JsonObject();
        icon.addProperty("id", item.getType().getKey().asString());

        if (item.getItemMeta() != null) {
            JsonObject components = componentDataParser(item.getItemMeta().getAsComponentString());
            if (components != null) {icon.add("components", components);}
        }

        display.add("icon", icon);
        display.add("title", JsonParser.parseString(JSONComponentSerializer.json().serialize(title)));
        display.addProperty("description", "");
        display.addProperty("frame", style.name().toLowerCase());
        display.addProperty("show_toast", true);
        display.addProperty("announce_to_chat", chatAnnounce);
        display.addProperty("hidden", true);

        advancement.add("display", display);

        JsonObject criteria = new JsonObject();
        JsonObject criterion = new JsonObject();
        criterion.addProperty("trigger", "minecraft:impossible");
        criteria.add("a", criterion);
        advancement.add("criteria", criteria);

        Advancement adv = Bukkit.getUnsafe().loadAdvancement(key, gson.toJson(advancement));
        players.forEach(p -> p.getAdvancementProgress(adv).awardCriteria("a"));

        Bukkit.getScheduler().runTaskLater(Lukitils.getInstance(), () -> {
            players.forEach(p -> p.getAdvancementProgress(adv).revokeCriteria("a"));
            Bukkit.getUnsafe().removeAdvancement(key);
        }, 5L);
    }

    public enum Style {
        TASK,
        GOAL,
        CHALLENGE
    }

    private static JsonObject componentDataParser(String data) {
        if (data == null || data.length() < 2) return null;

        JsonObject components = new JsonObject();
        String content = data.substring(1, data.length() - 1);

        // Split comma && Keep quotes
        String[] parts = content.split("(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$),");

        for (String part : parts) {
            String[] keyValue = part.trim().split("=", 2);
            if (keyValue.length != 2) continue;

            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            if (value.endsWith("b")) { // Boolean
                value = value.substring(0, value.length() - 1);
                components.addProperty(key, value.equals("1"));
            }
            else if (value.matches("^-?\\d+$")) components.addProperty(key, Integer.parseInt(value)); // Int
            else if (value.startsWith("'\"") && value.endsWith("\"'")) { // Component
                value = value.substring(2, value.length() - 2);
                components.addProperty(key, "{\"text\":\"" + value + "\"}");
            }
            else if (value.startsWith("\"") && value.endsWith("\"")) components.addProperty(key, value.substring(1, value.length() - 1)); // String
            else components.addProperty(key, value);
        }

        return components;
    }
}
