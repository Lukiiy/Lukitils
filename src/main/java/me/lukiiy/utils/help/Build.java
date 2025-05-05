package me.lukiiy.utils.help;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Build {
    public static @NotNull Set<Block> getBlocks(@NotNull Location pos1, @NotNull Location pos2, @Nullable Predicate<Block> filter) {
        World world = pos1.getWorld();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockX(), pos2.getBlockX());

        Stream<Block> stream = IntStream.rangeClosed(minX, maxX).boxed()
                .flatMap(x -> IntStream.rangeClosed(minY, maxY).boxed()
                        .flatMap(y -> IntStream.rangeClosed(minZ, maxZ)
                                .mapToObj(z -> world.getBlockAt(x, y, z))));

        return stream.parallel().filter(filter == null ? b -> true : filter).collect(Collectors.toSet());
    }

    public static @NotNull Set<Block> getBlocks(@NotNull Location pos1, @NotNull Location pos2) {
        return getBlocks(pos1, pos2, null);
    }
}
