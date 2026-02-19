package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.Lukitils
import me.lukiiy.utils.help.Utils
import me.lukiiy.utils.help.Utils.asFancyString
import me.lukiiy.utils.help.Utils.asPermission
import me.lukiiy.utils.help.Utils.getPlayerOrThrow
import me.lukiiy.utils.help.Utils.resetNametag
import me.lukiiy.utils.help.Utils.resetTextures
import me.lukiiy.utils.help.Utils.setNametag
import me.lukiiy.utils.help.Utils.setTextures
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.`object`.ObjectContents
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.File
import java.util.UUID

object Identity { // TODO
    private val req: (CommandSender) -> Boolean = { it.hasPermission("identity".asPermission()) }

    val changedSkin = mutableSetOf<UUID>()
    val changedNametag = mutableSetOf<UUID>()

    private val main = Commands.literal("identity")
        .requires { req(it.sender) }
        .then(Commands.argument("player", ArgumentTypes.players())
            .then(Commands.literal("nametag")
                .then(Commands.literal("#").executes {
                    val sender = it.source.sender
                    val target = it.getPlayerOrThrow("player")

                    target.resetNametag()
                    changedNametag.remove(target.uniqueId)
                    sender.sendMessage(Defaults.neutral(if (sender == target) "Your nametag has been reset".asFancyString() else target.name().color(Defaults.YELLOW).append("'s nametag has been reset".asFancyString())))
                    Command.SINGLE_SUCCESS
                })
                .then(Commands.argument("new_username", StringArgumentType.word()).executes {
                    val sender = it.source.sender
                    val target = it.getPlayerOrThrow("player")
                    val new = StringArgumentType.getString(it, "new_username")

                    if (!new.matches(Utils.USERNAME_REGEX)) throw Defaults.CmdException("Invalid username! Must only include alphanumeric characters, underscores, and must be within 16 characters.".asFancyString())

                    target.setNametag(new)
                    changedNametag.add(target.uniqueId)
                    sender.sendMessage(Defaults.neutral((if (sender == target) "Your nametag is now ".asFancyString() else target.name().color(Defaults.YELLOW).append("'s nametag is now ".asFancyString())).append(new.asFancyString().color(Defaults.YELLOW))))
                    Command.SINGLE_SUCCESS
                })
            )
            .then(Commands.literal("skin")
                .then(Commands.literal("#").executes {
                    val sender = it.source.sender
                    val target = it.getPlayerOrThrow("player")

                    target.resetTextures()
                    changedSkin.remove(target.uniqueId)
                    sender.sendMessage(Defaults.neutral((if (sender == target) "Your skin has been reset".asFancyString() else target.name().color(Defaults.YELLOW).append("'s skin has been reset".asFancyString()))))
                    Command.SINGLE_SUCCESS
                })
                .then(Commands.argument("player_name", StringArgumentType.word()).executes {
                    val sender = it.source.sender
                    val target = it.getPlayerOrThrow("player")
                    val sourceName = StringArgumentType.getString(it, "player_name")

                    target.setTextures(sourceName)
                    changedSkin.add(target.uniqueId)
                    sender.sendMessage(Defaults.neutral((if (sender == target) "Your skin has been updated".asFancyString() else target.name().color(Defaults.YELLOW).append("'s skin has been updated ".asFancyString()))))
                    Command.SINGLE_SUCCESS
                })
                .then(Commands.argument("texture_or_file", StringArgumentType.string())
                    .executes {
                        val sender = it.source.sender
                        val target = it.getPlayerOrThrow("player")
                        val (texture, signature) = readSkinFile(StringArgumentType.getString(it, "texture_or_file"))

                        target.setTextures(texture, signature)
                        changedSkin.add(target.uniqueId)
                        sender.sendMessage(Defaults.neutral((if (sender == target) "Your skin has updated".asFancyString() else target.name().color(Defaults.YELLOW).append("'s skin has updated".asFancyString()))))
                        Command.SINGLE_SUCCESS
                    }
                    .then(Commands.argument("signature", StringArgumentType.greedyString()).executes {
                        val sender = it.source.sender
                        val target = it.getPlayerOrThrow("player")

                        target.setTextures(StringArgumentType.getString(it, "texture_or_file"), StringArgumentType.getString(it, "signature"))
                        changedSkin.add(target.uniqueId)
                        sender.sendMessage(Defaults.neutral((if (sender == target) "Your skin has updated".asFancyString() else target.name().color(Defaults.YELLOW).append("'s skin has updated".asFancyString()))))
                        Command.SINGLE_SUCCESS
                    })
                )
            )
        )

    private val list = Commands.literal("identitylist") // TODO
        .requires { req(it.sender) }
        .executes {
            val sender = it.source.sender
            if (changedNametag.isEmpty() && changedSkin.isEmpty()) throw Defaults.CmdException(Component.text("No identity changes found"))

            sender.sendMessage(Defaults.neutral(Component.text("Players with changed username:").appendNewline().append(Component.join(Defaults.LIST_LIKE, changedNametag.mapNotNull { p -> Bukkit.getPlayer(p)?.name() }.map { c -> c.color(Defaults.YELLOW) }))))
            sender.sendMessage(Defaults.neutral(Component.text("Players with changed skin:").appendNewline().append(Component.join(Defaults.LIST_LIKE, changedSkin.mapNotNull { p -> Bukkit.getPlayer(p)?.name() }.map { c -> c.color(Defaults.YELLOW) }))))
            Command.SINGLE_SUCCESS
        }

    fun register(): LiteralCommandNode<CommandSourceStack> = main.build()
    fun registerList(): LiteralCommandNode<CommandSourceStack> = list.build()

    // File format: basically a txt file; 1st line = texture, 2nd line = signature
    private fun readSkinFile(name: String): Pair<String, String> { // TODO
        val fName = name.replace(".skin", "")
        val file = File(Lukitils.getInstance().dataFolder, "skins/$fName.skin")
        if (!file.exists()) throw Defaults.CmdException("Skin file not found!".asFancyString())

        val lines = file.readLines()
        if (lines.size < 2 || lines[0].isBlank() || lines[1].isBlank()) throw Defaults.CmdException("Skin file '$fName.skin' is malformed!.".asFancyString())

        return lines[0].trim() to lines[1].trim()
    }
}