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

object BareLife {
    fun register(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("barelife").requires {it.sender.hasPermission("lukitils.barelife")}
            .then(Commands.argument("player", ArgumentTypes.player())
                .executes {
                    val sender = it.source.sender
                    val target = it.getArgument("player", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().findFirst().orElse(null) ?: return@executes 0

                    handle(sender, target)
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
        target.apply {
            health = 1.0
            foodLevel = 1
            sendHealthUpdate()
        }

        sender.sendMessage(Defaults.msg(Component.text("Set ").append(target.name().color(Defaults.YELLOW)).append(Component.text("'s HP and Hunger to 1"))))
    }
}
