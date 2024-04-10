package me.lukiiy.utils.cmd;

import me.lukiiy.utils.cool.PlayerHelper;
import me.lukiiy.utils.cool.Presets;
import me.lukiiy.utils.main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerInfo implements CommandExecutor {
    // todo: it works... ig

    final TextColor section = TextColor.color(0xfff8de);
    final Component prefix = Component.text(" • ").color(Presets.Companion.getPRIMARY()); // Can be removed because it is only used once

    private Component info(String key, String value) {
        return prefix.append(Component.text(key + ": ").color(Presets.Companion.getACCENT_NEUTRAL()).append(Component.text(value).color(Presets.Companion.getPRIMARY())));
    }

    private Component info(String key, Component value) {
        return prefix.append(Component.text(key + ": ").color(Presets.Companion.getACCENT_NEUTRAL()).append(value.color(Presets.Companion.getPRIMARY())));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage(main.argsErrorMsg);
            return true;
        }

        Player target = Bukkit.getPlayer(strings[0]);
        if (target == null) {
            commandSender.sendMessage(main.notFoundMsg);
            return true;
        }

        commandSender.sendMessage(Presets.Companion.msg("Information from ")
                .append(target.name().color(Presets.Companion.getACCENT_NEUTRAL()))
                .append(Component.text(":").color(Presets.Companion.getSECONDARY())));

        Location pos = target.getLocation();
        AttributeInstance maxHp = target.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        Location spawn = PlayerHelper.getSpawnLocation(target);
        Location death = target.getLastDeathLocation();

        Component uuid = Component.text(target.getUniqueId().toString()).color(NamedTextColor.GREEN)
                .hoverEvent(HoverEvent.showText(Component.text("Click to copy!")
                        .clickEvent(ClickEvent.copyToClipboard(target.getUniqueId().toString()))));

        commandSender.sendMessage(Component.text(" Player Stats").color(section));
        commandSender.sendMessage(info("HP", target.getHealth() + (maxHp != null ? "/" + Math.floor(maxHp.getValue()) : "")));
        commandSender.sendMessage(info("Hunger", String.valueOf(target.getFoodLevel())));
        commandSender.sendMessage(info("Level", String.valueOf(target.getLevel())));

        commandSender.sendMessage(Component.text(" Saved Locations").color(section));
        commandSender.sendMessage(info("Current", PlayerHelper.getLocationComponent(pos)));
        commandSender.sendMessage(info("Spawn", PlayerHelper.getLocationComponent(spawn)));
        if (death != null) commandSender.sendMessage(info("Death", PlayerHelper.getLocationComponent(death)));

        commandSender.sendMessage(Component.text(" Conditions").color(section));
        commandSender.sendMessage(info("Gamemode", target.getGameMode().toString()));
        commandSender.sendMessage(info("Fly", String.valueOf(target.getAllowFlight())));
        commandSender.sendMessage(info("God", String.valueOf(target.isInvulnerable())));

        commandSender.sendMessage(Component.text(" Technical").color(section));
        commandSender.sendMessage(info("UUID", uuid));
        commandSender.sendMessage(info("Protoc. Version", String.valueOf(target.getProtocolVersion())));
        commandSender.sendMessage(info("Ping", String.valueOf(target.getPing())));
        return true;
    }
}
