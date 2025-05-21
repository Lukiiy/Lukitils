package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.Lukitils
import me.lukiiy.utils.help.Utils.asFancyString
import me.lukiiy.utils.help.Utils.asPermission

object Reload {
    private val main = Commands.literal("lukitils")
        .requires { it.sender.hasPermission("reload".asPermission()) }
        .executes {
            it.source.sender.sendMessage(Defaults.neutral("Reload config...".asFancyString()))
            Lukitils.getInstance().reloadConfig()
            Command.SINGLE_SUCCESS
        }

    fun register(): LiteralCommandNode<CommandSourceStack> = main.build()
}