package me.lukiiy.utils.cmd;

import me.lukiiy.utils.cool.Builds;
import me.lukiiy.utils.cool.Presets;
import me.lukiiy.utils.main;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class GravityZone implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player p)) {
            commandSender.sendMessage(main.nonPlayerMsg);
            return true;
        }

        Block block = p.getTargetBlockExact(32);
        if (block == null) {
            commandSender.sendMessage(Presets.Companion.warnMsg("No block detected!"));
            return true;
        }

        World w = block.getWorld();
        Set<Block> blocks = Builds.INSTANCE.getBlocks(block.getLocation().add(-3, 0, -3), block.getLocation().add(3, 6, 3));
        for (Block b : blocks) {
            if (!b.getType().isCollidable()) continue;
            FallingBlock f = w.spawn(b.getLocation(), FallingBlock.class, entity -> {
                entity.setBlockData(b.getBlockData());
                entity.setCancelDrop(true);
            });
            b.breakNaturally(false);
        }

        commandSender.sendMessage(Presets.Companion.msg("Spawning falling blocks..."));
        return true;
    }
}
