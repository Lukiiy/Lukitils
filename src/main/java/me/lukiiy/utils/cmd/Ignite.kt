package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.PlayerUtils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style

object Ignite {
    fun register(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("ignite")
            .requires { it.sender.hasPermission("lukitils.ignite") }
            .then(Commands.argument("players", ArgumentTypes.players())
            .then(Commands.argument("seconds", IntegerArgumentType.integer(1))
                .executes {
                    val sender = it.source.sender
                    val targets = it.getArgument("players", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().toList().takeIf {it.isNotEmpty()} ?: throw Defaults.NOT_FOUND
                    val duration = IntegerArgumentType.getInteger(it, "seconds")

                    targets.forEach { p -> p.fireTicks = duration * 20 }
                    sender.sendMessage(Defaults.msg(Component.text("Set ").append(PlayerUtils.group(targets, Style.style(Defaults.YELLOW))).append(Component.text(" on fire for ").append(Component.text(duration).color(Defaults.YELLOW)).append(Component.text(" seconds.")))))
                    Command.SINGLE_SUCCESS
                }))
        .build()
    }
}