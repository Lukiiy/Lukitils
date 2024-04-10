package me.lukiiy.utils;

import me.lukiiy.utils.cmd.*;
import me.lukiiy.utils.cool.Presets;
import me.lukiiy.utils.system.God;
import me.lukiiy.utils.system.Vanish;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class main extends JavaPlugin {
    public static main plugin;

    @Override
    public void onEnable() {
        plugin = this;

        getCommand("god").setExecutor(new God());
        getCommand("fly").setExecutor(new Fly());
        getCommand("broadcast").setExecutor(new Broadcast());
        getCommand("invsee").setExecutor(new GetInventory());
        getCommand("echest").setExecutor(new GetEChest());
        getCommand("ping").setExecutor(new Ping());
        getCommand("playerinfo").setExecutor(new PlayerInfo());
        getCommand("vanish").setExecutor(new Vanish());
        getCommand("displaytoast").setExecutor(new DisplayToast());
        getCommand("heal").setExecutor(new Heal());
        getCommand("feed").setExecutor(new Feed());
        getCommand("barelife").setExecutor(new BareLife());
        getCommand("gravity").setExecutor(new GravityZone());
        getCommand("vanishlist").setExecutor(new VanishList());
        getCommand("triggerscreen").setExecutor(new TriggerScreen());
        getCommand("ignite").setExecutor(new Ignite());

        PluginManager pl = getServer().getPluginManager();
        pl.registerEvents(new God(), this);
        pl.registerEvents(new Vanish(), this);
    }

    @Override
    public void onDisable() {
    }

    // Preset
    public static final Component notFoundMsg = Presets.Companion.msg("Player not found!");
    public static final Component nonPlayerMsg = Presets.Companion.warnMsg("You must be a player to do this!");
    public static final Component argsErrorMsg = Presets.Companion.warnMsg("Incomplete or invalid arguments!");

    public static final Component ON = Component.text("ᴏɴ").color(Presets.Companion.getACCENT_TRUE());
    public static final Component OFF = Component.text("ᴏꜰꜰ").color(Presets.Companion.getACCENT_FALSE());
}