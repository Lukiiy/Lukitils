package me.lukiiy.utils.help;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EquipView implements InventoryHolder {
    private final Inventory inv;
    private final Player p;
    private final Set<Player> viewers = ConcurrentHashMap.newKeySet();

    public EquipView(Player p) {
        this.inv = Bukkit.createInventory(this, InventoryType.HOPPER, p.name().append(Component.text("'s Equipment")));
        this.p = p;
        load();
    }

    public void load() {
        EntityEquipment equipment = p.getEquipment();

        inv.setItem(0, equipment.getHelmet().clone());
        inv.setItem(1, equipment.getChestplate().clone());
        inv.setItem(2, equipment.getLeggings().clone());
        inv.setItem(3, equipment.getBoots().clone());
        inv.setItem(4, equipment.getItemInOffHand().clone());

        viewers.forEach(Player::updateInventory);
    }

    public Set<Player> getViewers() {
        return viewers;
    }

    public void addViewer(Player viewer) {
        viewers.add(viewer);
    }

    public void removeViewer(Player viewer) {
        viewers.remove(viewer);
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inv;
    }

    @NotNull
    public Player getPlayer() {
        return p;
    }
}