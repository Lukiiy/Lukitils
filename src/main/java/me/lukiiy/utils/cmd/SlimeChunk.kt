package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import me.lukiiy.utils.Defaults
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

object SlimeChunk {
    private val defMsg: Component = Defaults.msg(Component.text("[a] in a slime chunk."))

    fun register(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("slimechunk")
            .then(Commands.argument("player", ArgumentTypes.player())
                .executes {
                    val sender = it.source.sender
                    val target = it.getArgument("player", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().findFirst().orElse(null) ?: return@executes 0
                    val state = if (target.chunk.isSlimeChunk) Component.text("is").color(Defaults.GREEN) else Component.text("isn't").color(Defaults.RED)

                    sender.sendMessage(defMsg.replaceText {it.matchLiteral("[a]").replacement(target.name().color(Defaults.YELLOW).appendSpace().append(state)).build()})
                    Command.SINGLE_SUCCESS
                })
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER.create()
                val state = if (sender.chunk.isSlimeChunk) Component.text("are").color(Defaults.GREEN) else Component.text("aren't").color(Defaults.RED)

                sender.sendMessage(defMsg.replaceText {it.matchLiteral("[a]").replacement(Component.text("You ").append(state)).build()})
                Command.SINGLE_SUCCESS
            }
        .build()
    }
}