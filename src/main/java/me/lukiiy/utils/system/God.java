package me.lukiiy.utils.system;

import me.lukiiy.utils.cool.PlayerHelper;
import me.lukiiy.utils.cool.Presets;
import me.lukiiy.utils.main;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class God implements CommandExecutor, Listener {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player) && strings.length == 0) {
            commandSender.sendMessage(main.argsErrorMsg);
            return true;
        }
        Player target = strings.length > 0 ? Bukkit.getPlayer(strings[0]) : (Player) commandSender;
        if (target == null) {
            commandSender.sendMessage(main.notFoundMsg);
            return true;
        }

        boolean invulnerable = target.isInvulnerable();
        Component state = invulnerable ? main.OFF : main.ON;

        Component message = Presets.Companion.msg("God mode is now ").append(state);

        if (target != commandSender) {
            commandSender.sendMessage(message.append(Presets.Companion.why(" for ")).append(target.name().color(Presets.Companion.getACCENT_NEUTRAL())));
            message = message.append(Presets.Companion.why("(by ").append(commandSender.name().color(Presets.Companion.getACCENT_NEUTRAL())).append(Presets.Companion.why(")")));
        }

        target.setInvulnerable(!invulnerable);
        target.sendMessage(message);
        return true;
    }

    @EventHandler
    public void dmg(EntityDamageEvent e) { // this is for void dmg and gamemode dmg
        if (!(e.getEntity() instanceof Player)) return;
        if (e.getEntity().isInvulnerable()) e.setCancelled(true);
    }
}
