package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Utils.asFancyString
import me.lukiiy.utils.help.Utils.asPermission
import me.lukiiy.utils.help.Utils.getPlayerOrThrow
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.Statistic
import org.bukkit.command.CommandSender
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player

object Statistics {
    private val main = Commands.literal("statistics")
        .requires { it.sender.hasPermission("statistics".asPermission()) }
        .then(Commands.argument("player", ArgumentTypes.player())
            .then(Commands.argument("statistic", StringArgumentType.string())
                .suggests { _, builder ->
                    Statistic.entries.forEach { builder.suggest(it.name.lowercase()) }

                    builder.buildFuture()
                }
                .then(Commands.argument("type", StringArgumentType.word())
                    .suggests { ctx, builder ->
                        val statistic = runCatching {
                            Statistic.valueOf(StringArgumentType.getString(ctx, "statistic").normalize().uppercase())
                        }.getOrNull()

                        when (statistic?.type) {
                            Statistic.Type.BLOCK, Statistic.Type.ITEM -> Material.entries.filter { !it.isLegacy }.forEach { builder.suggest(it.name.lowercase()) }
                            Statistic.Type.ENTITY -> EntityType.entries.forEach { builder.suggest(it.name.lowercase()) }
                            else -> { }
                        }

                        builder.buildFuture()
                    }
                    .executes {
                        val sender = it.source.sender
                        val player = it.getPlayerOrThrow("player")
                        val statistic = StringArgumentType.getString(it, "statistic")
                        val type = StringArgumentType.getString(it, "type")

                        handle(sender, player, statistic, type)
                        Command.SINGLE_SUCCESS
                    }
                )
                .executes {
                    val sender = it.source.sender
                    val player = it.getPlayerOrThrow("player")
                    val statistic = StringArgumentType.getString(it, "statistic")

                    handle(sender, player, statistic, null)
                    Command.SINGLE_SUCCESS
                }
            )
        )

    fun register(): LiteralCommandNode<CommandSourceStack> = main.build()

    private fun handle(sender: CommandSender, target: Player, statisticName: String, typeName: String?) {
        val statistic = runCatching {
            Statistic.valueOf(statisticName.normalize().uppercase())
        }.getOrElse {
            throw Defaults.CmdException(Component.text("No statistic was found"))
        }

        val value = when (statistic.type) {
            Statistic.Type.UNTYPED -> target.getStatistic(statistic)

            Statistic.Type.BLOCK, Statistic.Type.ITEM -> {
                val material = resolveMaterial(typeName) ?: run { throw Defaults.CmdException("Please insert a valid block/item!".asFancyString()) }

                target.getStatistic(statistic, material)
            }

            Statistic.Type.ENTITY -> {
                val entity = resolveEntity(typeName) ?: run { throw Defaults.CmdException("Please insert a valid entity!".asFancyString()) }

                target.getStatistic(statistic, entity)
            }
        }

        sender.sendMessage(Defaults.neutral(Component.empty().append(target.name().color(Defaults.YELLOW)).append(" has ".asFancyString()).append(Component.text(statistic.key().value()).color(Defaults.GREEN)).append(" set to ".asFancyString()).append(Component.text(value).color(Defaults.YELLOW))))
    }

    private fun String.normalize() = this.trim().replace('-', '_').replace(' ', '_')

    private fun resolveMaterial(input: String?): Material? {
        if (input == null) return null

        val normalized = input.normalize()

        return Material.matchMaterial(normalized) ?: runCatching { Material.valueOf(normalized.uppercase()) }.getOrNull()
    }

    private fun resolveEntity(input: String?): EntityType? {
        if (input == null) return null

        return runCatching { EntityType.valueOf(input.normalize().uppercase()) }.getOrNull()
    }
}