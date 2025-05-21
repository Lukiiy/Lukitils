package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Toast
import me.lukiiy.utils.help.Utils
import me.lukiiy.utils.help.Utils.asFancyString
import me.lukiiy.utils.help.Utils.asPermission
import me.lukiiy.utils.help.Utils.group
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.Style
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object DisplayToast {
    private val main = Commands.literal("displaytoast")
        .requires { it.sender.hasPermission("displaytoast".asPermission()) }
        .then(Commands.argument("players", ArgumentTypes.players())
            .then(Commands.argument("style", StringArgumentType.word())
                .suggests { _, builder ->
                    val input = builder.remaining.uppercase()
                    Toast.Style.entries.forEach { if (it.name.lowercase().startsWith(input.lowercase())) builder.suggest(it.name.lowercase()) }
                    builder.buildFuture()
                }
                .then(Commands.argument("item", ArgumentTypes.itemStack())
                    .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes {
                            val sender = it.source.sender
                            val target = it.getArgument("player", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().toList().takeIf {it.isNotEmpty()} ?: throw Defaults.NOT_FOUND
                            val style =
                                try { Toast.Style.valueOf(StringArgumentType.getString(it, "style").uppercase()) }
                                catch (_: IllegalArgumentException) { throw Defaults.CmdException(Component.text("Expected a valid style type.")) }

                            handle(sender, target, style, it.getArgument("item", ItemStack::class.java), StringArgumentType.getString(it, "message"))
                            Command.SINGLE_SUCCESS
                        }
                    ))))

    fun register(): LiteralCommandNode<CommandSourceStack> = main.build()

    private fun handle(sender: CommandSender, targets: List<Player>, style: Toast.Style, item: ItemStack, message: String) {
        val msg = message.replace("/n", "\n").replace("&", "§")
        val chatAnnounce = msg.contains(" -c")

        Utils.adminCmdFeedback(sender, "Displyed a toast to ${targets.group()}")
        sender.sendMessage(Defaults.neutral("Displaying a toast to ${targets.group(Style.style(Defaults.YELLOW)).hoverEvent(HoverEvent.showText(Component.join(Defaults.DEF_SEPARATOR, targets.stream().map(Player::name).toList())))}".asFancyString()))
        Toast.display(sender.name, targets, style, item, Defaults.FancyString.deserialize(msg.replace(" -c", "")), chatAnnounce)
    }
}