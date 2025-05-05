package me.lukiiy.utils;

import me.lukiiy.utils.cmd.Invulnerability;
import me.lukiiy.utils.cmd.Vanish;
import me.lukiiy.utils.idk.Equip;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Lukitils extends JavaPlugin {

    @Override
    public void onEnable() {
        setupConfig();
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(Invulnerability.INSTANCE, this);
        pm.registerEvents(Vanish.INSTANCE, this);
        pm.registerEvents(new Equip(), this);
    }

    public static Lukitils getInstance() {
        return JavaPlugin.getPlugin(Lukitils.class);
    }

    // Config
    public void setupConfig() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
