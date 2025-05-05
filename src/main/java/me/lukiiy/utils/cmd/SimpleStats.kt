package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Utils.asPermission
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType

object SimpleStats {
    private val req: (CommandSender) -> Boolean = { it.hasPermission("stats".asPermission()) }

    fun registerHeal(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("heal")
            .requires { req(it.sender) }
            .then(Commands.argument("players", ArgumentTypes.players())

                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0001)).executes {
                    val targets = it.getArgument("players", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().toList().takeIf {it.isNotEmpty()} ?: throw Defaults.NOT_FOUND

                    handle(it.source.sender, targets, DoubleArgumentType.getDouble(it, "amount"), { t, a ->
                        t.heal(a)
                        t.activePotionEffects.forEach { if (it.type.effectCategory != PotionEffectType.Category.BENEFICIAL) t.removePotionEffect(it.type) }
                        t.fireTicks = 0
                    }, "Healed")
                    Command.SINGLE_SUCCESS
                })

                .executes {
                    val targets = it.getArgument("players", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().toList().takeIf {it.isNotEmpty()} ?: throw Defaults.NOT_FOUND

                    handle(it.source.sender, targets, 20.0, { t, a ->
                        t.heal(a)
                        t.activePotionEffects.forEach { if (it.type.effectCategory != PotionEffectType.Category.BENEFICIAL) t.removePotionEffect(it.type) }
                        t.fireTicks = 0
                    }, "Healed")
                    Command.SINGLE_SUCCESS
                })
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER

                handle(sender, listOf(sender), 20.0, { t, a ->
                    t.heal(a)
                    t.activePotionEffects.forEach { if (it.type.effectCategory != PotionEffectType.Category.BENEFICIAL) t.removePotionEffect(it.type) }
                    t.fireTicks = 0
                }, "Healed")
                Command.SINGLE_SUCCESS
            }
        .build()
    }

    fun registerFeed(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("feed")
            .requires { req(it.sender) }
            .then(Commands.argument("players", ArgumentTypes.players())
                .then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes {
                    val targets = it.getArgument("players", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().toList().takeIf {it.isNotEmpty()} ?: throw Defaults.NOT_FOUND

                    handle(it.source.sender, targets, IntegerArgumentType.getInteger(it, "amount"), { t, a -> t.foodLevel = t.foodLevel + a }, "Fed")
                    Command.SINGLE_SUCCESS
                })

                .executes {
                    val targets = it.getArgument("players", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().toList().takeIf {it.isNotEmpty()} ?: throw Defaults.NOT_FOUND

                    handle(it.source.sender, targets, 20, { t, a -> t.foodLevel = t.foodLevel + a }, "Fed")
                    Command.SINGLE_SUCCESS
                })
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER

                handle(sender, listOf(sender), 20, { t, a -> t.foodLevel = t.foodLevel + a }, "Fed")
                Command.SINGLE_SUCCESS
            }
        .build()
    }

    fun registerBare(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("barelife")
            .requires { req(it.sender) }
            .then(Commands.argument("players", ArgumentTypes.players())
                .executes {
                    val targets = it.getArgument("players", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().toList().takeIf {it.isNotEmpty()} ?: throw Defaults.NOT_FOUND

                    handle(it.source.sender, targets, 1, { t, _ ->
                        t.foodLevel = 1
                        t.health = 1.0
                    }, "Barelifed")
                    Command.SINGLE_SUCCESS
                })
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER

                handle(sender, listOf(sender), 1, { t, _ ->
                    t.foodLevel = 1
                    t.health = 1.0
                }, "Barelifed")
                Command.SINGLE_SUCCESS
            }
            .build()
    }

    private fun <T> handle(sender: CommandSender, targets: List<Player>, amount: T, act: (Player, T) -> Unit, actDesc: String) {
        var msg = Component.text(actDesc)

        targets.forEach {
            act(it, amount)

            if (it != sender) {
                sender.sendMessage(Defaults.success(msg.appendSpace().append(it.name().color(Defaults.YELLOW)).append(Component.text(" by ").append(Component.text("$amount").color(Defaults.YELLOW)))))
                msg = msg.append(Component.text(" (by ").append(sender.name().color(Defaults.YELLOW)).append(Component.text(")")))
            }

            it.sendMessage(Defaults.success(msg))
        }
    }
}