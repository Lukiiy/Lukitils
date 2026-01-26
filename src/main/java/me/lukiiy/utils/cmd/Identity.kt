package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import io.papermc.paper.command.brigadier.Commands
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Utils
import me.lukiiy.utils.help.Utils.asFancyString
import me.lukiiy.utils.help.Utils.asPermission
import me.lukiiy.utils.help.Utils.resetNametag
import me.lukiiy.utils.help.Utils.resetTextures
import me.lukiiy.utils.help.Utils.setNametag
import me.lukiiy.utils.help.Utils.setTextures
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.`object`.ObjectContents
import org.bukkit.entity.Player
import java.util.UUID

object Identity {
    private val main = Commands.literal("identity")
        .requires { it.sender.hasPermission("identity".asPermission()) }
        .then(Commands.literal("nametag")
            .then(Commands.literal("#").executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER

                sender.resetNametag()
                sender.sendMessage(Defaults.neutral("Your nametag has been reset!".asFancyString()))

                return@executes Command.SINGLE_SUCCESS

            })
            .then(Commands.argument("new_username", StringArgumentType.word()).executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER
                val new = StringArgumentType.getString(it, "new_username")

                if (!new.matches(Utils.USERNAME_REGEX)) throw Defaults.CmdException("Invalid username! Must only include alphanumeric characters, underscores, and must be within 16 characters.".asFancyString())

                sender.setNametag(new)
                sender.sendMessage(Defaults.neutral("Your nametag is now ".asFancyString().append(new.asFancyString().color(Defaults.YELLOW)).append("!".asFancyString())))

                return@executes Command.SINGLE_SUCCESS
            })
        )
        .then(Commands.literal("skin")
            .then(Commands.literal("#").executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER

                sender.resetTextures()
                sender.sendMessage(Defaults.neutral("Your skin has been reset! ".asFancyString().append(Component.`object`(ObjectContents.playerHead(sender.playerProfile)))))

                return@executes Command.SINGLE_SUCCESS
            })
            .then(Commands.argument("new_skin", StringArgumentType.string()).executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER
                val new = StringArgumentType.getString(it, "new_skin")

                when {
                    new.matches(Utils.USERNAME_REGEX) -> {
                        sender.setTextures(new)
                        sender.sendMessage(Defaults.neutral("Your skin is now ".asFancyString().append(new.asFancyString().color(Defaults.YELLOW)).append("'s!".asFancyString())))
                    }
                    runCatching { UUID.fromString(new) }.isSuccess -> {
                        sender.setTextures(UUID.fromString(new))
                        sender.sendMessage(Defaults.neutral("Your skin has been updated!".asFancyString()))
                    }
                    else -> {
                        sender.setTextures(new)
                        sender.sendMessage(Defaults.neutral("Your skin has been updated with the provided texture!".asFancyString()))
                    }
                }

                return@executes Command.SINGLE_SUCCESS
            })
            .then(Commands.argument("texture", StringArgumentType.string())
                .then(Commands.argument("signature", StringArgumentType.greedyString()).executes {
                    val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER
                    val texture = StringArgumentType.getString(it, "texture")
                    val signature = StringArgumentType.getString(it, "signature")

                    sender.setTextures(texture, signature)
                    sender.sendMessage(Defaults.neutral("Your skin has been updated!".asFancyString()))

                    return@executes Command.SINGLE_SUCCESS
                })
            )
        )
}