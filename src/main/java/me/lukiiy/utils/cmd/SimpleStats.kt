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
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffectType

object SimpleStats {
    fun registerHeal(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("heal").requires { it.sender.hasPermission("lukitils.heal") }
            .then(Commands.argument("players", ArgumentTypes.players())

                .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0.0001)).executes {
                    val targets = it.getArgument("players", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().toList().takeIf {it.isNotEmpty()} ?: throw Defaults.NOT_FOUND_MULTI.create()

                    handle(it.source.sender, targets, DoubleArgumentType.getDouble(it, "amount"), { t, a ->
                        t.heal(a)
                        t.activePotionEffects.forEach { if (it.type.effectCategory != PotionEffectType.Category.BENEFICIAL) t.removePotionEffect(it.type) }
                        t.fireTicks = 0
                    }, "Healed")
                    Command.SINGLE_SUCCESS
                })

                .executes {
                    val targets = it.getArgument("players", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().toList().takeIf {it.isNotEmpty()} ?: throw Defaults.NOT_FOUND_MULTI.create()

                    handle(it.source.sender, targets, 20.0, { t, a ->
                        t.heal(a)
                        t.activePotionEffects.forEach { if (it.type.effectCategory != PotionEffectType.Category.BENEFICIAL) t.removePotionEffect(it.type) }
                        t.fireTicks = 0
                    }, "Healed")
                    Command.SINGLE_SUCCESS
                })
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER.create()

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
        return Commands.literal("feed").requires { it.sender.hasPermission("lukitils.feed") }
            .then(Commands.argument("players", ArgumentTypes.players())

                .then(Commands.argument("amount", IntegerArgumentType.integer(1)).executes {
                    val targets = it.getArgument("players", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().toList().takeIf {it.isNotEmpty()} ?: throw Defaults.NOT_FOUND_MULTI.create()

                    handle(it.source.sender, targets, IntegerArgumentType.getInteger(it, "amount"), { t, a -> t.foodLevel = t.foodLevel + a }, "Fed")
                    Command.SINGLE_SUCCESS
                })

                .executes {
                    val targets = it.getArgument("players", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().toList().takeIf {it.isNotEmpty()} ?: throw Defaults.NOT_FOUND_MULTI.create()

                    handle(it.source.sender, targets, 20, { t, a -> t.foodLevel = t.foodLevel + a }, "Fed")
                    Command.SINGLE_SUCCESS
                })
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER.create()

                handle(sender, listOf(sender), 20, { t, a -> t.foodLevel = t.foodLevel + a }, "Fed")
                Command.SINGLE_SUCCESS
            }
        .build()
    }


    private fun <T> handle(sender: CommandSender, targets: List<Player>, amount: T, act: (Player, T) -> Unit, actDesc: String) {
        var msg = Defaults.msg(Component.text("$actDesc by ").append(Component.text("$amount").color(Defaults.YELLOW)))

        targets.forEach {
            act(it, amount)

            if (it != sender) {
                sender.sendMessage(msg.append(it.name().color(Defaults.YELLOW)))
                msg = msg.append(Component.text(" (by ").append(sender.name().color(Defaults.YELLOW)).append(Component.text(")")))
            }
            it.sendMessage(msg)
        }
    }
}