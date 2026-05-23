package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Utils
import me.lukiiy.utils.help.Utils.asFancyString
import me.lukiiy.utils.help.Utils.asPermission
import me.lukiiy.utils.help.Utils.getPlayerOrThrow
import net.kyori.adventure.text.Component
import org.bukkit.entity.Fox
import org.bukkit.entity.Tameable

object ForceTame {
    private val main = Commands.literal("forcetame")
        .requires { it.sender.hasPermission("forcetame".asPermission()) }
        .then(Commands.argument("entity", ArgumentTypes.entity())
            .then(Commands.argument("tamer", ArgumentTypes.player())
                .executes {
                    val entity = it.getArgument("entity", EntitySelectorArgumentResolver::class.java).resolve(it.getSource()).first()
                    val tamer = it.getPlayerOrThrow("tamer")
                    val sender = it.source.sender

                    val tamerComp = tamer.name().color(Defaults.YELLOW)
                    val keyLangComp = Component.translatable(entity.type.translationKey()).color(Defaults.YELLOW)

                    if (entity is Tameable) {
                        entity.owner = tamer
                        entity.isTamed = true

                        sender.sendMessage(Defaults.neutral("Set ".asFancyString().append(tamerComp).append(" as the tamer for ".asFancyString()).append(keyLangComp)))
                        Utils.adminCmdFeedback(sender, "Set ${tamer.name} as the tamer for ${entity.type.name}")
                    } else if (entity is Fox) {
                        entity.firstTrustedPlayer = tamer

                        sender.sendMessage(Defaults.neutral("Set ".asFancyString().append(tamerComp).append(" as the primary trusted player for ".asFancyString()).append(keyLangComp)))
                        Utils.adminCmdFeedback(sender, "Set ${tamer.name} as the primary trusted player for ${entity.type.name}")
                    } else throw Defaults.CmdException("Such entity can't be tamed or can't trust a player.".asFancyString())

                    Command.SINGLE_SUCCESS
                })
        )

    fun register(): LiteralCommandNode<CommandSourceStack> = main.build()
}