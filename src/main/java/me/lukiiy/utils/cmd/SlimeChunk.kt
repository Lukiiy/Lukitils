package me.lukiiy.utils.cmd

import me.lukiiy.utils.cool.PlayerHelper
import me.lukiiy.utils.cool.Presets.Companion.ACCENT_FALSE
import me.lukiiy.utils.cool.Presets.Companion.ACCENT_NEUTRAL
import me.lukiiy.utils.cool.Presets.Companion.ACCENT_TRUE
import me.lukiiy.utils.cool.Presets.Companion.msg
import me.lukiiy.utils.cool.Presets.Companion.why
import me.lukiiy.utils.main
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SlimeChunk : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        val target: Player? = if (commandSender !is Player) {
            if (strings.isEmpty()) {
                commandSender.sendMessage(main.argsErrorMsg)
                return true
            }
            Bukkit.getPlayer(strings[0])
        } else PlayerHelper.getCommandTarget(commandSender, strings)
        if (target == null) {
            commandSender.sendMessage(main.notFoundMsg)
            return true
        }
        val message = msg("Is ")
            .append(target.name().color(ACCENT_NEUTRAL))
            .append(why(" in a slime chunk? "))
            .append(if (target.location.chunk.isSlimeChunk) Component.text("ʏᴇѕ").color(ACCENT_TRUE) else Component.text("ɴᴏ").color(ACCENT_FALSE))
        commandSender.sendMessage(message)
        return true
    }
}
