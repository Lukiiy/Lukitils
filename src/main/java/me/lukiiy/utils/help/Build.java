package me.lukiiy.utils.help;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Build {
    public static Set<Block> getBlocks(Location pos1, Location pos2, Predicate<Block> filter) {
        World world = pos1.getWorld();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        return IntStream.rangeClosed(minX, maxX).boxed()
                .flatMap(x -> IntStream.rangeClosed(minY, maxY).boxed()
                        .flatMap(y -> IntStream.rangeClosed(minZ, maxZ).boxed()
                                .map(z -> world.getBlockAt(x, y, z))))
                .filter(filter != null ? filter : block -> true)
                .collect(Collectors.toSet());
    }

    public static @NotNull Set<Block> getBlocks(@NotNull Location pos1, @NotNull Location pos2) {
        return getBlocks(pos1, pos2, null);
    }
}
