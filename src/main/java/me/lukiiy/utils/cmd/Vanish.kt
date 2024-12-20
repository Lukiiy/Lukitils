package me.lukiiy.utils.cmd

import com.destroystokyo.paper.event.server.PaperServerListPingEvent
import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.Lukitils
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*

object Vanish : Listener {
    private val vanished = mutableSetOf<UUID>()
    fun getVanished(): Set<UUID> = vanished

    fun registerMain(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("vanish").requires { it.sender.hasPermission("lukitils.vanish") }
            .then(Commands.argument("player", ArgumentTypes.player())
                .executes {
                    val sender = it.source.sender
                    val target = it.getArgument("player", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().findFirst().orElse(null) ?: return@executes 0

                    handle(sender, target)
                    Command.SINGLE_SUCCESS
                })
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER.create()

                handle(sender, sender)
                Command.SINGLE_SUCCESS
            }
        .build()
    }

    private fun handle(sender: CommandSender, target: Player) {
        val isVanished = target.uniqueId in vanished
        val state = if (isVanished) Defaults.OFF else Defaults.ON
        var message = Defaults.msg(Component.text("Vanish is now ").append(state))

        if (target != sender) {
            sender.sendMessage(message.append(Component.text(" for ").color(Defaults.GRAY)).append(target.name().color(Defaults.YELLOW)))
            message = message.append(Component.text(" (by ${sender.name})").color(Defaults.GRAY))
        }

        if (!isVanished) {
            Bukkit.getOnlinePlayers().filterNot { it == target }.forEach { it.hidePlayer(Lukitils.getInstance(), target) }
            vanished.add(target.uniqueId)
        } else {
            Bukkit.getOnlinePlayers().filterNot { it == target }.forEach { it.showPlayer(Lukitils.getInstance(), target) }
            vanished.remove(target.uniqueId)
        }

        target.sendMessage(message)
    }

    fun registerList(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("vanishlist").requires { it.sender.hasPermission("lukitils.vanish") }
            .executes {
                val sender = it.source.sender
                if (vanished.isEmpty()) {
                    sender.sendMessage(Defaults.fail(Component.text("There are no vanished players.")))
                    Command.SINGLE_SUCCESS
                }

                sender.sendMessage(Defaults.msg(Component.text("Vanished player list:")))
                for (v in vanished) {
                    val player = Bukkit.getPlayer(v)
                    if (player != null) sender.sendMessage(Component.text("• ").color(Defaults.GRAY).append(player.name()))
                }
                Command.SINGLE_SUCCESS
            }
        .build()
    }

    // Listener
    @EventHandler(priority = EventPriority.LOWEST)
    fun join(e: PlayerJoinEvent) {
        if (vanished.isEmpty()) return
        val p = e.player

        if (vanished.contains(p.uniqueId)) {
            e.joinMessage(null)
            Bukkit.getOnlinePlayers().stream().filter { player: Player? -> player != p }.forEach { player: Player? -> player!!.hidePlayer(Lukitils.getInstance(), p) }
            p.sendMessage(Defaults.msg(Component.text("You're still vanished!").clickEvent(ClickEvent.suggestCommand("/vanish"))))
        }

        Bukkit.getScheduler().runTaskLater(Lukitils.getInstance(),
            Runnable { vanished.stream().map { id -> Bukkit.getPlayer(id!!) }.filter { Objects.nonNull(it) }.forEach { player -> p.hidePlayer(Lukitils.getInstance(), player!!) } }, 2L
        )
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun quit(e: PlayerQuitEvent) {
        if (vanished.isNotEmpty() && vanished.contains(e.player.uniqueId)) e.quitMessage(null)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun ping(e: PaperServerListPingEvent) {
        e.listedPlayers.removeIf { vanished.contains(it!!.id()) }
        e.numPlayers = e.listedPlayers.size
    }

    @EventHandler
    fun advancement(e: PlayerAdvancementDoneEvent) {
        if (vanished.isNotEmpty() && vanished.contains(e.player.uniqueId)) e.message(null)
    }
}