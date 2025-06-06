package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Utils.asPermission
import me.lukiiy.utils.help.Utils.getPlayerOrThrow
import me.lukiiy.utils.help.Utils.getSpawn
import me.lukiiy.utils.help.Utils.toComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Statistic
import org.bukkit.attribute.Attribute
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object PlayerData {
    private val req: (CommandSender) -> Boolean = { it.hasPermission("playerdata".asPermission()) }

    fun registerOnline(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("playerdata")
            .requires { req(it.sender) }
            .then(Commands.argument("player", ArgumentTypes.player())
                .executes {
                    val sender = it.source.sender
                    val target = it.getPlayerOrThrow("player")

                    handle(sender, target)
                    Command.SINGLE_SUCCESS
                })
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER

                handle(sender, sender)
                Command.SINGLE_SUCCESS
            }
        .build()
    }

    fun registerOffline(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("offplayerdata")
            .requires { req(it.sender) }
            .then(Commands.argument("offline_player", StringArgumentType.string())
                .executes {
                    val sender = it.source.sender
                    val target = Bukkit.getOfflinePlayerIfCached(StringArgumentType.getString(it, "offline_player")) ?: throw Defaults.NOT_FOUND

                    handle(sender, target)
                    Command.SINGLE_SUCCESS
                })
            .build()
    }

    private fun fancyData(title: String, value: Any): TextComponent = Component.text("$title: ").append(Component.text(value.toString()).color(Defaults.ORANGE))

    private fun handle(sender: CommandSender, target: Player) {
        val basic = listOfNotNull(
            target.getAttribute(Attribute.MAX_HEALTH)?.value?.let { fancyData("Health", "${String.format("%.1f", target.health)}/$it") },
            fancyData("Food", target.foodLevel),
            if (target.totalExperience > 0) fancyData("XP", target.totalExperience).append(fancyData(" | Level", target.level)) else null,
            target.getAttribute(Attribute.ARMOR)?.value?.let { if (it > 0) fancyData("Armor", it) else null },
            fancyData("Gamemode", target.gameMode.name)
        )

        val flags = listOfNotNull(
            if (target.isFlying) "Fly" else null,
            if (target.isInvulnerable) "Invulnerability" else null,
            if (Vanish.getVanished().contains(target.uniqueId)) "Vanish" else null
        )

        val locations = listOfNotNull(
            Component.text("Current: ").append(target.location.toComponent().color(Defaults.ORANGE)),
            Component.text("Spawn: ").append(target.getSpawn().toComponent().color(Defaults.ORANGE)),
            if (target.compassTarget != target.getSpawn()) Component.text("Compass: ").append(target.compassTarget.toComponent().color(Defaults.ORANGE)) else null,
            target.lastDeathLocation?.let { Component.text("Last death: ").append(it.toComponent().color(Defaults.ORANGE)) }
        )

        val everything = target.name().color(Defaults.YELLOW)
            .append(Component.text("'s Info").color(Defaults.GRAY)).appendNewline().appendNewline()
            .append(Component.join(Defaults.LIST_LIKE, basic)).appendNewline().appendNewline()
            .let { if (flags.isNotEmpty()) { it.append(Defaults.LIST_PREFIX).append(fancyData("Enabled flags", flags.joinToString(", "))).appendNewline().appendNewline() } else it }
            .append(Component.join(Defaults.LIST_LIKE, locations)).appendNewline()

        sender.sendMessage(Defaults.neutral(Component.text("Showing ").append(everything)))
    }

    fun formatDateData(millis: Long): String {
        if (millis == 0L) return ""
        val inputDate = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
        val now = LocalDate.now()

        val formatter = DateTimeFormatter.ofPattern(if (inputDate.year == now.year) "MM/dd" else "MM/dd/yyyy")
        val daysAgo = ChronoUnit.DAYS.between(inputDate, now)

        return "${inputDate.format(formatter)} ${if (daysAgo > 0) "($daysAgo days ago)" else ""}"
    }

    private fun handle(sender: CommandSender, target: OfflinePlayer) {
        if (target.isOnline) throw Defaults.CmdException(Component.text("This user is online"))
        if (!target.hasPlayedBefore()) throw Defaults.CmdException(Component.text("This player has never played here before"))

        val time = listOfNotNull(
            fancyData("First login", formatDateData(target.firstPlayed)),
            fancyData("Last login", formatDateData(target.lastLogin)),
            fancyData("Last seen", formatDateData(target.lastSeen)),
            fancyData("Playtime", "${target.getStatistic(Statistic.PLAY_ONE_MINUTE) / 72000} hours")
        )

        val everything = Component.text(target.name.toString()).color(Defaults.YELLOW)
            .append(Component.text("'s Offline Info").color(Defaults.GRAY)).appendNewline().appendNewline()
            .append(Component.join(Defaults.LIST_LIKE, time)).appendNewline()

        sender.sendMessage(Defaults.neutral(Component.text("Showing ").append(everything)))
    }
}