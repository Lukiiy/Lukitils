package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.PlayerUtils
import me.lukiiy.utils.help.Toast
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.Style
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object DisplayToast {
    fun register(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("displaytoast").requires { it.sender.hasPermission("lukitils.displaytoast") }
            .then(Commands.argument("player", ArgumentTypes.players())
            .then(Commands.argument("style", StringArgumentType.word())
                .suggests { _, builder ->
                    val input = builder.remaining.uppercase()
                    Toast.Style.entries.forEach { if (it.name.startsWith(input)) builder.suggest(it.name.lowercase()) }
                    builder.buildFuture()
                }
            .then(Commands.argument("item", ArgumentTypes.itemStack())
            .then(Commands.argument("message", StringArgumentType.greedyString())
                .executes {
                    val target = it.getArgument("player", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().toList().takeIf {it.isNotEmpty()} ?: throw Defaults.NOT_FOUND.create()
                    val style =
                        try { Toast.Style.valueOf(StringArgumentType.getString(it, "style").uppercase()) }
                        catch (_: IllegalArgumentException) { throw Defaults.CUSTOM_ERR("Expected a valid style type.").create() }

                    handle(it.source, target, style, it.getArgument("item", ItemStack::class.java), StringArgumentType.getString(it, "message"))
                    Command.SINGLE_SUCCESS
                }
            ))))
        .build()
    }

    private fun handle(source: CommandSourceStack, targets: List<Player>, style: Toast.Style, item: ItemStack, message: String) {
        val sender = source.sender
        val msg = message.replace("/n", "\n").replace("&", "§")
        val chatAnnounce = msg.contains(" -c")

        sender.sendMessage(Defaults.msg(Component.text("Displaying toast to ").append(PlayerUtils.group(targets, Style.style(Defaults.YELLOW)))).hoverEvent(HoverEvent.showText(Component.join(Defaults.SEPARATOR, targets.stream().map(Player::name).toList()))))
        Toast.display(sender.name, targets, style, item, Defaults.mini(msg.replace(" -c", "")), chatAnnounce)
    }
}