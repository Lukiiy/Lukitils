package me.lukiiy.utils.cool

import org.bukkit.Location
import org.bukkit.block.Block
import kotlin.math.max
import kotlin.math.min

object Builds {
    fun getBlocks(pos1: Location, pos2: Location): Set<Block> {
        val b: MutableSet<Block> = HashSet()
        val w = pos1.world

        val minX = min(pos1.blockX, pos2.blockX)
        val minY = min(pos1.blockY, pos2.blockY)
        val minZ = min(pos1.blockZ, pos2.blockZ)

        val maxX = max(pos1.blockX, pos2.blockX)
        val maxY = max(pos1.blockY, pos2.blockY)
        val maxZ = max(pos1.blockZ, pos2.blockZ)

        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    b.add(w.getBlockAt(x, y, z))
                }
            }
        }
        return b
    }
}
