package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import me.lukiiy.utils.Defaults
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Fly {
    fun register(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("fly").requires{it.sender.hasPermission("lukitils.fly")}
            .then(Commands.argument("player", ArgumentTypes.player())
                .executes {
                    val target = it.getArgument("player", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().findFirst().orElse(null) ?: return@executes 0

                    handle(it.source.sender, target)
                    Command.SINGLE_SUCCESS
                })
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NOT_FOUND.create()

                handle(sender, sender)
                Command.SINGLE_SUCCESS
            }
        .build()
    }

    private fun handle(sender: CommandSender, target: Player) {
        val update = !target.allowFlight
        val state = if (update) Defaults.ON else Defaults.OFF

        val msg = Defaults.msg(Component.text("Flight is now ")).append(state)
        target.allowFlight = update

        if (target != sender) {
            sender.sendMessage(msg.append(Component.text(" for ").append(target.name().color(Defaults.YELLOW))).color(Defaults.GRAY))
            target.sendMessage(msg.append(Component.text(" (by ").append(sender.name().color(Defaults.YELLOW)).append(Component.text(")"))).color(Defaults.GRAY))
        }
        else target.sendMessage(msg)
    }
}