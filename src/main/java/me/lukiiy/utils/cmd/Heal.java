package me.lukiiy.utils.cmd;

import me.lukiiy.utils.cool.PlayerHelper;
import me.lukiiy.utils.cool.Presets;
import me.lukiiy.utils.main;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class Heal implements CommandExecutor {
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

        double value = 20;
        AttributeInstance maxHP = target.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHP != null) value = maxHP.getBaseValue();
        if (strings.length > 1) {
            try {value = Double.parseDouble(strings[1]);}
            catch (NumberFormatException ignored) {}
        }
        if (value <= 0) value = 1;
        target.heal(value);

        for (PotionEffect effect : target.getActivePotionEffects()) {
            PotionEffectType type = effect.getType();
            if (!(type.getEffectCategory() == PotionEffectType.Category.BENEFICIAL)) target.removePotionEffect(type);
        }

        Component message = Presets.Companion.msg("Healed ");
        if (target != commandSender) {
            commandSender.sendMessage(message.append(target.name().color(Presets.Companion.getACCENT_NEUTRAL())));
            message = message.append(Presets.Companion.why("(by ").append(commandSender.name().color(Presets.Companion.getACCENT_NEUTRAL())).append(Presets.Companion.why(")")));
        }
        target.sendMessage(message);
        return true;
    }
}
