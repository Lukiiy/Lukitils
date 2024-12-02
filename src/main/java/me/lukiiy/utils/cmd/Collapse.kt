package me.lukiiy.utils.cmd

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import me.lukiiy.utils.Defaults
import me.lukiiy.utils.help.Build
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.FallingBlock
import org.bukkit.entity.Player
import org.bukkit.inventory.BlockInventoryHolder

object Collapse {
    private val detectionRange = 32
    private val noBlock = Defaults.CUSTOM_ERR("Expected target within a distance of $detectionRange blocks.")

    fun register(): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal("collapse").requires { it.sender.hasPermission("lukitils.collapse") }
            .then(Commands.argument("radius", IntegerArgumentType.integer(1))
                .executes {
                    val sender = it.source.sender as? Player ?: throw Defaults.NOT_FOUND.create();
                    val block = sender.getTargetBlockExact(detectionRange) ?: throw noBlock.create()

                    handle(sender, block, IntegerArgumentType.getInteger(it, "radius"))
                    Command.SINGLE_SUCCESS
                })
            .executes {
                val sender = it.source.sender as? Player ?: throw Defaults.NOT_FOUND.create()
                val block = sender.getTargetBlockExact(detectionRange) ?: throw noBlock.create()

                handle(sender, block, 3)
                Command.SINGLE_SUCCESS
            }
            .build()
    }

    private fun handle(player: Player, block: Block, area: Int) {
        val world = block.world
        val blocks = Build.getBlocks (
            block.location.add((-area).toDouble(), 0.0, (-area).toDouble()),
            block.location.add(area.toDouble(), (area * 2).toDouble(), area.toDouble())
        ) { b -> !b.type.isAir && !b.isReplaceable }

        blocks.forEach { b: Block ->
            if (b.state is BlockInventoryHolder) {
                b.breakNaturally(true)
                return@forEach
            }

            world.spawn(b.location.add(.5, 0.0, 0.5), FallingBlock::class.java) {
                it.blockData = b.blockData
                it.blockState = b.state
                it.dropItem = true
            }
            b.type = Material.AIR
        }

        player.sendMessage(Defaults.msg(Component.text("Spawning ${blocks.size} falling blocks...")))
    }
}