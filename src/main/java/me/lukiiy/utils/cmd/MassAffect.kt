package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.MassEffect
import me.lukiiy.utils.help.Utils
import me.lukiiy.utils.help.Utils.asFancyString
import me.lukiiy.utils.help.Utils.asPermission
import me.lukiiy.utils.help.Utils.asPlainString
import me.lukiiy.utils.help.Utils.fancy
import me.lukiiy.utils.help.Utils.getPlayersOrThrow
import me.lukiiy.utils.help.Utils.mark
import me.lukiiy.utils.idk.MassEffectArgument
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.Style
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object MassAffect {
    private val main = Commands.literal("massaffect")
        .requires { it.sender.hasPermission("massaffect".asPermission()) }
        .then(Commands.argument("players", ArgumentTypes.players())
            .then(Commands.argument("effect", MassEffectArgument())
                .then(Commands.argument("intensity", DoubleArgumentType.doubleArg())
                    .executes {
                        val targets = it.getPlayersOrThrow("players")

                        handle(it.source.sender, targets, it.getArgument("effect", MassEffect::class.java), DoubleArgumentType.getDouble(it, "intensity"))
                        Command.SINGLE_SUCCESS
                    })
                .then(Commands.argument("clear", StringArgumentType.word())
                    .suggests { _, builder ->
                        builder.suggest("clear")
                        builder.buildFuture()
                    }
                    .executes {
                        val targets = it.getPlayersOrThrow("players")

                        handle(it.source.sender, targets, it.getArgument("effect", MassEffect::class.java), 0.0)
                        Command.SINGLE_SUCCESS
                    }
                )
                .executes {
                    val targets = it.getPlayersOrThrow("players")

                    handle(it.source.sender, targets, it.getArgument("effect", MassEffect::class.java), 1.0)
                    Command.SINGLE_SUCCESS
                }))

    fun register(): LiteralCommandNode<CommandSourceStack> = main.build()

    private fun handle(sender: CommandSender, targets: List<Player>, effect: MassEffect, intensity: Double) {
        val toRevert = intensity == 0.0
        var msg = if (toRevert) "Reverting ".asFancyString().append(Component.text(effect.name()).color(Defaults.YELLOW)).append("'s changes for ".asFancyString()) else "Applying ".asFancyString().append(Component.text(effect.name()).color(Defaults.YELLOW)) // oh the beauty of this line.

        val intens = intensity.fancy()
        if (!toRevert && intensity != 1.0) msg = msg.append(" with intensity ".asFancyString()).append(intens.asFancyString()).color(Defaults.YELLOW)
        val mark = targets.mark(Style.style(Defaults.YELLOW))

        targets.forEach { if (toRevert) effect.clear(it) else effect.apply(it, intensity + 1) }
        Utils.adminCmdFeedback(sender, "Massaffected ${mark.asPlainString()} with ${effect.name()} and intensity $intens")
        sender.sendMessage(Defaults.neutral(msg.append(mark)))
    }
}