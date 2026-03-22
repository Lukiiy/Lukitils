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
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.`object`.ObjectContents
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.io.File
import java.util.UUID

object Identity {
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
                    val uuid = target.uniqueId

                    sender.sendMessage(Defaults.neutral(if (sender == target) "Your nametag has been reset".asFancyString() else Component.empty().append(Utils.getIdChangedPlayerName(uuid)!!.asFancyString().color(Defaults.YELLOW)).append("'s nametag has been reset".asFancyString())))
                    target.resetNametag()
                    changedNametag.remove(uuid)

                    Command.SINGLE_SUCCESS
                })
                .then(Commands.argument("username", StringArgumentType.word()).executes {
                    val sender = it.source.sender
                    val target = it.getPlayerOrThrow("player")
                    val uuid = target.uniqueId
                    val new = StringArgumentType.getString(it, "username")

                    if (!new.matches(Utils.USERNAME_REGEX)) throw Defaults.CmdException("Invalid username! Must only include alphanumeric characters, underscores, and must be within 16 characters.".asFancyString())

                    sender.sendMessage(Defaults.neutral((if (sender == target) "Your nametag is now ".asFancyString() else Component.empty().append(target.name().color(Defaults.YELLOW)).append("'s nametag is now ".asFancyString())).append(new.asFancyString().color(Defaults.YELLOW))))
                    target.setNametag(new)
                    changedNametag.add(uuid)

                    Command.SINGLE_SUCCESS
                })
            )
            .then(Commands.literal("skin")
                .then(Commands.literal("#").executes {
                    val sender = it.source.sender
                    val target = it.getPlayerOrThrow("player")

                    target.resetTextures()
                    changedSkin.remove(target.uniqueId)
                    sender.sendMessage(Defaults.neutral((if (sender == target) "Your skin has been reset".asFancyString() else Component.empty().append(target.name().color(Defaults.YELLOW)).append("'s skin has been reset".asFancyString()))))

                    Command.SINGLE_SUCCESS
                })
                .then(Commands.argument("name", StringArgumentType.word()).executes {
                    val sender = it.source.sender
                    val target = it.getPlayerOrThrow("player")
                    val name = StringArgumentType.getString(it, "name")

                    if (name.endsWith(".skin")) {
                        val (texture, signature) = readSkinFile(name)

                        target.setTextures(texture, signature)
                    } else target.setTextures(name)

                    changedSkin.add(target.uniqueId)
                    sender.sendMessage(Defaults.neutral((if (sender == target) "Your skin has been updated".asFancyString() else Component.empty().append(target.name().color(Defaults.YELLOW)).append("'s skin has been updated ".asFancyString()))))

                    Command.SINGLE_SUCCESS
                })
                .then(Commands.argument("texture", StringArgumentType.string())
                    .then(Commands.argument("signature", StringArgumentType.greedyString()).executes {
                        val sender = it.source.sender
                        val target = it.getPlayerOrThrow("player")

                        target.setTextures(StringArgumentType.getString(it, "texture"), StringArgumentType.getString(it, "signature"))
                        changedSkin.add(target.uniqueId)
                        sender.sendMessage(Defaults.neutral((if (sender == target) "Your skin has updated".asFancyString() else Component.empty().append(target.name().color(Defaults.YELLOW)).append("'s skin has updated".asFancyString()))))

                        Command.SINGLE_SUCCESS
                    })
                )
            )
        )

    private val list = Commands.literal("identitylist")
        .requires { req(it.sender) }
        .executes {
            val sender = it.source.sender

            if (changedNametag.isEmpty() && changedSkin.isEmpty()) throw Defaults.CmdException("No identity changes found".asFancyString())
            val affected = (changedSkin + changedNametag).toSet()

            val entries = affected.mapNotNull { uuid ->
                val changed = Bukkit.getPlayer(uuid) ?: return@mapNotNull null
                val name = Utils.getIdChangedPlayerName(uuid) ?: changed.name
                var hover = "Disguised as ".asFancyString().color(Defaults.GRAY)

                if (name != changed.name) hover = hover.append(changed.name().color(Defaults.YELLOW)).appendSpace()
                if (Utils.getIdChangedPlayerSkin(uuid) != null) hover = hover.append(Component.`object`(ObjectContents.playerHead(changed)).color(NamedTextColor.WHITE))

                name.asFancyString().color(Defaults.YELLOW).hoverEvent(HoverEvent.showText(hover))
            }

            sender.sendMessage(Defaults.neutral("Disguised players:".asFancyString().appendNewline().append(Component.join(Defaults.LIST_LIKE, entries))))

            Command.SINGLE_SUCCESS
        }

    fun register(): LiteralCommandNode<CommandSourceStack> = main.build()
    fun registerList(): LiteralCommandNode<CommandSourceStack> = list.build()

    // File format: basically a txt file; texture + " " + signature; extension is ".skin"
    private fun readSkinFile(name: String): Pair<String, String> {
        val fName = name.replace(".skin", "")
        val file = File(Lukitils.getInstance().dataFolder, "skins/$fName.skin")
        if (!file.exists()) throw Defaults.CmdException("Skin file not found!".asFancyString())

        return file.readLines().firstOrNull()
            ?.takeIf { it.contains(" ") }
            ?.let { it.substringBefore(" ") to it.substringAfter(" ") }
            ?: throw Defaults.CmdException("Skin file \"$fName.skin\" is malformed!.".asFancyString())
    }
}