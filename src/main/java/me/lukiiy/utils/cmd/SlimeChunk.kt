package me.lukiiy.utils.cmd

import me.lukiiy.utils.cool.Presets
import me.lukiiy.utils.main
import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SlimeChunk : CommandExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, strings: Array<String>): Boolean {
        if (commandSender !is Player) {
            commandSender.sendMessage(main.nonPlayerMsg)
            return true
        }

        val message = Presets.msg("You ")
            .append(if (commandSender.location.chunk.isSlimeChunk) Component.text("ᴀʀᴇ").color(Presets.ACCENT_TRUE) else Component.text("ᴀʀᴇɴ'ᴛ").color(Presets.ACCENT_FALSE))
            .append(Presets.why(" in a slime chunk."))
        commandSender.sendMessage(message)
        return true
    }
}
