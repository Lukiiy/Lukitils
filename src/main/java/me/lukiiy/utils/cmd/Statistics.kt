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
import me.lukiiy.utils.help.Utils.suggestFiltered
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
                    builder.suggestFiltered(Statistic.entries) { name.lowercase() }
                    builder.buildFuture()
                }
                .then(Commands.argument("type", StringArgumentType.word())
                    .suggests { ctx, builder ->
                        val statistic = runCatching {
                            Statistic.valueOf(StringArgumentType.getString(ctx, "statistic").normalize().uppercase())
                        }.getOrNull()

                        when (statistic?.type) {
                            Statistic.Type.BLOCK, Statistic.Type.ITEM -> builder.suggestFiltered(Material.entries.filter { !it.isLegacy }) { name.lowercase() }
                            Statistic.Type.ENTITY -> builder.suggestFiltered(EntityType.entries) { name.lowercase() }
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
            Statistic.Type.UNTYPED -> target.getStatistic(statistic) to null

            Statistic.Type.BLOCK, Statistic.Type.ITEM -> {
                val material = resolveMaterial(typeName) ?: throw Defaults.CmdException("Please insert a valid block/item!".asFancyString())

                target.getStatistic(statistic, material) to material.name.lowercase()
            }

            Statistic.Type.ENTITY -> {
                val entity = resolveEntity(typeName) ?: throw Defaults.CmdException("Please insert a valid entity!".asFancyString())

                target.getStatistic(statistic, entity) to entity.name.lowercase()
            }
        }

        val (stat, param) = value

        val msg = Component.empty().append(target.name().color(Defaults.YELLOW)).append(" has ".asFancyString())
            .append(Component.translatable("stat.minecraft.${statistic.key().value()}", statistic.key().value().uppercase()).color(Defaults.GREEN))
            .run { if (param != null) append(" (${param})".asFancyString()) else this }
            .append(" set to ".asFancyString())
            .append(Component.text(stat).color(Defaults.YELLOW))

        sender.sendMessage(Defaults.neutral(msg))
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