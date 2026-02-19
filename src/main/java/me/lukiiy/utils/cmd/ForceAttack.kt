package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Utils.asFancyString
import me.lukiiy.utils.help.Utils.asPermission
import net.kyori.adventure.text.Component
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Mob


object ForceAttack {
    private val main = Commands.literal("forceattack")
        .requires { it.sender.hasPermission("forceattack".asPermission()) }
        .then(Commands.argument("entity", ArgumentTypes.entities())
            .then(Commands.argument("angry_at", ArgumentTypes.entity())
                .executes {
                    val entities = it.getArgument("entity", EntitySelectorArgumentResolver::class.java).resolve(it.getSource()).filterIsInstance<Mob>()
                    val angryAt = it.getArgument("angry_at", EntitySelectorArgumentResolver::class.java).resolve(it.getSource()).first()

                    if (entities.isEmpty()) throw Defaults.CmdException("There are no entities to target others".asFancyString())
                    if (angryAt !is LivingEntity) throw Defaults.CmdException("You can only set entities to target a living entity".asFancyString())

                    entities.forEach { entity ->
                        entity.target = angryAt
                        entity.isAggressive = true
                    }

                    it.source.sender.sendMessage(Defaults.neutral(Component.text("${entities.size} ${(if (entities.size == 1) "entity" else "entities")}").color(Defaults.YELLOW).append(" ${if (entities.size == 1) "is" else "are"} now targeting ".asFancyString()).append(angryAt.name().color(Defaults.YELLOW))))
                    Command.SINGLE_SUCCESS
                })
        )

    fun register(): LiteralCommandNode<CommandSourceStack> = main.build()
}