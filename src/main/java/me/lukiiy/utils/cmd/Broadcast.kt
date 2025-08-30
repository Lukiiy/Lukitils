package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.dialog.Dialog
import io.papermc.paper.registry.data.dialog.DialogBase
import io.papermc.paper.registry.data.dialog.body.DialogBody
import io.papermc.paper.registry.data.dialog.type.DialogType
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Utils
import me.lukiiy.utils.help.Utils.asFancyString
import me.lukiiy.utils.help.Utils.asPermission
import me.lukiiy.utils.help.Utils.stripArgument
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

object Broadcast {
    private val main = Commands.literal("broadcast")
        .requires { it.sender.hasPermission("broadcast".asPermission()) }
        .then(Commands.argument("msg", StringArgumentType.greedyString())
            .executes {
                val (msg, isDialog) = StringArgumentType.getString(it, "msg").replace("/n", "\n").replace("&", "§").stripArgument("d")

                Utils.adminCmdFeedback(it.source.sender, "Broadcasted a message")

                val fMsg = msg.asFancyString().colorIfAbsent(Defaults.BLUE)
                val dialog = Dialog.create { d -> d.empty().apply {
                    base(DialogBase.builder(Component.text("Broadcast")).canCloseWithEscape(true).afterAction(DialogBase.DialogAfterAction.CLOSE).apply {
                        body(listOf(DialogBody.plainMessage(fMsg)))
                    }.build())
                    type(DialogType.notice())
                } }

                if (isDialog) Bukkit.getOnlinePlayers().forEach { p -> p.showDialog(dialog) }
                else Bukkit.broadcast(Component.newline().append("   ".asFancyString()).append(fMsg).appendNewline())

                Command.SINGLE_SUCCESS
            })

    fun register(): LiteralCommandNode<CommandSourceStack> = main.build()
}
