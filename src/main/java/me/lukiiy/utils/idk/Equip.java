package me.lukiiy.utils.idk;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.lukiiy.utils.help.EquipView;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Equip implements Listener { // todo
    private static final Map<Player, EquipView> tracker = new ConcurrentHashMap<>();

    public static EquipView getView(Player toView, Player first) {
        return tracker.computeIfAbsent(toView, p -> {
            EquipView view = new EquipView(p);
            view.addViewer(first);
            return view;
        });
    }

    public static boolean isWatching(Player watcher) {
        return watcher.getOpenInventory().getTopInventory().getHolder(false) instanceof EquipView;
    }

    public static void removeViewer(Player watcher) {
        tracker.values().removeIf(view -> {
            view.removeViewer(watcher);
            if (view.getViewers().isEmpty()) {
                removeFromWatch(view.getPlayer());
                return true;
            }
            return false;
        });
    }

    public static void removeFromWatch(Player player) {
        EquipView view = tracker.remove(player);
        if (view == null) return;
        view.getViewers().forEach(p -> {
            if (isWatching(p)) p.closeInventory();
        });
    }

    public static boolean isBeingWatched(Player player) {
        return tracker.containsKey(player);
    }

    public static void updateWatchers(Player player) {
        EquipView view = tracker.get(player);
        if (view != null) view.load();
    }

    // Listeners
    @EventHandler(ignoreCancelled = true)
    public void equipmentDmg(PlayerItemDamageEvent e) {
        Player p = e.getPlayer();
        if (!isBeingWatched(p)) return;
        ItemStack item = e.getItem();
        if (item.isEmpty() || item.getItemMeta().isUnbreakable()) return;
        updateWatchers(p);
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (isBeingWatched(p)) removeFromWatch(p);
        if (isWatching(p)) removeViewer(p);
    }

    @EventHandler
    public void invClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        if (isBeingWatched(p)) removeFromWatch(p);
        if (isWatching(p)) removeViewer(p);
    }

    @EventHandler(ignoreCancelled = true)
    public void armorChange(PlayerArmorChangeEvent e) {
        Player p = e.getPlayer();
        if (!isBeingWatched(p)) return;
        updateWatchers(p);
    }

    @EventHandler(ignoreCancelled = true)
    public void invClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        Inventory inv = e.getClickedInventory();
        if (inv == null) return;

        if (isBeingWatched(p) && inv == p.getInventory() && e.getSlotType() == InventoryType.SlotType.ARMOR) updateWatchers(p);

        if (inv.getHolder(false) instanceof EquipView eV) {
            Player watched = eV.getPlayer();
            EntityEquipment equip = watched.getEquipment();

            switch (e.getSlot()) {
                case 0 -> equip.setHelmet(e.getCursor());
                case 1 -> equip.setChestplate(e.getCursor());
                case 2 -> equip.setLeggings(e.getCursor());
                case 3 -> equip.setBoots(e.getCursor());
                default -> {
                    e.setCancelled(true);
                    return;
                }
            }

            e.setCancelled(true);

            ItemStack current = e.getCurrentItem();
            p.setItemOnCursor(current != null && !p.getItemOnCursor().isSimilar(current) ? current : ItemStack.empty());
            updateWatchers(watched);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void invDrag(InventoryDragEvent e) {
        Player p = (Player) e.getWhoClicked();

        Inventory inv = e.getInventory();
        if (isBeingWatched(p) && inv == p.getInventory()) updateWatchers(p);

        if (inv.getHolder(false) instanceof EquipView) e.setCancelled(true);
    }
}