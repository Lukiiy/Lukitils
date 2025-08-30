package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Utils.asFancyString
import me.lukiiy.utils.help.Utils.asPermission
import me.lukiiy.utils.help.Utils.group
import net.kyori.adventure.text.format.Style

object Ignite {
    private val main = Commands.literal("ignite")
        .requires { it.sender.hasPermission("ignite".asPermission()) }
        .then(Commands.argument("players", ArgumentTypes.players())
            .then(Commands.argument("seconds", IntegerArgumentType.integer(1))
                .executes {
                    val sender = it.source.sender
                    val targets = it.getArgument("players", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().toList().takeIf { l -> l.isNotEmpty() } ?: throw Defaults.NOT_FOUND
                    val seconds = IntegerArgumentType.getInteger(it, "seconds")

                    targets.forEach { p -> p.fireTicks = seconds * 20 }
                    sender.sendMessage(Defaults.neutral("Set ".asFancyString().append(targets.group(Style.style(Defaults.YELLOW))).append(" on fire for ".asFancyString().append("$seconds".asFancyString().color(Defaults.YELLOW)).append(" seconds".asFancyString()))))
                    Command.SINGLE_SUCCESS
                }))

    fun register(): LiteralCommandNode<CommandSourceStack> = main.build()
}