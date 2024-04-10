package me.lukiiy.utils.cmd

import me.lukiiy.utils.cool.Presets
import me.lukiiy.utils.main
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class BareLife : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        if (strings.isEmpty()) {
            commandSender.sendMessage(main.argsErrorMsg)
            return true
        }
        val target = Bukkit.getPlayer(strings[0])
        if (target == null) {
            commandSender.sendMessage(main.notFoundMsg)
            return true
        }
        target.health = 1.0
        target.foodLevel = 1
        target.sendHealthUpdate()
        commandSender.sendMessage(
            Presets.msg("Set ").append(target.name().color(Presets.ACCENT_NEUTRAL))
                .append(Presets.why(" HP and Hunger to 0"))
        )
        return true
    }
}
