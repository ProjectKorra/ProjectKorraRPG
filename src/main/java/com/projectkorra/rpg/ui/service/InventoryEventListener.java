package com.projectkorra.rpg.ui.service;

import com.projectkorra.rpg.ui.InventoryUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

/**
 * Handle onClose and onClick to clean up the UI and Memory properly
 */
public class InventoryEventListener implements Listener {
    private final InventoryService service;

    public InventoryEventListener(InventoryService service) {
        this.service = service;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!((event.getWhoClicked()) instanceof Player player)) return;

        InventoryUI ui = service.get(player);

        if (ui != null && event.getInventory().getHolder() == ui) {
            event.setCancelled(true);
            ui.handleClick(event);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        InventoryUI ui = service.get(player);

        if (ui != null && event.getInventory().getHolder() == ui) {
            service.close(player);
        }
    }
}
