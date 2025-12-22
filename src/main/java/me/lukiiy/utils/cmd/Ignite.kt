package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Utils
import me.lukiiy.utils.help.Utils.asFancyString
import me.lukiiy.utils.help.Utils.asPermission
import me.lukiiy.utils.help.Utils.asPlainString
import me.lukiiy.utils.help.Utils.getPlayersOrThrow
import me.lukiiy.utils.help.Utils.mark
import net.kyori.adventure.text.format.Style

object Ignite {
    private val main = Commands.literal("ignite")
        .requires { it.sender.hasPermission("ignite".asPermission()) }
        .then(Commands.argument("players", ArgumentTypes.players())
            .then(Commands.argument("seconds", IntegerArgumentType.integer(1))
                .executes {
                    val sender = it.source.sender
                    val targets = it.getPlayersOrThrow("players")
                    val seconds = IntegerArgumentType.getInteger(it, "seconds")
                    val mark = targets.mark(Style.style(Defaults.ORANGE)) // on fire

                    targets.forEach { p -> p.fireTicks = seconds * 20 }

                    sender.sendMessage(Defaults.neutral("Set ".asFancyString().append(mark).append(" on fire for ".asFancyString().append("$seconds".asFancyString().color(Defaults.YELLOW)).append(" seconds".asFancyString()))))
                    Utils.adminCmdFeedback(sender, "Set ${mark.asPlainString()} on fire for $seconds seconds")
                    Command.SINGLE_SUCCESS
                }))

    fun register(): LiteralCommandNode<CommandSourceStack> = main.build()
}