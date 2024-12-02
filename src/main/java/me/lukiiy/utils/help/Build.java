package me.lukiiy.utils.help;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class Build {
    public static Set<Block> getBlocks(Location pos1, Location pos2, Predicate<Block> filter) {
        World world = pos1.getWorld();
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        int max = (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
        Set<Block> blocks = new HashSet<>(max);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (filter.test(block)) blocks.add(block);
                }
            }
        }

        return blocks;
    }

    public static Set<Block> getBlocks(Location pos1, Location pos2) {return getBlocks(pos1, pos2, b -> true);}
}
