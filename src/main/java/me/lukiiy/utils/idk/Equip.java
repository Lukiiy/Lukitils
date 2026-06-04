package me.lukiiy.utils.idk;

import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import me.lukiiy.utils.Lukitils;
import me.lukiiy.utils.help.EquipView;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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

            return view.getViewers().isEmpty();
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
        if (e.getInventory().getHolder(false) instanceof EquipView) removeViewer((Player) e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void equipChange(EntityEquipmentChangedEvent e) {
        if (e.getEntity() instanceof Player p && isBeingWatched(p)) updateView(p);
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
        Inventory clicked = e.getClickedInventory();
        if (clicked == null) return;

        if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && e.getView().getTopInventory().getHolder(false) instanceof EquipView) {
            e.setCancelled(true);
            return;
        }

        if (!(clicked.getHolder(false) instanceof EquipView view)) return;

        e.setCancelled(true);

        Player viewer = (Player) e.getWhoClicked();
        EntityEquipment equip = view.getPlayer().getEquipment();
        ItemStack cursor = e.getCursor();
        ItemStack current = e.getCurrentItem();

        switch (e.getSlot()) {
            case 0 -> equip.setHelmet(cursor, true);
            case 1 -> equip.setChestplate(cursor, true);
            case 2 -> equip.setLeggings(cursor, true);
            case 3 -> equip.setBoots(cursor, true);
            case 4 -> equip.setItemInOffHand(cursor, true);
            default -> {
                return;
            }
        }

        viewer.setItemOnCursor(current == null ? ItemStack.empty() : current);
    }

    @EventHandler(ignoreCancelled = true)
    public void invDrag(InventoryDragEvent e) {
        if (e.getInventory().getHolder(false) instanceof EquipView) e.setCancelled(true);
    }
}