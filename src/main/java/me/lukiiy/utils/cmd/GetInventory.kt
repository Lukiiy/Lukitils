package me.lukiiy.utils.cmd

import me.lukiiy.utils.cool.PlayerHelper
import me.lukiiy.utils.cool.Presets
import me.lukiiy.utils.main
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GetInventory : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        if (commandSender !is Player) {
            commandSender.sendMessage(main.nonPlayerMsg)
            return true
        }
        if (strings.isEmpty()) {
            commandSender.sendMessage(main.argsErrorMsg)
            return true
        }
        val target = Bukkit.getPlayer(strings[0]) ?: run {
            commandSender.sendMessage(main.notFoundMsg)
            return true
        }
        commandSender.openInventory(target.inventory)
        commandSender.sendMessage(Presets.msg(Component.text("Showing ").append(target.name().color(Presets.ACCENT_NEUTRAL)).append(Component.text("'s Inventory"))))
        return true
    }
}
