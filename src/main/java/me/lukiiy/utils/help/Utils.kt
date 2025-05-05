package me.lukiiy.utils.help

import me.lukiiy.utils.Defaults
import me.lukiiy.utils.Lukitils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

object Utils {

    // Component Help!

    fun adminCmdFeedback(sender: CommandSender, message: String) {
        if (!Lukitils.getInstance().config.getBoolean("commandFeedback")) return

        val senderName = if (sender is ConsoleCommandSender) "Server".asFancyString() else sender.name()
        val msg: Component = Component.translatable("chat.type.admin").arguments(senderName, message.asFancyString()).color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)

        Bukkit.getOnlinePlayers().stream().filter { it.hasPermission("minecraft.admin.command_feedback") && it != sender }.forEach { it.sendMessage(msg) }
        if (sender !is ConsoleCommandSender) Lukitils.getInstance().componentLogger.trace(msg)
    }

    // String & Components Extensions!
    @JvmStatic
    fun String.asPermission(): String = Defaults.getPermission(this)

    @JvmStatic
    fun String.asFancyString(): Component = Defaults.FancyString.deserialize(this)

    @JvmStatic
    fun Location.toComponent(): Component {
        val coordString = "${this.blockX} ${this.blockY} ${this.blockZ}"
        return coordString.asFancyString().hoverEvent(HoverEvent.showText(Component.text("Click to Copy!").color(Defaults.YELLOW))).clickEvent(ClickEvent.copyToClipboard("/tp @s $coordString")).append(" @ ${this.world.name}".asFancyString())
    }

    // Player Extensions!

    @JvmStatic
    fun Player.getSpawn(): Location {
        val loc = this.respawnLocation
        return loc ?: Bukkit.getWorlds()[0].spawnLocation
    }

    @JvmStatic
    fun List<Player>.group(): String = if (this.size == 1) this.first().name else "${this.size} players"

    @JvmStatic
    fun List<Player>.group(style: Style): Component {
        if (this.size == 1) return this.first().name()
        return Component.text(this.group()).style(style).hoverEvent(HoverEvent.showText(Component.join(JoinConfiguration.newlines(), this.stream().map { p -> p.name().style(style) }.toList())))
    }

    @JvmStatic
    fun Player.addScalarTransientMod(id: String, attribute: Attribute, value: Double) {
        val key = NamespacedKey(Lukitils.getInstance(), id)
        val instance = this.getAttribute(attribute)

        if (instance == null) return
        if (instance.getModifier(key) != null) instance.removeModifier(key)
        if (value != 0.0) instance.addTransientModifier(AttributeModifier(key, value, AttributeModifier.Operation.MULTIPLY_SCALAR_1))
    }
}