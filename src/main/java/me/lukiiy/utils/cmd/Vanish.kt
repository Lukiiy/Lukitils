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
import me.lukiiy.utils.help.Utils.asPermission
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
    private val req: (CommandSender) -> Boolean = { it.hasPermission("vanish".asPermission()) }
    private val vanished = mutableSetOf<UUID>()
    fun getVanished(): Set<UUID> = vanished

    fun registerMain(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("vanish")
            .requires { req(it.sender) }
            .then(Commands.argument("player", ArgumentTypes.player())
                .executes {
                    val sender = it.source.sender
                    val target = it.getArgument("player", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().findFirst().orElse(null) ?: return@executes 0

                    handle(sender, target)
                    Command.SINGLE_SUCCESS
                })
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER

                handle(sender, sender)
                Command.SINGLE_SUCCESS
            }
        .build()
    }

    private fun handle(sender: CommandSender, target: Player) {
        val isVanished = target.uniqueId in vanished
        var message = Defaults.success(Component.text("Vanish is now ").append(if (isVanished) Defaults.OFF else Defaults.ON))

        if (target != sender) {
            sender.sendMessage(message.append(Component.text(" for ").color(Defaults.GRAY)).append(target.name().color(Defaults.YELLOW)))
            message = message.append(Component.text(" (by ${sender.name})").color(Defaults.GRAY))
        }

        if (!isVanished) {
            Bukkit.getOnlinePlayers().minus(target).forEach { it.hidePlayer(Lukitils.getInstance(), target) }
            vanished.add(target.uniqueId)
        } else {
            Bukkit.getOnlinePlayers().minus(target).forEach { it.showPlayer(Lukitils.getInstance(), target) }
            vanished.remove(target.uniqueId)
        }

        target.sendMessage(message)
    }

    fun registerList(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("vanishlist")
            .requires { req(it.sender) }
            .executes {
                val sender = it.source.sender
                if (vanished.isEmpty()) throw Defaults.CmdException(Component.text("No vanished players found"))

                val players = vanished.mapNotNull { p -> Bukkit.getPlayer(p)?.name() }
                if (players.isEmpty()) throw Defaults.CmdException(Component.text("No online vanished players found"))

                sender.sendMessage(Defaults.success(Component.text("Vanished players:").appendNewline().append(Component.join(Defaults.LIST_LIKE, players.map { c -> c.color(Defaults.YELLOW) }))))
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
            p.sendMessage(Defaults.success(Component.text("You're still vanished!").clickEvent(ClickEvent.suggestCommand("/vanish"))))
        }

        Bukkit.getGlobalRegionScheduler().run(Lukitils.getInstance()) { task ->
            vanished.stream().map { id -> Bukkit.getPlayer(id!!) }.filter { Objects.nonNull(it) }.forEach { player -> p.hidePlayer(Lukitils.getInstance(), player!!) }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun quit(e: PlayerQuitEvent) {
        if (vanished.contains(e.player.uniqueId)) e.quitMessage(null)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun ping(e: PaperServerListPingEvent) {
        e.listedPlayers.removeIf { vanished.contains(it!!.id()) }
        e.numPlayers = e.listedPlayers.size
    }

    @EventHandler
    fun advancement(e: PlayerAdvancementDoneEvent) {
        if (vanished.contains(e.player.uniqueId)) e.message(null)
    }
}