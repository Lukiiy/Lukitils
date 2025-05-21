package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Utils.asPermission
import me.lukiiy.utils.help.Utils.getPlayersOrThrow
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType

object SimpleStats {
    private val req: (CommandSender) -> Boolean = { it.hasPermission("stats".asPermission()) }

    private val healUnit: (Player, Double) -> Unit = { t, a ->
        t.apply {
            heal(a)
            activePotionEffects.forEach { if (it.type.effectCategory != PotionEffectType.Category.BENEFICIAL) { t.removePotionEffect(it.type) } }
            fireTicks = 0
        }
    }

    private val feedUnit: (Player, Int) -> Unit = { t, a ->
        t.apply {
            foodLevel = foodLevel + a
            saturation = 5f
            exhaustion = 0f
        }
    }

    private val bareUnit: (Player, Int) -> Unit = { t, _ ->
        t.apply {
            health = 1.0
            foodLevel = 1
            saturation = 0f
        }
    }

    fun registerHeal(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("heal")
            .requires { req(it.sender) }
            .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0001)).executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER

                handle(sender, listOf(sender), DoubleArgumentType.getDouble(it, "amount"), healUnit, "Healed")
                Command.SINGLE_SUCCESS
            })
            .then(Commands.argument("players", ArgumentTypes.players())
                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0001)).executes {
                    val targets = it.getPlayersOrThrow("players")

                    handle(it.source.sender, targets, DoubleArgumentType.getDouble(it, "amount"), healUnit, "Healed")
                    Command.SINGLE_SUCCESS
                })

                .executes {
                    val targets = it.getPlayersOrThrow("players")

                    handle(it.source.sender, targets, 20.0, healUnit, "Healed")
                    Command.SINGLE_SUCCESS
                })
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER

                handle(sender, listOf(sender), 20.0, healUnit, "Healed")
                Command.SINGLE_SUCCESS
            }
        .build()
    }

    fun registerFeed(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("feed")
            .requires { req(it.sender) }
            .then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER

                handle(sender, listOf(sender), IntegerArgumentType.getInteger(it, "amount"), feedUnit, "Fed")
                Command.SINGLE_SUCCESS
            })
            .then(Commands.argument("players", ArgumentTypes.players())
                .then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes {
                    val targets = it.getPlayersOrThrow("players")

                    handle(it.source.sender, targets, IntegerArgumentType.getInteger(it, "amount"), feedUnit, "Fed")
                    Command.SINGLE_SUCCESS
                })

                .executes {
                    val targets = it.getPlayersOrThrow("players")

                    handle(it.source.sender, targets, 20, feedUnit, "Fed")
                    Command.SINGLE_SUCCESS
                })
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER

                handle(sender, listOf(sender), 20, feedUnit, "Fed")
                Command.SINGLE_SUCCESS
            }
        .build()
    }

    fun registerBare(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("barelife")
            .requires { req(it.sender) }
            .then(Commands.argument("players", ArgumentTypes.players())
                .executes {
                    val targets = it.getPlayersOrThrow("players")

                    handle(it.source.sender, targets, 1, bareUnit, "Barelifed")
                    Command.SINGLE_SUCCESS
                })
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER

                handle(sender, listOf(sender), 1, bareUnit, "Barelifed")
                Command.SINGLE_SUCCESS
            }
            .build()
    }

    private fun <T> handle(sender: CommandSender, targets: List<Player>, amount: T, act: (Player, T) -> Unit, actDesc: String) {
        var msg = Component.text(actDesc)

        targets.forEach {
            act(it, amount)

            if (it != sender) {
                sender.sendMessage(Defaults.neutral(msg.appendSpace().append(it.name().color(Defaults.YELLOW)).append(Component.text(" by ").append(Component.text("$amount").color(Defaults.YELLOW)))))
                msg = msg.append(Component.text(" (by ").append(sender.name().color(Defaults.YELLOW)).append(Component.text(")")))
            }

            it.sendMessage(Defaults.neutral(msg.append(Component.text(" by ")).append(Component.text("$amount").color(Defaults.YELLOW))))
        }
    }
}