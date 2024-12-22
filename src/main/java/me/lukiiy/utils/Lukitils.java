package me.lukiiy.utils;

import me.lukiiy.utils.cmd.Invulnerability;
import me.lukiiy.utils.cmd.Vanish;
import me.lukiiy.utils.idk.Equip;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Lukitils extends JavaPlugin {
    private static Lukitils instance;
    public static Lukitils getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(Invulnerability.INSTANCE, this);
        pm.registerEvents(Vanish.INSTANCE, this);
        pm.registerEvents(new Equip(), this);
    }
}
