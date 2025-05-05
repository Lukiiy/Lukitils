package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Utils
import me.lukiiy.utils.help.Utils.asFancyString
import me.lukiiy.utils.help.Utils.asPermission
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

object Broadcast {
    private val main = Commands.literal("broadcast")
        .requires { it.sender.hasPermission("broadcast".asPermission()) }
        .then(Commands.argument("msg", StringArgumentType.greedyString())
            .executes {
                Utils.adminCmdFeedback(it.source.sender, "Broadcasted a message")
                Bukkit.broadcast(Component.newline().append("   ${StringArgumentType.getString(it, "msg")}".asFancyString()).color(Defaults.BLUE).appendNewline())

                Command.SINGLE_SUCCESS
            })

    fun register(): LiteralCommandNode<CommandSourceStack> = main.build()
}
