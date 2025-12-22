package me.lukiiy.utils.help

import com.mojang.brigadier.context.CommandContext
import com.viaversion.viaversion.api.Via
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.Lukitils
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.`object`.ObjectContents
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

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
    fun Component.asLegacyString(): String = LegacyComponentSerializer.legacySection().serialize(this)

    @JvmStatic
    fun Component.asPlainString(): String = PlainTextComponentSerializer.plainText().serialize(this)

    @JvmStatic
    fun Location.toComponent(): Component {
        val coordString = "${this.blockX} ${this.blockY} ${this.blockZ}"

        return coordString.asFancyString().hoverEvent(HoverEvent.showText(Component.text("Click to Copy!").color(Defaults.YELLOW))).clickEvent(ClickEvent.copyToClipboard("/tp @s $coordString")).append(" @ ${this.world.name}".asFancyString())
    }

    @JvmStatic
    fun Location.toVanillalikeComponent(): Component { // Probably needs refactoring
        val dim = when (this.world?.environment) {
            World.Environment.NORMAL -> "overworld"
            World.Environment.NETHER -> "the_nether"
            World.Environment.THE_END -> "the_end"
            else -> ""
        }

        val coords = "${this.blockX} ${this.blockY} ${this.blockZ}"
        return coords.asFancyString().hoverEvent(HoverEvent.showText(Component.text("Click to suggest command!").color(Defaults.YELLOW))).clickEvent(ClickEvent.suggestCommand("/execute ${if (dim.isNotEmpty()) "in minecraft:$dim" else ""} run tp @s $coords")).append(" @ ${this.world.name}".asFancyString())
    }

    @JvmStatic
    fun String.stripArgument(argument: String): Pair<String, Boolean> {
        val regex = Regex("(?<=^|\\s)-$argument(?=\\s|$)")

        val removed = regex.containsMatchIn(this)
        val modified = if (removed) regex.replace(this, "").replace(Regex("\\s+"), " ").trim() else this

        return modified to removed
    }

    // Doubles Extensions!
    @JvmStatic
    @JvmOverloads
    fun Double.boundedScale(max: Double = 10.0): Double = (this - 1.0).coerceIn(-0.9, max)

    @JvmStatic
    fun Double.fancy(): String {
        val symbols = DecimalFormatSymbols().apply {
            groupingSeparator = '.'
            decimalSeparator = ','
        }

        val format = DecimalFormat("#,##0.##", symbols)

        format.isGroupingUsed = true
        return format.format(this)
    }

    // Player Extensions!
    @JvmStatic
    fun Player.getSpawn(): Location {
        val loc = this.respawnLocation
        return loc ?: Bukkit.getWorlds()[0].spawnLocation
    }

    @JvmStatic
    @JvmOverloads
    fun Player.mark(style: Style = Style.empty(), entityHover: Boolean = true, head: Boolean = false): Component {
        var end = Component.empty()
        var body = displayName().style(style)

        if (entityHover) body = body.hoverEvent(HoverEvent.showEntity(HoverEvent.ShowEntity.showEntity(Key.key(Key.MINECRAFT_NAMESPACE, "player"), this.uniqueId, this.name())))
        if (head) end = end.append(Component.`object`(ObjectContents.playerHead(this.playerProfile)).appendSpace())
        end = end.append(body)

        return end
    }

    @JvmStatic
    @JvmOverloads
    fun Collection<Player>.mark(style: Style = Style.empty(), heads: Boolean = false): Component {
        if (this.size == 1) return this.first().mark(style)

        return Component.text("${this.size} players").style(style).hoverEvent(HoverEvent.showText(Component.join(JoinConfiguration.newlines(), this.stream().map { it.mark(style, false, heads) }.toList())))
    }

    @JvmStatic
    fun Player.getProtocol(): Int {
        return try {
            Via.getAPI().getPlayerVersion(this)
        } catch (_: Exception) {
            player!!.protocolVersion // wow.
        }
    }

    @JvmStatic
    fun Player.addScalarTransientMod(id: String, attribute: Attribute, value: Double) {
        val key = NamespacedKey(Lukitils.getInstance(), id)
        val instance = this.getAttribute(attribute) ?: return

        instance.removeModifier(key)
        if (value != 0.0) instance.addTransientModifier(AttributeModifier(key, value, AttributeModifier.Operation.MULTIPLY_SCALAR_1))
    }

    @JvmStatic
    fun Player.removeTransientMod(id: String, attribute: Attribute) = this.getAttribute(attribute)?.removeModifier(NamespacedKey(Lukitils.getInstance(), id))

    // Command Extensions!
    @JvmStatic
    fun CommandContext<CommandSourceStack>.getPlayerOrThrow(arg: String): Player = getArgument(arg, PlayerSelectorArgumentResolver::class.java).resolve(source).stream().findFirst().orElse(null) ?: throw Defaults.NOT_FOUND

    @JvmStatic
    fun CommandContext<CommandSourceStack>.getPlayersOrThrow(arg: String): List<Player> = getArgument(arg, PlayerSelectorArgumentResolver::class.java).resolve(source).stream().toList().takeIf { it.isNotEmpty() } ?: throw Defaults.NOT_FOUND

    // Misc Extensions!
    @JvmStatic
    fun isFolia(): Boolean = try {
        Class.forName("io.papermc.paper.threadedregions.RegionizedServer")
        true
    } catch (_: ClassNotFoundException) {
        false
    }
}