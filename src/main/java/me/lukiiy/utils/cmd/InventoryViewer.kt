package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import me.lukiiy.utils.Defaults
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

object InventoryViewer {
    fun registerInv(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("invsee").requires {it.sender.hasPermission("lukitils.invsee")}
            .then(Commands.argument("player", ArgumentTypes.player())
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NOT_FOUND.create()
                val target = it.getArgument("player", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().findFirst().orElse(null) ?: return@executes 0

                handle(sender, target, {sender, target -> sender.openInventory(target.inventory)}, "inventory")
                Command.SINGLE_SUCCESS
            })
        .build()
    }

    fun registerEChest(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("echest").requires {it.sender.hasPermission("lukitils.invsee")}
            .then(Commands.argument("player", ArgumentTypes.player())
                .executes {
                    val sender = it.source.sender as? Player ?: throw Defaults.NOT_FOUND.create()
                    val target = it.getArgument("player", PlayerSelectorArgumentResolver::class.java).resolve(it.source).stream().findFirst().orElse(null) ?: return@executes 0

                    handle(sender, target, {s, t -> s.openInventory(t.enderChest)}, "ender chest")
                    Command.SINGLE_SUCCESS
                })
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NOT_FOUND.create()

                handle(sender, sender, {s, t -> s.openInventory(t.enderChest)}, "ender chest")
                Command.SINGLE_SUCCESS
            }
        .build()
    }

    private fun handle(sender: Player, target: Player, act: (Player, Player) -> Unit, actDesc: String) {
        sender.sendMessage(Defaults.msg(Component.text("Opening ").append(target.name().color(Defaults.YELLOW)).append(Component.text("'s $actDesc"))))
        act(sender, target)
    }
}