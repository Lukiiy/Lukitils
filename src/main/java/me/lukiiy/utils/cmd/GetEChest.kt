package me.lukiiy.utils.cmd

import me.lukiiy.utils.cool.PlayerHelper
import me.lukiiy.utils.cool.Presets
import me.lukiiy.utils.main
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class GetEChest : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        if (commandSender !is Player) {
            commandSender.sendMessage(main.nonPlayerMsg)
            return true
        }
        val target = PlayerHelper.getCommandTarget(commandSender, strings)
        commandSender.sendMessage(
            Presets.msg("Showing ").append(target.displayName().color(Presets.ACCENT_NEUTRAL))
                .append(Presets.why("'s Ender Chest."))
        )
        commandSender.openInventory(target.enderChest)
        return true
    }
}
