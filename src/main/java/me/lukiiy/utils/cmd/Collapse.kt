package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Build
import me.lukiiy.utils.help.Utils
import me.lukiiy.utils.help.Utils.asFancyString
import me.lukiiy.utils.help.Utils.asPermission
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.FallingBlock
import org.bukkit.entity.Player
import org.bukkit.inventory.BlockInventoryHolder

object Collapse {
    private const val detectionRange = 64
    private val noBlock = Defaults.CmdException("Expected target within a distance of $detectionRange blocks.".asFancyString())

    private val main = Commands.literal("collapse")
        .requires { it.sender.hasPermission("collapse".asPermission()) }
        .executes {
            val sender = it.source.sender as? Player ?: throw Defaults.NOT_FOUND
            val block = sender.getTargetBlockExact(detectionRange) ?: throw noBlock

            handle(sender, block)
            Command.SINGLE_SUCCESS
        }
        .then(Commands.argument("radius", IntegerArgumentType.integer(1))
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NOT_FOUND
                val block = sender.getTargetBlockExact(detectionRange) ?: throw noBlock

                handle(sender, block, IntegerArgumentType.getInteger(it, "radius"))
                Command.SINGLE_SUCCESS
            })

    fun register(): LiteralCommandNode<CommandSourceStack> = main.build()

    private fun handle(player: Player, block: Block, area: Int = 3) {
        val world = block.world
        val blocks = Build.getBlocks(block.location.add((-area).toDouble(), 0.0, (-area).toDouble()), block.location.add(area.toDouble(), (area * 2).toDouble(), area.toDouble())) { b -> !b.isEmpty && !b.isReplaceable }

        blocks.forEach { b ->
            if (b.state is BlockInventoryHolder) {
                b.breakNaturally(true)
                return@forEach
            }

            world.spawn(b.location.toCenterLocation(), FallingBlock::class.java) {
                it.blockData = b.blockData
                it.blockState = b.state
                it.dropItem = true
            }

            b.type = Material.AIR
        }

        Utils.adminCmdFeedback(player, "Collapsed ${blocks.size} blocks")
        player.sendMessage(Defaults.neutral("Collapsing ${blocks.size} blocks...".asFancyString()))

    }
}