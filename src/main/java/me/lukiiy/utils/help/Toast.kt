package me.lukiiy.utils.help

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import me.lukiiy.utils.Lukitils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

object Toast {
    private val gson = Gson()

    @Suppress("DEPRECATION")
    fun display(origin: String?, players: List<Player?>, style: Style, item: ItemStack, title: Component, chatAnnounce: Boolean) {
        val key = NamespacedKey(Lukitils.getInstance(), "$origin-${UUID.randomUUID()}")

        val icon = JsonObject().apply {
            addProperty("id", item.type.key.asString())
            item.itemMeta?.asComponentString?.let { data -> componentDataParser(data)?.let { add("components", it) } }
        }

        val display = JsonObject().apply {
            add("icon", icon)
            add("title", gson.fromJson(JSONComponentSerializer.json().serialize(title), JsonElement::class.java))
            addProperty("description", "")
            addProperty("frame", style.name.lowercase())
            addProperty("show_toast", true)
            addProperty("announce_to_chat", chatAnnounce)
            addProperty("hidden", true)
        }

        val advancement = JsonObject().apply {
            add("display", display)
            add("criteria", JsonObject().apply {
                add("a", JsonObject().apply { addProperty("trigger", "minecraft:impossible") })
            })
        }

        val adv = Bukkit.getUnsafe().loadAdvancement(key, gson.toJson(advancement))
        players.forEach { it?.getAdvancementProgress(adv)?.awardCriteria("a") }

        Bukkit.getGlobalRegionScheduler().runDelayed(Lukitils.getInstance(), {
            players.forEach { it?.getAdvancementProgress(adv)?.revokeCriteria("a") }
            Bukkit.getUnsafe().removeAdvancement(key)
        }, 3L)
    }

    private fun componentDataParser(data: String?): JsonElement? {
        if (data.isNullOrBlank() || data.length < 2) return null

        val content = data.substring(1, data.length - 1)
        val parts = content.split("(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$),".toRegex())

        return JsonObject().apply {
            for (part in parts) {
                val (key, value) = part.trim().split("=", limit = 2).map { it.trim() }.takeIf { it.size == 2 } ?: continue

                val fV: JsonElement = when {
                    value.endsWith("b") -> JsonPrimitive(value.dropLast(1) == "1") // Boolean
                    value.matches(Regex("^-?\\d+$")) -> JsonPrimitive(value.toInt()) // Integer
                    value.startsWith("'\"") && value.endsWith("\"'") -> JsonPrimitive("{\"text\":\"${value.substring(2, value.length - 2)}\"}") // Component
                    value.startsWith("\"") && value.endsWith("\"") -> JsonPrimitive(value.substring(1, value.length - 1)) // String
                    else -> JsonPrimitive(value)
                }

                add(key, fV)
            }
        }
    }

    enum class Style {
        TASK,
        GOAL,
        CHALLENGE
    }
}