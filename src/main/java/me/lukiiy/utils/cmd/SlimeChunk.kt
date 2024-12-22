package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import me.lukiiy.utils.Defaults
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import org.bukkit.Chunk
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
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER
                val state = if (sender.chunk.isSlimeChunk) Component.text("are").color(Defaults.GREEN) else Component.text("aren't").color(Defaults.RED)

                val nearby = if (!sender.chunk.isSlimeChunk) findNearby(sender) else listOf()

                sender.sendMessage(defMsg.replaceText {it.matchLiteral("[a]").replacement(Component.text("You ").append(state)).build()}.append(if (nearby.isEmpty()) Component.empty() else Component.text(" [Nearby slime chunks]").hoverEvent(HoverEvent.showText(Component.join(Defaults.LIST_LIKE, nearby.map { Component.text("[${it.x}, ${it.z}]").color(Defaults.WHITE) })))))
                Command.SINGLE_SUCCESS
            }
        .build()
    }

    private fun findNearby(player: Player): List<Chunk> {
        val current = player.chunk
        val world = player.world

        val slimeChunks = mutableListOf<Chunk>()

        for (dx in -2..2) {
            for (dz in -2..2) {
                if (dx == 0 && dz == 0) continue

                val check = world.getChunkAt(current.x + dx, current.z + dz)
                if (check.isSlimeChunk) slimeChunks.add(check)
            }
        }

        return slimeChunks
    }
}