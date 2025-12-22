package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.body.DialogBody
import io.papermc.paper.registry.data.dialog.type.DialogType
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Utils.asFancyString
import me.lukiiy.utils.help.Utils.asPermission
import me.lukiiy.utils.help.Utils.getProtocol
import me.lukiiy.utils.help.Utils.getSpawn
import me.lukiiy.utils.help.Utils.toVanillalikeComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.`object`.ObjectContents
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.Statistic
import org.bukkit.attribute.Attribute
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object PlayerData {
    private val main = Commands.literal("playerdata")
        .requires { it.sender.hasPermission("playerdata".asPermission()) }
        .then(Commands.argument("player", StringArgumentType.string())
            .suggests { _, builder ->
                val input = builder.remaining.lowercase()

                Bukkit.getOnlinePlayers().stream().map { it.name }.filter { it.startsWith(input) }.forEach { builder.suggest(it) }
                builder.buildFuture()
            }
            .executes {
                val sender = it.source.sender
                val targetInput = StringArgumentType.getString(it, "player")
                val player = Bukkit.getPlayer(targetInput) ?: Bukkit.getOfflinePlayerIfCached(targetInput) ?: throw Defaults.NOT_FOUND

                handle(sender, player)
                Command.SINGLE_SUCCESS
            }
        )
        .executes {
            val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER

            handle(sender, sender)
            Command.SINGLE_SUCCESS
        }

    fun register(): LiteralCommandNode<CommandSourceStack> = main.build()

    private fun handle(sender: CommandSender, target: OfflinePlayer) {
        if (!target.hasPlayedBefore()) throw Defaults.CmdException(Component.text("This player has never played here before"))

        val name = target.player?.name() ?: target.name?.asFancyString() ?: "Player".asFancyString()
        val head = if (target.player != null) Component.`object`(ObjectContents.playerHead(target.player!!.playerProfile)).appendSpace() else Component.empty()
        val header = head.append(name.color(Defaults.YELLOW).append(Component.text("'s Info").color(Defaults.GRAY)))

        val baseBuild = DialogBase.builder(header).canCloseWithEscape(true).afterAction(DialogBase.DialogAfterAction.CLOSE)

        val offData = listOfNotNull(
            fancyData("First login", target.firstPlayed.formatDate()),
            fancyData("Last login", target.lastLogin.formatDate()),
            fancyData("Last seen", target.lastSeen.formatDate()),
            fancyData("Playtime", "${target.getStatistic(Statistic.PLAY_ONE_MINUTE) / 72000} hours")
        )

        val player = target.player!!

        val basic = listOfNotNull(
            player.getAttribute(Attribute.MAX_HEALTH)?.value?.let { fancyData("Health", "${String.format("%.1f", player.health)}/$it") },
            fancyData("Food", player.foodLevel),
            if (player.totalExperience > 0) { fancyData("XP", player.totalExperience).append(fancyData(" | Level", player.level)) } else null,
            fancyData("Total Experience", player.totalExperience),
            player.getAttribute(Attribute.ARMOR)?.value?.let { if (it > 0) fancyData("Armor", it) else null },
            fancyData("Gamemode", player.gameMode.name)
        )

        val flags = listOfNotNull(
            if (player.isFlying) "Fly" else null,
            if (player.isInvulnerable) "Invulnerability" else null,
            if (Vanish.getVanished().contains(target.uniqueId)) "Vanish" else null
        )

        val locations = listOfNotNull(
            Component.text("Current: ").append(player.location.toVanillalikeComponent().color(Defaults.ORANGE)),
            Component.text("Spawn: ").append(player.getSpawn().toVanillalikeComponent().color(Defaults.ORANGE)),
            if (player.compassTarget != player.getSpawn()) { Component.text("Compass: ").append(player.compassTarget.toVanillalikeComponent().color(Defaults.ORANGE)) } else null,
            player.lastDeathLocation?.let { Component.text("Last death: ").append(it.toVanillalikeComponent().color(Defaults.ORANGE)) }
        )

        if (sender is Player && sender.getProtocol() > 770) {
            baseBuild.body(mutableListOf<DialogBody>().apply {
                if (player.isOnline) {
                    add(DialogBody.item(ItemStack.of(Material.APPLE)).showTooltip(false).build())
                    add(category(basic))

                    if (flags.isNotEmpty()) {
                        add(DialogBody.plainMessage(Component.empty()))
                        add(DialogBody.item(ItemStack.of(Material.BOOK)).showTooltip(false).build())
                        add(DialogBody.plainMessage(Defaults.LIST_PREFIX.append(fancyData("Enabled flags", flags.joinToString(", ")))))
                    }
                }

                add(DialogBody.item(ItemStack.of(Material.COMPASS)).showTooltip(false).build())
                add(category(locations))

                add(DialogBody.item(ItemStack.of(Material.BOOK)).showTooltip(false).build())
                add(category(offData))
            })

            sender.showDialog(Dialog.create { it.empty().apply {
                base(baseBuild.build())
                type(DialogType.notice())
            } })
        } else {
            val everything = header.appendNewline().appendNewline()
                .append(Component.join(Defaults.LIST_LIKE, basic)).appendNewline().appendNewline()
                .let {
                    if (flags.isNotEmpty()) it.append(Defaults.LIST_PREFIX).append(fancyData("Enabled flags", flags.joinToString(", "))).appendNewline().appendNewline() else it
                }
                .append(Component.join(Defaults.LIST_LIKE, locations)).appendNewline()

            sender.sendMessage(everything)
        }
    }

    private fun category(lines: List<Component>): DialogBody = DialogBody.plainMessage(Component.join(JoinConfiguration.newlines(), lines))

    private fun fancyData(title: String, value: Any): TextComponent = Component.text("$title: ").append(Component.text(value.toString()).color(Defaults.ORANGE))

    private fun Long.formatDate(): String {
        if (this == 0L) return ""

        val inputDate = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
        val now = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern(if (inputDate.year == now.year) "MM/dd" else "MM/dd/yyyy")
        val daysAgo = ChronoUnit.DAYS.between(inputDate, now)

        return "${inputDate.format(formatter)} ${if (daysAgo > 0) "($daysAgo days ago)" else ""}"
    }
}