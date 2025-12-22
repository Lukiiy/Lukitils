package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Utils.asPermission
import me.lukiiy.utils.help.Utils.getPlayerOrThrow
import me.lukiiy.utils.idk.Equip
import net.kyori.adventure.text.Component
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Listener

object InventoryViewer : Listener {
    private val reqInvsee: (CommandSender) -> Boolean = { it.hasPermission("invsee".asPermission()) }
    private val reqEChest: (CommandSender) -> Boolean = { it.hasPermission("echest".asPermission()) }

    fun registerInv(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("invsee")
            .requires { reqInvsee(it.sender) }
            .then(Commands.argument("player", ArgumentTypes.player())
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NOT_FOUND
                val target = it.getPlayerOrThrow("player")

                handle(sender, target, { sender, target -> sender.openInventory(target.inventory) }, "inventory")
                Command.SINGLE_SUCCESS
            })
        .build()
    }

    fun registerEChest(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("echest")
            .requires { reqEChest(it.sender) }
            .then(Commands.argument("player", ArgumentTypes.player())
                .executes {
                    val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER
                    val target = it.getPlayerOrThrow("player")

                    handle(sender, target, {s, t -> s.openInventory(t.enderChest)}, "ender chest")
                    Command.SINGLE_SUCCESS
                })
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER

                handle(sender, sender, {s, t -> s.openInventory(t.enderChest)}, "ender chest")
                Command.SINGLE_SUCCESS
            }
        .build()
    }

    fun registerEquip(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("equipsee")
            .requires { reqInvsee(it.sender) }
            .then(Commands.argument("player", ArgumentTypes.player())
                .executes {
                    val sender = it.source.sender as? Player ?: throw Defaults.NON_PLAYER
                    val target = it.getPlayerOrThrow("player")

                    handle(sender, target, {s, t -> s.openInventory(Equip.getView(t, s).inventory)}, "equipment")
                    Command.SINGLE_SUCCESS
                })
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NOT_FOUND

                handle(sender, sender, {s, t -> s.openInventory(Equip.getView(t, s).inventory)}, "equipment")
                Command.SINGLE_SUCCESS
            }
        .build()
    }

    private fun handle(sender: Player, target: Player, act: (Player, Player) -> Unit, actDesc: String) {
        sender.sendMessage(Defaults.neutral(Component.text("Inspecting ").append(target.name().color(Defaults.YELLOW)).append(Component.text("'s $actDesc"))))
        act(sender, target)
    }
}
