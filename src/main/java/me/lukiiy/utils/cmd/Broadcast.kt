package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import me.lukiiy.utils.Defaults
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

object Broadcast {
    fun register(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("broadcast").requires {it.sender.hasPermission("lukitils.broadcast")}
            .then(Commands.argument("msg", StringArgumentType.greedyString())
            .executes {
                Bukkit.broadcast(Component.newline().append(Defaults.mini("   ${StringArgumentType.getString(it, "msg")}").color(Defaults.YELLOW)).appendNewline())
                Command.SINGLE_SUCCESS
            })
        .build()
    }
}
