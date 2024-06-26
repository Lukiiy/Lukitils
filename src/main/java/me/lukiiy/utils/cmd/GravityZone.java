package me.lukiiy.utils.cmd;

import me.lukiiy.utils.cool.Builds;
import me.lukiiy.utils.cool.Presets;
import me.lukiiy.utils.main;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BlockInventoryHolder;
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
        int area = 3;
        if (strings.length > 0) {
            try {area = Integer.parseInt(strings[0]);}
            catch (NumberFormatException ignored) {
                commandSender.sendMessage(Presets.Companion.msg("Not a number!"));
                return true;
            }
        }
        if (area < 1) area = 1;
        if (area > 25) area = 25;

        World w = block.getWorld();
        Set<Block> blocks = Builds.INSTANCE.getBlocksPerf(block.getLocation().add(-area, 0, -area), block.getLocation().add(area, area * 2, area));
        int size = blocks.size();
        for (Block b : blocks) {
            if (b.getState() instanceof BlockInventoryHolder) {
                b.breakNaturally(true);
                continue;
            }
            w.spawn(b.getLocation().toCenterLocation(), FallingBlock.class, f -> {
                f.setBlockData(b.getBlockData());
                f.setBlockState(b.getState());
                f.setDropItem(true);
            });
            b.setType(Material.AIR);
        }

        commandSender.sendMessage(Presets.Companion.msg("Spawning " + size + " falling blocks..."));
        return true;
    }
}
