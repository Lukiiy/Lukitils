package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.ComponentUtils
import me.lukiiy.utils.help.PlayerUtils
import net.kyori.adventure.text.Component
import org.bukkit.attribute.Attribute
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.text.DecimalFormat

object PlayerData {
    fun register(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("playerdata")
            .then(Commands.argument("player", ArgumentTypes.player())
                .executes {
                    val sender = it.source.sender
                    val target = it.getArgument("player", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().findFirst().orElse(null) ?: return@executes 0

                    handle(sender, target)
                    Command.SINGLE_SUCCESS
                })
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER.create()

                handle(sender, sender)
                Command.SINGLE_SUCCESS
            }
        .build()
    }

    private fun handle(sender: CommandSender, target: Player) {
        val basic = listOfNotNull(
            target.getAttribute(Attribute.MAX_HEALTH)?.value?.let { Component.text("Health: ").append(Component.text("${DecimalFormat("#.#").format(target.health)}/$it").color(Defaults.ORANGE)) },
            Component.text("Food: ").append(Component.text(target.foodLevel).color(Defaults.ORANGE)),
            if (target.totalExperience > 0) Component.text("XP: ").append(Component.text(target.totalExperience).color(Defaults.ORANGE)).append(Component.text(" | Level: ").append(Component.text(target.level).color(Defaults.ORANGE))) else null,
            target.getAttribute(Attribute.ARMOR)?.value?.let { if (it > 0) Component.text("Armor: ").append(Component.text(it).color(Defaults.ORANGE)) else null },
            Component.text("Gamemode: ").append(Component.text(target.gameMode.name).color(Defaults.ORANGE))
        )

        val states = listOfNotNull(
            if (target.isFlying) Component.text("Fly: ").append(Defaults.ON) else null,
            if (target.isInvulnerable) Component.text("Invulnerability: ").append(Defaults.ON) else null,
            if (Vanish.getVanished().contains(target.uniqueId)) Component.text("Vanish: ").append(Defaults.ON) else null
        )

        val locations = listOfNotNull(
            Component.text("Current: ").append(ComponentUtils.coolLocation(target.location).color(Defaults.ORANGE)),
            Component.text("Spawn: ").append(ComponentUtils.coolLocation(PlayerUtils.getSpawn(target)).color(Defaults.ORANGE)),
            if (target.compassTarget != PlayerUtils.getSpawn(target)) Component.text("Compass: ").append(ComponentUtils.coolLocation(target.compassTarget).color(Defaults.ORANGE)) else null,
            target.lastDeathLocation?.let { Component.text("Last death: ").append(ComponentUtils.coolLocation(it).color(Defaults.ORANGE)) }
        )

        val everything = target.name().color(Defaults.YELLOW)
            .append(Component.text("'s Info").color(Defaults.GRAY)).appendNewline().appendNewline()
            .append(Component.join(Defaults.LIST_LIKE, basic)).appendNewline().appendNewline()
            .let { if (states.isNotEmpty()) { it.append(Component.join(Defaults.LIST_LIKE, states)).appendNewline().appendNewline() } else it }
            .append(Component.join(Defaults.LIST_LIKE, locations)).appendNewline()

        sender.sendMessage(Defaults.msg(Component.text("Showing ").append(everything)))
    }
}