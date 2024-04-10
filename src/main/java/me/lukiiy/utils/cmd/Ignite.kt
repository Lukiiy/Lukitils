package me.lukiiy.utils.cmd

import me.lukiiy.utils.cool.Presets
import me.lukiiy.utils.main
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class Ignite : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        if (strings.size < 2) {
            commandSender.sendMessage(main.argsErrorMsg)
            return true
        }
        val target = Bukkit.getPlayer(strings[0])
        if (target == null) {
            commandSender.sendMessage(main.notFoundMsg)
            return true
        }
        var duration = 1
        duration = try {
            strings[1].toInt()
        } catch (e: NumberFormatException) {
            commandSender.sendMessage(Presets.warnMsg("Invalid number!"))
            return true
        }
        if (duration <= 0) {
            commandSender.sendMessage(Presets.warnMsg("Must be a positive value greater than 0."))
            return true
        }
        target.fireTicks = duration * 20
        target.sendMessage(Presets.msg("You set ").append(target.name().color(Presets.ACCENT_NEUTRAL)).append(Presets.why(" on fire for $duration seconds!")))
        return true
    }
}
