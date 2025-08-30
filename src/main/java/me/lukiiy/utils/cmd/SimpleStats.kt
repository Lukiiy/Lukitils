package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.Lukitils
import me.lukiiy.utils.help.Utils.asFancyString
import me.lukiiy.utils.help.Utils.asPermission
import me.lukiiy.utils.help.Utils.getPlayersOrThrow
import me.lukiiy.utils.help.Utils.group
import net.kyori.adventure.text.format.Style
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
            foodLevel += a
            saturation = 5f
            exhaustion = 0f
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

    private fun <T> handle(sender: CommandSender, targets: List<Player>, amount: T, act: (Player, T) -> Unit, actDesc: String) {
        targets.forEach {
            act(it, amount)
            if (!Lukitils.getInstance().config.getBoolean("silentStats", true) && it != sender) it.sendMessage(Defaults.neutral("$actDesc by ".asFancyString().append(" (by ".asFancyString()).append(sender.name()).append(")".asFancyString())))
        }

        sender.sendMessage(Defaults.neutral("$actDesc ".asFancyString().append(targets.group(Style.style(Defaults.YELLOW))).append(" by ".asFancyString()).append("$amount".asFancyString().color(Defaults.YELLOW))))
    }
}