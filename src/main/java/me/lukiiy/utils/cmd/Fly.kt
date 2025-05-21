package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Utils
import me.lukiiy.utils.help.Utils.asPermission
import me.lukiiy.utils.help.Utils.getPlayerOrThrow
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Fly {
    private val main = Commands.literal("fly")
        .requires { it.sender.hasPermission("fly".asPermission()) }
        .then(Commands.argument("player", ArgumentTypes.player())
            .executes {
                val target = it.getPlayerOrThrow("player")

                handle(it.source.sender, target)
                Command.SINGLE_SUCCESS
            })
        .executes {
            val sender = it.source.sender as? Player ?: throw Defaults.NOT_FOUND

            handle(sender, sender)
            Command.SINGLE_SUCCESS
        }

    fun register(): LiteralCommandNode<CommandSourceStack> = main.build()

    private fun handle(sender: CommandSender, target: Player) {
        val update = !target.allowFlight
        val msg = Defaults.neutral(Component.text("Flight is now ")).append(if (update) Defaults.ON else Defaults.OFF)
        var adminMsgExtra = ""

        target.allowFlight = update

        if (target != sender) {
            sender.sendMessage(msg.append(Component.text(" for ").append(target.name().color(Defaults.YELLOW))).color(Defaults.GRAY))
            target.sendMessage(msg.append(Component.text(" (by ").append(sender.name().color(Defaults.YELLOW)).append(Component.text(")"))).color(Defaults.GRAY))
            adminMsgExtra = "for ${target.name}"
        } else target.sendMessage(msg)

        Utils.adminCmdFeedback(sender, "${if (update) "Enabled" else "Disabled"} flight $adminMsgExtra")
    }
}