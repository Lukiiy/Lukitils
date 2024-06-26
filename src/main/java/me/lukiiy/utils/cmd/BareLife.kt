package me.lukiiy.utils.cmd

import me.lukiiy.utils.cool.Presets
import me.lukiiy.utils.main
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BareLife : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        if (commandSender !is Player && strings.isEmpty()) {
            commandSender.sendMessage(main.argsErrorMsg)
            return true
        }
        val target = if (strings.isNotEmpty()) Bukkit.getPlayer(strings[0]) else commandSender as Player
        if (target == null) {
            commandSender.sendMessage(main.notFoundMsg)
            return true
        }

        target.apply {
            health = 1.0
            foodLevel = 1
            sendHealthUpdate()
        }
        commandSender.sendMessage(Presets.msg(Component.text("Set ").append(target.name().color(Presets.ACCENT_NEUTRAL)).append(Component.text("'s HP and Hunger to 1"))))
        return true
    }
}
