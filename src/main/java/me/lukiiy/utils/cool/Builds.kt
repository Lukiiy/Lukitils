package me.lukiiy.utils.cool

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import java.util.*
import kotlin.math.max
import kotlin.math.min

object Builds {
    private fun getBlocks(pos1: Location, pos2: Location, filter: (Block) -> Boolean = {true}): Set<Block> {
        val b = mutableSetOf<Block>()
        val w = pos1.world
        val minX = min(pos1.blockX, pos2.blockX)
        val minY = min(pos1.blockY, pos2.blockY)
        val minZ = min(pos1.blockZ, pos2.blockZ)
        val maxX = max(pos1.blockX, pos2.blockX)
        val maxY = max(pos1.blockY, pos2.blockY)
        val maxZ = max(pos1.blockZ, pos2.blockZ)

        val seq = sequence {
            for (x in minX..maxX) {
                for (y in minY..maxY) {
                    for (z in minZ..maxZ) {
                        yield(w.getBlockAt(x, y, z))
                    }
                }
            }
        }

        seq.filter(filter).toCollection(b)
        return b
    }

    fun getBlocksPerf(pos1: Location, pos2: Location): Set<Block> {
        return getBlocks(pos1, pos2) {block -> !block.type.isAir && !block.isReplaceable}
    }
}
