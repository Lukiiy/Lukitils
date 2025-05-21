package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Utils.asPermission
import me.lukiiy.utils.help.Utils.group
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style

object Ignite {
    private val main = Commands.literal("ignite")
        .requires { it.sender.hasPermission("ignite".asPermission()) }
        .then(Commands.argument("players", ArgumentTypes.players())
            .then(Commands.argument("time", ArgumentTypes.time(1))
                .executes {
                    val sender = it.source.sender
                    val targets = it.getArgument("players", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().toList().takeIf { l -> l.isNotEmpty() } ?: throw Defaults.NOT_FOUND
                    val durationTicks = IntegerArgumentType.getInteger(it, "time")

                    targets.forEach { p -> p.fireTicks = durationTicks }
                    sender.sendMessage(Defaults.neutral(Component.text("Set ").append(targets.group(Style.style(Defaults.YELLOW))).append(Component.text(" on fire for ").append(Component.text(durationTicks * 20).color(Defaults.YELLOW)).append(Component.text(" seconds.")))))
                    Command.SINGLE_SUCCESS
                }))

    fun register(): LiteralCommandNode<CommandSourceStack> = main.build()
}