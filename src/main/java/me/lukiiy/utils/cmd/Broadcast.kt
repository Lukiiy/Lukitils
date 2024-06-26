package me.lukiiy.utils.cmd

import me.lukiiy.utils.main
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Broadcast : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        if (strings.isEmpty()) {
            commandSender.sendMessage(main.argsErrorMsg)
            return true
        }
        Bukkit.broadcast(LegacyComponentSerializer.legacyAmpersand().deserialize(strings.joinToString(" ")))
        return true
    }
}
