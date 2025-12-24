package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Utils.asFancyString
import me.lukiiy.utils.help.Utils.getPlayerOrThrow
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Ping {
    private val main = Commands.literal("ping")
        .then(Commands.argument("player", ArgumentTypes.player())
            .executes {
                val target = it.getPlayerOrThrow("player")

                handle(it.source.sender, target)
                Command.SINGLE_SUCCESS
            })
        .executes {
            val target = it.source.sender as? Player ?: throw Defaults.NON_PLAYER

            handle(target, target)
            Command.SINGLE_SUCCESS
        }

    fun register(): LiteralCommandNode<CommandSourceStack> = main.build()

    fun handle(sender: CommandSender, player: Player) {
        val pre = if (sender == player) "Your".asFancyString().color(Defaults.YELLOW) else player.name().color(Defaults.YELLOW).append("'s".asFancyString())

        sender.sendMessage(Defaults.neutral(pre.append(" ping is ".asFancyString()).append("${player.ping}".asFancyString().color(Defaults.PURPLE).decorate(TextDecoration.UNDERLINED)).append(" ms".asFancyString())))
    }
}