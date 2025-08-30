package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.registry.RegistryKey
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Utils
import me.lukiiy.utils.help.Utils.asFancyString
import me.lukiiy.utils.help.Utils.asPermission
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile

object QuickThrow {
    private val main = Commands.literal("quickthrow")
        .requires { it.sender.hasPermission("quickthrow".asPermission()) }
        .then(Commands.argument("entity", ArgumentTypes.resource(RegistryKey.ENTITY_TYPE))
            .executes {
                handle(it.source.sender, it.getArgument("entity", EntityType::class.java))
                Command.SINGLE_SUCCESS
            }
            .then(Commands.argument("speed", DoubleArgumentType.doubleArg(0.0))
                .executes {
                    handle(it.source.sender, it.getArgument("entity", EntityType::class.java), DoubleArgumentType.getDouble(it, "speed"))
                    Command.SINGLE_SUCCESS
                }
            )
        )

    fun register(): LiteralCommandNode<CommandSourceStack> = main.build()

    private fun handle(sender: CommandSender, entityType: EntityType, speed: Double = 1.0) {
        val p = sender as? Player ?: throw Defaults.NON_PLAYER
        val entityKey = entityType.translationKey()

        p.world.spawnEntity(p.eyeLocation, entityType).apply {
            if (this is Projectile) this.shooter = p
            velocity = p.location.direction.normalize().multiply(speed)
        }

        Utils.adminCmdFeedback(sender, "Quick threw a $entityKey")
        sender.sendMessage(Defaults.neutral("Quick throwing a ".asFancyString().append(Component.translatable(entityKey).color(Defaults.YELLOW)).append(" with speed ".asFancyString()).append("$speed".asFancyString().color(Defaults.YELLOW))))
    }
}