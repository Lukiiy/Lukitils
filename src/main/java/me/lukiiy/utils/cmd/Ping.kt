package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import me.lukiiy.utils.Defaults
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player

object Ping {
    private val main = Commands.literal("ping")
        .then(Commands.argument("player", ArgumentTypes.player())
            .executes {
                val sender = it.source.sender
                val target = it.getArgument("player", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().findFirst().orElse(null) ?: throw Defaults.NOT_FOUND

                sender.sendMessage(Defaults.success(target.name().append(Component.text("'s ping is ").append(Component.text(target.ping).color(Defaults.PURPLE).decorate(TextDecoration.UNDERLINED)).append(Component.text(" ms")))))
                Command.SINGLE_SUCCESS
            })
        .executes {
            val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER

            sender.sendMessage(Defaults.success(Component.text("Your ping is ").append(Component.text(sender.ping).color(Defaults.PURPLE).decorate(TextDecoration.UNDERLINED)).append(Component.text("ms"))))
            Command.SINGLE_SUCCESS
        }

    fun register(): LiteralCommandNode<CommandSourceStack> = main.build()
}