package me.lukiiy.utils.cmd

import me.lukiiy.utils.cool.Presets.Companion.PRIMARY
import me.lukiiy.utils.cool.Presets.Companion.msg
import me.lukiiy.utils.system.Vanish
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class VanishList : CommandExecutor {
    private val prefix: Component = Component.text("• ").color(PRIMARY)
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        val vanish = Vanish.vanish
        if (vanish.isEmpty()) {
            commandSender.sendMessage(msg("There are no vanished players."))
            return true
        }
        commandSender.sendMessage(msg("Vanished player list:"))
        for (vanished in vanish) {
            val player = Bukkit.getPlayer(vanished!!)
            if (player != null) commandSender.sendMessage(prefix.append(player.displayName()))
        }
        return true
    }
}
