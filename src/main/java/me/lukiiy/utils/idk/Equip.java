package me.lukiiy.utils.idk;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.lukiiy.utils.Lukitils;
import me.lukiiy.utils.help.EquipView;
import org.bukkit.Bukkit;
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

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Equip implements Listener {
    private static final Map<Player, EquipView> TRACKER = new ConcurrentHashMap<>();

    public static EquipView getView(Player target, Player viewer) {
        EquipView view = TRACKER.computeIfAbsent(target, EquipView::new);

        view.addViewer(viewer);
        return view;
    }

    public static boolean isWatching(Player watcher) {
        return watcher.getOpenInventory().getTopInventory().getHolder(false) instanceof EquipView;
    }

    public static void removeViewer(Player watcher) {
        TRACKER.values().removeIf(view -> {
            view.removeViewer(watcher);

            if (view.getViewers().isEmpty()) {
                removeViewer(view.getPlayer());
                return true;
            }

            return false;
        });
    }

    public static void stopWatching(Player target) {
        EquipView view = TRACKER.remove(target);
        if (view == null) return;

        view.getViewers().forEach(viewer -> {
            if (viewer.getOpenInventory().getTopInventory() == view.getInventory()) viewer.closeInventory();
        });
    }

    public static boolean isBeingWatched(Player target) {
        return TRACKER.containsKey(target);
    }

    public static void updateView(Player target) {
        EquipView view = TRACKER.get(target);

        if (view != null) view.load();
    }

    @EventHandler
    public void quit(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        if (isBeingWatched(p)) stopWatching(p);
        if (isWatching(p)) removeViewer(p);
    }

    @EventHandler
    public void invClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        Inventory closed = e.getInventory();

        if (closed.getHolder(false) instanceof EquipView) removeViewer(p);
    }

    @EventHandler(ignoreCancelled = true)
    public void armorChange(PlayerArmorChangeEvent e) {
        if (isBeingWatched(e.getPlayer())) updateView(e.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void itemDamage(PlayerItemDamageEvent e) {
        Player p = e.getPlayer();
        if (!isBeingWatched(p)) return;

        ItemStack item = e.getItem();
        if (item.isEmpty() || item.getItemMeta().isUnbreakable()) return;

        Bukkit.getGlobalRegionScheduler().execute(Lukitils.getInstance(), () -> updateView(p));
    }

    @EventHandler(ignoreCancelled = true)
    public void invClick(InventoryClickEvent e) {
        Player viewer = (Player) e.getWhoClicked();
        Inventory clicked = e.getClickedInventory();
        if (clicked == null) return;

        if (isBeingWatched(viewer) && clicked == viewer.getInventory() && e.getSlotType() == InventoryType.SlotType.ARMOR) Bukkit.getGlobalRegionScheduler().execute(Lukitils.getInstance(), () -> updateView(viewer));

        if (clicked.getHolder(false) instanceof EquipView view) {
            e.setCancelled(true);

            Player target = view.getPlayer();
            EntityEquipment equip = target.getEquipment();
            ItemStack cursor = e.getCursor();
            ItemStack current = e.getCurrentItem();

            switch (e.getSlot()) {
                case 0 -> equip.setHelmet(cursor, true);
                case 1 -> equip.setChestplate(cursor, true);
                case 2 -> equip.setLeggings(cursor, true);
                case 3 -> equip.setBoots(cursor, true);
                default -> equip.setItemInOffHand(cursor, true);
            }

            viewer.setItemOnCursor(current == null ? ItemStack.empty() : current);
            updateView(target);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void invDrag(InventoryDragEvent e) {
        Inventory inv = e.getInventory();

        if (inv.getHolder(false) instanceof EquipView) {
            e.setCancelled(true);
            return;
        }

        Player p = (Player) e.getWhoClicked();
        if (isBeingWatched(p) && inv == p.getInventory()) Bukkit.getGlobalRegionScheduler().execute(Lukitils.getInstance(), () -> updateView(p));
    }
}