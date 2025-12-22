package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
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
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Barelife {
    private val main = Commands.literal("barelife")
        .requires { it.sender.hasPermission("barelife".asPermission()) }
        .then(Commands.argument("players", ArgumentTypes.players())
            .executes {
                val targets = it.getPlayersOrThrow("players")

                handle(it.source.sender, targets)
                Command.SINGLE_SUCCESS
            })
        .executes {
            val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER

            handle(sender, listOf(sender))
            Command.SINGLE_SUCCESS
        }

    fun handle(sender: CommandSender, players: List<Player>) {
        players.forEach {
            it.health = 1.0
            it.foodLevel = 1
            it.saturation = 0f
        }

        val mark = players.mark(Style.style(Defaults.YELLOW))

        sender.sendMessage(Defaults.neutral("Barelife'd ".asFancyString().append(mark)))
        Utils.adminCmdFeedback(sender, "Barelife'd ${mark.asPlainString()}")
    }

    fun register(): LiteralCommandNode<CommandSourceStack> = main.build()
}