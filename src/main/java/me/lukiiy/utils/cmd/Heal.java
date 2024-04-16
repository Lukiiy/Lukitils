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
        Player target;

        if (!(commandSender instanceof Player)) {
            if (strings.length < 1) {
                commandSender.sendMessage(main.argsErrorMsg);
                return true;
            }
            target = Bukkit.getPlayer(strings[0]);
        } else target = PlayerHelper.getCommandTarget((Player) commandSender, strings);

        if (target == null) {
            commandSender.sendMessage(main.notFoundMsg);
            return true;
        }

        double value = 20;
        if (strings.length > 1) {
            try {
                value = Double.parseDouble(strings[1]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage(Presets.Companion.warnMsg("Invalid number!"));
                return true;
            }
        } else {
            AttributeInstance attribute = target.getAttribute(Attribute.GENERIC_MAX_HEALTH);
            if (attribute != null) value = attribute.getValue();
        }
        if (value <= 0) {
            commandSender.sendMessage(Presets.Companion.warnMsg("Must be a positive value greater than 0."));
            return true;
        }
        target.setHealth(value);

        for (PotionEffect effect : target.getActivePotionEffects()) {
            PotionEffectType type = effect.getType();
            if (!(type.getEffectCategory() == PotionEffectType.Category.BENEFICIAL)) target.removePotionEffect(type);
        }

        Component message = Presets.Companion.msg("Healed ");

        if (target != commandSender) {
            commandSender.sendMessage(message.append(target.name().color(Presets.Companion.getACCENT_NEUTRAL())));
            message = message.append(Presets.Companion.why("(by " + commandSender.getName() + ")"));
        }

        target.sendMessage(message);
        return true;
    }
}
