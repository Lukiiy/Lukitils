package me.lukiiy.utils;

import me.lukiiy.utils.cmd.Invulnerability;
import me.lukiiy.utils.cmd.Vanish;
import me.lukiiy.utils.help.MassEffect;
import me.lukiiy.utils.idk.Equip;
import me.lukiiy.utils.idk.LukiMassEffects;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Lukitils extends JavaPlugin {
    private final Map<String, MassEffect> massEffects = new HashMap<>();

    @Override
    public void onEnable() {
        setupConfig();
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(Invulnerability.INSTANCE, this);
        pm.registerEvents(Vanish.INSTANCE, this);
        pm.registerEvents(new Equip(), this);

        LukiMassEffects.INSTANCE.init();
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

    // API?
    public Map<String, MassEffect> getMassEffects() {
        return Collections.unmodifiableMap(massEffects);
    }

    public void addMassEffect(MassEffect effect) {
        massEffects.put(effect.id().toLowerCase(), effect);
    }

    @ApiStatus.Experimental
    public void removeMassEffect(String id) {
        massEffects.remove(id);
    }
}
